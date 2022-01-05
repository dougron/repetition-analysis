package gui_objects.content_clip_and_chord_panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import DataObjects.ableton_live_clip.LiveClip;
import GUIObjects.ClipAndChordBrowser;
import GUIObjects.FileBrowserPanel;
import GUIObjects.FileBrowserParent;
import gui_objects.ColourUpdateParent;
import gui_objects.cell_length_panel.CellLengthCheckBoxPanel;
import gui_objects.cell_length_panel.CellLengthPanelParent;
import gui_objects.main_ui.RepetitionAnalysisGameFrame;
import gui_objects.main_ui.RepetitionAnalysisMainFrame;
import gui_objects.main_ui.RepetitionAnalysisParent;
import gui_objects.repeat_schema_panel.MasterRepeatSchemaGamePanel;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.repeat_schema_list.SchemaListObject;

public class ContentClipAndChordBrowser extends JPanel implements FileBrowserParent, CellLengthPanelParent{

	RepetitionAnalysisParent parent;
//	private String clipDialogTitle = "Select a melody clip....";
//	private String chordsDialogTitle = "Select a chord clip....";
//	private String[] clipFilter = new String[]{"liveclip"};
//	private String[] chordsFilter = new String[]{"chords.liveclip"};
	private String clipSavePath = "src/resources/file_browser_data/contentClipList.data";
	private String chordsSavePath = "src/resources/file_browser_data/contentChordsList.data";
	private int listLength = 15;
	private String clipPath;// = "D:/Documents/repos/CorpusMelodyFiles";
	private String chordsPath;// = "D:/Documents/repos/ChordProgressionTestFiles";
	private ClipAndChordBrowser cncBrowser;
//	private JComboBox cellLengthBox;
//	private int[] cellLengthOptions = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};	// no theoretical upper limit, 1 may actuall be an option that would need to be tested
	private int DEFAULT_CELL_LENGTH = 3;		// default value
//	private DefaultListModel masterSelectionItems = null;
//	private JList masterSelectionList;
	private static final int DEFAULT_CELL_LENGTH_INDEX = 3;
	private RepetitionAnalysis ra = null;
//	private FeatureSortPanel sortPanel;
//	private ArrayList<FeatureChunkRepeatSchema> masterRepeatDisplayList;
	private MasterRepeatSchemaGamePanel masterPanel;
	private JCheckBox linkCheckBox;
	private ContentClipAndChordBrowserParent contentParent;
	private CellLengthCheckBoxPanel lengthSelectionPanel;
	private ColourUpdateParent colourParent;
	
	
	public ContentClipAndChordBrowser(RepetitionAnalysisParent parent, MasterRepeatSchemaGamePanel masterPanel, ContentClipAndChordBrowserParent contentParent){
		setPathsFromPropertiesFile();		
		this.parent = parent;
		this.masterPanel = masterPanel;
		this.contentParent = contentParent;
		this.colourParent = (ColourUpdateParent) parent;
		makeLayout();
		makeHeader();
		makeClipAndChordBrowser();
//		makeCellLength();
		makeLengthSelectionPanel();
		makeLinkCheckBox();
		updateBackgroundColor();
	}
	private void setPathsFromPropertiesFile (){
		try (InputStream input = new FileInputStream(RepetitionAnalysisGameFrame.propertiesPath))
		{
			Properties props = new Properties();
			props.load(input);
			clipPath = props.getProperty("default_melody_directory");
			chordsPath = props.getProperty("default_chords_directory");
		}
		catch (IOException ex)
		{
			
		}	
	}

//	public int cellLength(){
//		return cellLength;
//	}
	public ArrayList<Integer> cellLengthIndexList(){
		return lengthSelectionPanel.selectedIndexList();
	}
	public void setClipFile(String path){
		cncBrowser.setSelectedFile(path);
	}
	public void setChordsFile(String path){
		cncBrowser.setChordsFile(path);
	}
//	public void setCellLengthIndex(int i){
//		cellLength = i;
//		cellLengthBox.setSelectedIndex(i);
//	}
	public void setCellLengthIndexList(ArrayList<Integer> list){
		lengthSelectionPanel.setLengthCheckBoxes(list);
	}
	public void addSelectionIndex(int i){
		lengthSelectionPanel.addSelectedIndex(i);
	}
	public boolean isLinkedToMasterPanel() {
		return linkCheckBox.isSelected();
	}
	
	

// privates ----------------------------------------------------------------------------------------- 
	private void makeLengthSelectionPanel() {
		lengthSelectionPanel = new CellLengthCheckBoxPanel(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, this);
		lengthSelectionPanel.addSelectedIndex(DEFAULT_CELL_LENGTH_INDEX);
		GridBagConstraints gbc_cellLengthPanel = getGridBagConstraints(0, 2);
		add(lengthSelectionPanel, gbc_cellLengthPanel);
	}
	private void makeHeader() {
		JLabel label = new JLabel("content selection panel");
		GridBagConstraints gbc_label = getGridBagConstraints(0, 0);
		add(label, gbc_label);
	}
	private void makeLinkCheckBox() {
		linkCheckBox = new JCheckBox("Link to master");
		GridBagConstraints gbc_linkBox = getGridBagConstraints(0, 3);
		linkCheckBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (linkCheckBox.isSelected()){
					updateFromMasterPanel();
					cncBrowser.setCombosEnabled(false);
					lengthSelectionPanel.setEnabled(false);
				} else {
					cncBrowser.setCombosEnabled(true);
					lengthSelectionPanel.setEnabled(true);
				}
				
			}
			
		});
		linkCheckBox.setOpaque(false);
//		gbc_linkBox.gridwidth = 2;
//		gbc_linkBox.insets = new Insets(0, 0, 5, 5);
//		gbc_linkBox.fill = GridBagConstraints.HORIZONTAL;
		add(linkCheckBox, gbc_linkBox);
	}
	protected void updateFromMasterPanel() {
		cncBrowser.setFile(masterPanel.getSelectedFilePath());
		cncBrowser.setChordsFile(masterPanel.getSelectedChordsPath());
		lengthSelectionPanel.setLengthCheckBoxes(masterPanel.cellLengthSelections());
	}

	private void makeLayout() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{100, 0, 0};
		gridBagLayout.rowHeights = new int[]{20, 20, 20, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
	}
	private void makeClipAndChordBrowser(){
		cncBrowser = new ClipAndChordBrowser(this, FileBrowserPanel.BUTTONS_BELOW, clipPath, chordsPath, clipSavePath, chordsSavePath, listLength);
		GridBagConstraints gbc_cncBrowser = getGridBagConstraints(0, 1);
		gbc_cncBrowser.gridwidth = 2;
//		gbc_clipBrowser.insets = new Insets(0, 0, 5, 5);
//		gbc_clipBrowser.fill = GridBagConstraints.HORIZONTAL;
		add(cncBrowser, gbc_cncBrowser);
	}
//	private void makeCellLength() {
//		JLabel cellLengthLabel = new JLabel("cell length");
//		GridBagConstraints gbc_cll = getGridBagConstraints(0, 2);
//		add(cellLengthLabel, gbc_cll);
//		
//		cellLengthBox = new JComboBox();
//		cellLengthBox.setPreferredSize(new Dimension(40, 25));
//		cellLengthBox.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				cellLength = (int)cellLengthBox.getSelectedItem();
//				recalculateRA();
//				parent.setParameterHasChanged(true);
//			}
//
//
//		});
//		GridBagConstraints gbc_clb = getGridBagConstraints(1, 2);
//		
//		
//		for (int i: cellLengthOptions){
//			cellLengthBox.addItem(i);
//		}
//		cellLengthBox.setSelectedIndex(DEFAULT_CELL_LENGTH_INDEX);
//		add(cellLengthBox, gbc_clb);
//	}


	protected FeatureChunkRepeatSchema getRepeatSchema(int index) {
		if (ra == null){
			return null;
		} else {
			return ra.getRepeatSchema(index);
		}
	}
	private GridBagConstraints getGridBagConstraints(int xpos, int ypos, int width) {
		GridBagConstraints gbc_trns = getGridBagConstraints(xpos, ypos);
		gbc_trns.gridwidth = width;
		return gbc_trns;
	}
	private GridBagConstraints getGridBagConstraints(int xpos, int ypos) {
		GridBagConstraints gbc_trns = new GridBagConstraints();
		gbc_trns.insets = new Insets(5, 5, 5, 5);
		gbc_trns.gridx = xpos;
		gbc_trns.gridy = ypos;
		return gbc_trns;
	}
	
	private void recalculateRA() {
		if (lengthSelectionPanel.selectedCount() > 0) {
			try {
				if (cncBrowser.hasSelectedClipFile()) {
					File clipFile = cncBrowser.getSelectedClipFile();
					LiveClip clip = new LiveClip(0, 0);
					clip.instantiateClipFromBufferedReader(new BufferedReader(new FileReader(clipFile)));
					if (cncBrowser.hasSelectedChordsFile()) {
						File chordsFile = cncBrowser.getSelectedChordsFile();
						LiveClip chords = new LiveClip(0, 0);
						chords.instantiateClipFromBufferedReader(new BufferedReader(new FileReader(chordsFile)));
						ra = new RepetitionAnalysis(clip, lengthSelectionPanel.selectedIndexList(), chords);
					} else {
						ra = new RepetitionAnalysis(clip, lengthSelectionPanel.selectedIndexList());
					}
					//System.out.println("ra rendered: " + ra.toString());
					//				
				} else {
					ra = null;
				}

				contentParent.updateFeatureChooserOptionLists(ra);
				colourParent.updateSubPanelColours();

			} catch (Exception ex) {
				//String str;
				//if (ra == null) str = "null"; else str = ra.toString();
				//System.out.println("ContentAndClipBrowser.recalculateRA() has failed\n" + "ra is..." + str); 
			} 
		}
	}
	public void updateBackgroundColor() {
		if (ra == null){
			System.out.println("ContentClipAndChordPanel.updateBackgroundColor() ra=null");
			if (colourParent.isOn()){
				setBackground(Color.pink);
			} else {
				setBackground(Color.lightGray);
			}
		} else {
			if (colourParent.isOn()) {
				setBackground(Color.green);
			} else {
				setBackground(Color.gray);
			}
		}
		
	}


	@Override
	public void fileBrowserIsUpdated() {
		System.out.println("ContentClipAndChordPanel.fileBrowserIsUpdated(); called...");
		recalculateRA();
		updateBackgroundColor();
		
	}




}
