 package gui_objects.repeat_schema_panel;

import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import DataObjects.ableton_live_clip.LiveClip;
import GUIObjects.ClipAndChordBrowser;
import GUIObjects.FileBrowserPanel;
import GUIObjects.FileBrowserParent;
import gui_objects.ColourUpdateParent;
import gui_objects.cell_length_panel.CellLengthCheckBoxPanel;
import gui_objects.cell_length_panel.CellLengthPanelParent;
import gui_objects.feature_chooser_panel.FeatureSortForTreePanel;
import gui_objects.feature_chooser_panel.FeatureSortForTreeParent;
import gui_objects.feature_chooser_panel.FeatureSortParent;
import gui_objects.main_ui.RepetitionAnalysisGameFrame;
import gui_objects.main_ui.RepetitionAnalysisMainFrame;
import gui_objects.main_ui.RepetitionAnalysisParent;
import jtree_utils.UniqueIcon;
import jtree_utils.UniqueIconRenderer;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchemaSortWrapper;
import repetition_analysis.index_list.IndexList;
import repetition_analysis.repeat_schema_list.FCRSSortWrapperList;
import repetition_analysis.repeat_schema_list.SchemaListObject;

public class MasterRepeatSchemaGamePanel extends JPanel implements FileBrowserParent, FeatureSortParent, FeatureSortForTreeParent, CellLengthPanelParent{

	RepetitionAnalysisParent raParent;
	MasterSchemaPanelParent mspParent;
	private String nameText = "master repeat schema panel";
//	private String clipDialogTitle = "Select a melody clip....";
//	private String chordsDialogTitle = "Select a chord clip....";
//	private String[] clipFilter = new String[]{"liveclip"};
//	private String[] chordsFilter = new String[]{"chords.liveclip"};
	private String clipSavePath;// = "D:/Documents/miscForBackup/Repetition text files/FileBrowserData/masterRepeatClipList.data";
	private String chordsSavePath;// = "D:/Documents/miscForBackup/Repetition text files/FileBrowserData/masterRepeatChordsList.data";
	private int listLength = 15;
	private String clipPath;//= "D:/Documents/repos/CorpusMelodyFiles";
	private String chordsPath;// = "D:/Documents/repos/ChordProgressionTestFiles";
	private ClipAndChordBrowser cncBrowser;
	private JComboBox cellLengthBox;
	private int[] cellLengthOptions = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};	// no theoretical upper limit, 1 may actuall be an option that would need to be tested
	private int cellLength = 3;		// default value
	private DefaultListModel masterSelectionItems = null;
	private JList masterSelectionList;
	private static final int DEFAULT_CELL_LENGTH_INDEX = 3;
	private RepetitionAnalysis ra = null;
	private FeatureSortForTreePanel sortPanel;
	private ArrayList<FeatureChunkRepeatSchema> masterRepeatDisplayList;
	private FeatureChunkRepeatSchemaSortWrapper selectedSchema = null;
	private JLabel lengthLabel;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel model;
	private JTree tree;
	private JScrollPane chooserScrollPane;
	private double end = 10000.0;			// start and end are for testing ultimately this will be a bar and offset number from the output form unit and only describe the upper limit
	private double start = 0.0;		// arbitrarily high number.
	private String iconPath = "src/resources/graphics/heart.png";
	private String slugIconPath = "src/resources/graphics/slug.png";
	private int outputBarCount = 1000;	// arbitrarily high
//	private JCheckBox[] cbArray;
//	private ArrayList<Integer> lengthSelections = new ArrayList<Integer>();
	private CellLengthCheckBoxPanel lengthSelectionPanel;
	private ColourUpdateParent colourParent;

	
	public MasterRepeatSchemaGamePanel(RepetitionAnalysisParent raParent, MasterSchemaPanelParent mspParent){
		setPathsFromPropertiesFile();
		setPathsFromRepetitionAnalysisGamePanel();
		this.raParent = raParent;
		this.mspParent = mspParent;
		this.colourParent = (ColourUpdateParent) raParent;
		makeLayout();
		makeNameLabel();
		makeClipAndChordBrowser();
//		makeCellLength();
//		
//		makeCellLengthMultiOption();
//		lengthSelections.add(DEFAULT_CELL_LENGTH_INDEX);
//		setLengthCheckBoxes(lengthSelections);
		makeLengthSelectionPanel();
		makeSortPanel();
		makeMasterSelectionPane();
		makeListLengthLabel();
		updateBackgroundColor();
	}

	//	public int cellLength(){
//		return cellLength;
//	}
	
//	public int cellLengthIndex(){
//		return cellLengthBox.getSelectedIndex();
//	}
	
	public String getSelectedFilePath(){
		return cncBrowser.selectedClipFilePath();
	}
	
	public String getSelectedChordsPath(){
		return cncBrowser.selectedChordsFilePath();
	}
	
	private void setPathsFromPropertiesFile (){
		try (InputStream input = new FileInputStream(RepetitionAnalysisGameFrame.propertiesPath))
		{
			Properties props = new Properties();
			props.load(input);
			String graphicsPath = props.getProperty("graphics_path");
			iconPath = graphicsPath = "heart.png";
			slugIconPath = graphicsPath = "slug.png";
			clipPath = props.getProperty("default_melody_directory");
			chordsPath = props.getProperty("default_chords_directory");
		}
		catch (IOException ex)
		{
			
		}	
	}
	
	public void setPathsFromRepetitionAnalysisGamePanel (){
		clipSavePath = RepetitionAnalysisMainFrame.getFileBrowserDataPath() + "masterRepeatClipList.data";
		chordsSavePath = RepetitionAnalysisMainFrame.getFileBrowserDataPath() + "masterRepeatChordsList.data";
	}
	
	private void makeLengthSelectionPanel() {
		lengthSelectionPanel = new CellLengthCheckBoxPanel(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, this);
		lengthSelectionPanel.addSelectedIndex(DEFAULT_CELL_LENGTH_INDEX);
		GridBagConstraints gbc_cellLengthPanel = getGridBagConstraints(0, 2);
		add(lengthSelectionPanel, gbc_cellLengthPanel);
	}
//	private void setLengthCheckBoxes(ArrayList<Integer> list) {
//		for (JCheckBox box: cbArray){
//			for (ItemListener il: box.getItemListeners()){
//				box.removeItemListener(il);
//			}
//			int index = Integer.parseInt(box.getText());
//			if (list.contains(index)){				
//				box.setSelected(true);
//			} else {
//				box.setSelected(false);
//			}
//			box.addItemListener(lengthCheckBoxItemListener());
//		}
//		
//	}

	private void makeNameLabel() {
		JLabel label = new JLabel(nameText);
		GridBagConstraints gbc_label = getGridBagConstraints(0, 0);
		add(label, gbc_label);
	}

	private void makeLayout() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{100, 0, 0};
		gridBagLayout.rowHeights = new int[]{20, 20, 20, 20, 50, 250, 20};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE, 1.0, 0.0};
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
//				raParent.setParameterHasChanged(true);
//				mspParent.setLinkedCellLengthIndexInContentBrowser(lengthSelections);
//			}
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
//	private void makeCellLengthMultiOption(){
//		JPanel panel = new JPanel();
//		panel.setLayout(new GridBagLayout());
//		panel.setOpaque(false);
//		
//		JLabel cellLengthLabel = new JLabel("cell length");
//		GridBagConstraints gbc_cll = getGridBagConstraints(0, 0);
//		gbc_cll.gridwidth = 5;
//		panel.add(cellLengthLabel, gbc_cll);
//		
//		int gridxcount = 6;
//		int[] lengthOptions = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
//		int xpos = 0;
//		int ypos = 1;
//		cbArray = new JCheckBox[lengthOptions.length];
//		int index = 0;
//		for (int i: lengthOptions){
//			JCheckBox box = new JCheckBox();
//			box.setText("" + i);
//			box.setOpaque(false);
//			box.addItemListener(lengthCheckBoxItemListener());
//			cbArray[index] = box;
//			GridBagConstraints gbc_box = new GridBagConstraints();
//			gbc_box.gridx = xpos;
//			gbc_box.gridy = ypos;
//			gbc_box.anchor = GridBagConstraints.WEST;
//			panel.add(box, gbc_box);
//			xpos++;
//			if (xpos == gridxcount){
//				xpos = 0;
//				ypos++;
//			}
//			index++;
//		}
//		
//		GridBagConstraints gbc_cellLengthPanel = getGridBagConstraints(0, 2);
//		add(panel, gbc_cellLengthPanel);
//	}
//	
//	private ItemListener lengthCheckBoxItemListener() {
//		
//		return new ItemListener(){
//
//			@Override
//			public void itemStateChanged(ItemEvent arg0) {
//				System.out.println("Checking length option buttons");
//				lengthSelections.clear();
//				for (JCheckBox box: cbArray){
//					if (box.isSelected()){
//						//System.out.println(box.getText());
//						lengthSelections.add(Integer.parseInt(box.getText()));
//					}
//				}
//				String str = "lengthSelections=";
//				for (Integer i: lengthSelections){
//					str += i + ",";
//				}
//				System.out.println(str);
//			}
//			
//		};
//	}







	private void makeSortPanel() {
		GridBagConstraints gbc_sortPane =  getGridBagConstraints(0, 4);
//		gbc_sortPane.fill = GridBagConstraints.BOTH;
//		gbc_sortPane.gridheight = 3;
//		gbc_sortPane.gridwidth = 2;
		sortPanel = new FeatureSortForTreePanel(this);
		sortPanel.setOpaque(false);
		add(sortPanel, gbc_sortPane);	
		
	}
	private void makeListLengthLabel() {
		lengthLabel = new JLabel("listLength");
		GridBagConstraints gbc_lengthLabel =  getGridBagConstraints(0, 8);
		gbc_lengthLabel.fill = GridBagConstraints.BOTH;
		gbc_lengthLabel.gridheight = 1;
		gbc_lengthLabel.gridwidth = 2;
		add(lengthLabel, gbc_lengthLabel);
	}
	
	private void makeMasterSelectionPane() {
//		makeOldSelectionPane();
		makeNewJTreeSelectionPane();
		addTestContentToList();
	}
	
	private void makeNewJTreeSelectionPane() {
		GridBagConstraints gbc_chooser = getGBC(0, 5);
		gbc_chooser.fill = GridBagConstraints.BOTH;
		gbc_chooser.weighty = 1.0;
		gbc_chooser.weightx = 1.0;
		
		root = new DefaultMutableTreeNode("Root");
		model = new DefaultTreeModel(root);
		tree = new JTree(model);
		tree.addTreeSelectionListener(new TreeSelectionListener(){

			@Override
			public void valueChanged(TreeSelectionEvent te) {
				DefaultMutableTreeNode selectedNode = 
					       (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
				if (selectedNode != null){
					Object uo = selectedNode.getUserObject();
					if (uo instanceof UniqueIcon){
						UniqueIcon ui = (UniqueIcon) uo;
						FeatureChunkRepeatSchemaSortWrapper fcrs = (FeatureChunkRepeatSchemaSortWrapper) ui.getObject();
						if (fcrs != null){
							//System.out.println("Tree item selected: " + selectedNode.toString() + " obj=" + fcrs.toString());
							updateFeatureChoosers(fcrs);
							updateGraphic();
						} else {
							//System.out.println("null object selected");
						}
					}
				} 
			}

			
			
		});

		tree.setRootVisible(false);

		chooserScrollPane = new JScrollPane(tree);
		chooserScrollPane.setPreferredSize(new Dimension(200, 250));

		add(chooserScrollPane, gbc_chooser);
		
	}
	private void updateGraphic() {
		// TODO Auto-generated method stub
		mspParent.updateRSGraphic();
	}
	protected void updateFeatureChoosers(FeatureChunkRepeatSchemaSortWrapper fcrs) {
    	selectedSchema = fcrs;
    	setFeatureChooserAttachLists();
    	raParent.setParameterHasChanged(true);
		
	}







	private GridBagConstraints getGBC(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.anchor = GridBagConstraints.WEST;
		return gbc;
	}

//	private void makeOldSelectionPane() {
//		JScrollPane scrollPane = new JScrollPane();
//		scrollPane.setPreferredSize(new Dimension(200, 250));
//		GridBagConstraints gbc_scrollPane =  getGridBagConstraints(0, 5);
//		gbc_scrollPane.fill = GridBagConstraints.BOTH;
//		gbc_scrollPane.gridheight = 3;
//		gbc_scrollPane.gridwidth = 2;
//		add(scrollPane, gbc_scrollPane);
//		
////		JPanel panel = new JPanel(new BorderLayout());
////		scrollPane.setViewportView(panel);
//		
////		textPane = new JTextPane();
////		panel.add(textPane);
//		
//		masterSelectionItems = new DefaultListModel();
//		masterSelectionList = new JList(masterSelectionItems);
//		masterSelectionList.addMouseListener(new MouseAdapter() {
//	        public void mouseClicked(MouseEvent e) {
//	            if (e.getClickCount() == 1) {
//	                //al.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "ENTER"));
//	            	int index = masterSelectionList.getSelectedIndex();
//	            	//System.out.println("index=" + index);
//	            	FeatureChunkRepeatSchema fcrs = masterRepeatDisplayList.get(index);
//	            	selectedSchema = fcrs;
//	            	setFeatureChooserAttachLists();
//	            	raParent.setParameterHasChanged(true);
//	            	//System.out.println("masterRepeatSchema selected: index=" + index + "\n" + fcrs.toShortString());
//	            }
//	        }
//	    });
//		scrollPane.setViewportView(masterSelectionList);
//		
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
		if (lengthSelectionPanel.selectedCount() > 0){
			try{
				if (cncBrowser.hasSelectedClipFile()){
					File clipFile = cncBrowser.getSelectedClipFile();
					LiveClip clip = new LiveClip(0, 0);
					clip.instantiateClipFromBufferedReader(new BufferedReader(new FileReader(clipFile)));
					if (cncBrowser.hasSelectedChordsFile()){
						File chordsFile = cncBrowser.getSelectedChordsFile();
						LiveClip chords = new LiveClip(0, 0);
						chords.instantiateClipFromBufferedReader(new BufferedReader(new FileReader(chordsFile)));
						ra = new RepetitionAnalysis(clip, cellLength, chords);
					} else {
						ra = new RepetitionAnalysis(clip, cellLength);
					}
					
				} else {
					ra = null;
				}
				//updateRepeatList(sortPanel.getfeatureIDList());
				refreshSelectionDisplay();
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
		
	}

	private void setFeatureChooserAttachLists() {
		ArrayList<IndexList> list;
		if (selectedSchema == null){
			list = new ArrayList<IndexList>();
		} else {
			list = selectedSchema.fcrs.secondaryIndexList();
		}
		
		mspParent.setAttachListContentInFeatureChooserPanel(list);
		
	}

	private void updateRepeatList(ArrayList<Integer> featureIDList) {
		masterRepeatDisplayList = makeMasterRepeatDisplayList(featureIDList);
		ArrayList<String> list = new ArrayList<String>();
		if (masterRepeatDisplayList.size() > 0){
			for (FeatureChunkRepeatSchema fcrs: masterRepeatDisplayList){
				list.add(fcrs.toShortString());
				//list.add(slo.repeatsToString());				
			}
		} else {
			list.add("no options at this time....");
		}
		setSelectionListOptions(list);	
		setListLength(list.size());
	}

// FeatureSortForTreeParent methods --------------------------------------------------------
	public void refreshSelectionDisplay() {
		
		
//		System.out.println("refresh display parameters: " + sortContent + ", " + priorityContent);
		
		if (ra != null) {
			if (lengthSelectionPanel.selectedCount() > 0) {
				String sortContent = getGroupSelectionString(sortPanel.getSortGroup());
				String priorityContent = getGroupSelectionString(sortPanel.getSortPriorityGroup());
				ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList = ra.getSchemaList()
						.makePositionConstrainedSchemaList(outputBarCount);
				sortPanel.loadFeatureConstrainers(swList, sortContent);
				//		for (FeatureChunkRepeatSchemaSortWrapper sw: swList){
				//			System.out.println(sw.toString());
				//		}
				ArrayList<FCRSSortWrapperList> swlList = new ArrayList<FCRSSortWrapperList>();
				//		TreeMap<Double, ArrayList<FCRSSortWrapperList>> map;
				int count = 0;
				if (sortContent == FeatureSortForTreePanel.POSITION_STRING) {
					swlList = ra.getSchemaList().getPosSortWrapperList(swList, sortPanel.minimumFeatureLength(),
							sortPanel.maximumFeatureLength());
					if (priorityContent == FeatureSortForTreePanel.START_POSITION_STRING) {
						count = doStartPosMap(swlList);
					} else if (priorityContent == FeatureSortForTreePanel.FEATURE_LENGTH_STRING) {
						count = doFeatureLengthMap(swlList);
					} else if (priorityContent == FeatureSortForTreePanel.POPULARITY_STRING) {
						count = doPopularityMap(swlList);
					}
				} else if (sortContent == FeatureSortForTreePanel.GAP_STRING) {
					swlList = ra.getSchemaList().getGapSortWrapperList(swList, sortPanel.minimumFeatureLength(),
							sortPanel.maximumFeatureLength());
					count = doGapSortMap(swlList);
				}
				sortPanel.setLvl1Button(sortPanel.EXPAND_LEVEL_1, false);
				sortPanel.setLvl2Button(sortPanel.EXPAND_LEVEL_2, false);
				tree.setCellRenderer(new UniqueIconRenderer());
				tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				tree.expandRow(0);
				model.reload();
				setListLength(count);
			} 
		} else {
			root.removeAllChildren();
			model.reload();
			setListLength(0);
		}
	}
	@Override
	public DefaultMutableTreeNode root() {
		return root;
	}







	@Override
	public JTree tree() {
		return tree;
	}
/// --------------------------------------------------------------------------------------------------
	private int doGapSortMap(ArrayList<FCRSSortWrapperList> swlList) {
		//returns number of FeatureChunRepeatSchemas in the JTree
		Collections.sort(swlList, FCRSSortWrapperList.featureLengthComparator);
		root.removeAllChildren();
		TreeMap<Double, ArrayList<FeatureChunkRepeatSchemaSortWrapper>> map = new TreeMap<Double, ArrayList<FeatureChunkRepeatSchemaSortWrapper>>();
		int count = 0;
		for (FCRSSortWrapperList swl: swlList){
			
			map.clear();
			Iterator<FeatureChunkRepeatSchemaSortWrapper> swIterator = swl.iterator();
			while (swIterator.hasNext()){
				FeatureChunkRepeatSchemaSortWrapper sw = swIterator.next();
				if (!map.containsKey(sw.firstPos())){
					map.put(sw.firstPos(), new ArrayList<FeatureChunkRepeatSchemaSortWrapper>());
				}
				map.get(sw.firstPos()).add(sw);
			}
			String branchName = "gapArray " + swl.keyArrayToString() + " (" + map.size() + ")";
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(new UniqueIcon(branchName, iconPath, null));
			String view;
			for (Double d: map.keySet()){
				if (sortPanel.showAbsolutePos()) {
					view = "" + d;
				} else {
					view = map.get(d).get(0).firstBarPosToString();
				}
				DefaultMutableTreeNode twig = new DefaultMutableTreeNode(new UniqueIcon("startPos " + view + " (" + map.get(d).size() + ")", slugIconPath, map.get(d).get(0)));		// 0 - if you select the group it returns the first item, as they ball have the same posArray
				for (FeatureChunkRepeatSchemaSortWrapper sw: map.get(d)){
					String twigName = sw.fcrs.featureShortName() + ": " + sw.fcrs.featureString();
					twig.add(new DefaultMutableTreeNode(new UniqueIcon(twigName, sw)));
					count++;
				}
				branch.add(twig);
			}
		
//			swl.sortList(FeatureChunkRepeatSchemaSortWrapper.sizeAndFirstPosComparator);
						
			root.add(branch);
		}
		return count;
		
	}
	private int doPopularityMap(ArrayList<FCRSSortWrapperList> swlList) {
		TreeMap<Integer, ArrayList<FCRSSortWrapperList>> map = new TreeMap<Integer, ArrayList<FCRSSortWrapperList>>();
		for (FCRSSortWrapperList swl: swlList){
			if (!map.containsKey(swl.size())){
				map.put(swl.size(), new ArrayList<FCRSSortWrapperList>());
			}
			map.get(swl.size()).add(swl);
		}
		root.removeAllChildren();
		int count = 0;
		for (Integer i: map.keySet()){
			ArrayList<FCRSSortWrapperList> list = map.get(i);
			String branchName = "popularity " + i + " (" + list.size() + ")";
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(new UniqueIcon(branchName, iconPath, null));
			Collections.sort(list, FCRSSortWrapperList.positionComparator);
			String view;
			for (FCRSSortWrapperList swl: list){
				if (sortPanel.showAbsolutePos()) {
					view = swl.keyArrayToString();
				} else {
					view = swl.relativeBarPosToString();
				}
				DefaultMutableTreeNode twig = new DefaultMutableTreeNode(new UniqueIcon(view, slugIconPath, swl.getFirstSortWrapper()));
//				swl.sortList(FeatureChunkRepeatSchemaSortWrapper.posComparator);
				Iterator<FeatureChunkRepeatSchemaSortWrapper> swIterator = swl.iterator();
				while (swIterator.hasNext()){
					FeatureChunkRepeatSchemaSortWrapper sw = swIterator.next();
					twig.add(new DefaultMutableTreeNode(new UniqueIcon(sw.fcrs.featureShortName(), sw)));
					count++;
				}
				branch.add(twig);
			}
			root.add(branch);
		}
		return count;
	}
	private int doFeatureLengthMap(ArrayList<FCRSSortWrapperList> swlList) {
		TreeMap<Integer, ArrayList<FCRSSortWrapperList>> map = new TreeMap<Integer, ArrayList<FCRSSortWrapperList>>();
		for (FCRSSortWrapperList swl: swlList){
			if (!map.containsKey(swl.featureArrayLength())){
				map.put(swl.featureArrayLength(), new ArrayList<FCRSSortWrapperList>());
			}
			map.get(swl.featureArrayLength()).add(swl);
		}
		root.removeAllChildren();
		int count = 0;
		for (Integer i: map.keySet()){
			ArrayList<FCRSSortWrapperList> list = map.get(i);
			String branchName = "featureLength " + i + " (" + list.size() + ")";
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(new UniqueIcon(branchName, iconPath, null));
			Collections.sort(list, FCRSSortWrapperList.positionComparator);
			String view;
			for (FCRSSortWrapperList swl: list){
				if (sortPanel.showAbsolutePos()) {
					view = swl.keyArrayToString();
				} else {
					view = swl.relativeBarPosToString();
				}
				DefaultMutableTreeNode twig = new DefaultMutableTreeNode(new UniqueIcon(view, slugIconPath, swl.getFirstSortWrapper()));
//				swl.sortList(FeatureChunkRepeatSchemaSortWrapper.posComparator);
				Iterator<FeatureChunkRepeatSchemaSortWrapper> swIterator = swl.iterator();
				while (swIterator.hasNext()){
					FeatureChunkRepeatSchemaSortWrapper sw = swIterator.next();
					twig.add(new DefaultMutableTreeNode(new UniqueIcon(sw.fcrs.featureShortName(), sw)));
					count++;
				}
				branch.add(twig);
			}
			root.add(branch);
		}
		return count;
	}
	private int doStartPosMap(ArrayList<FCRSSortWrapperList> swlList) {
		TreeMap<Double, ArrayList<FCRSSortWrapperList>> map = new TreeMap<Double, ArrayList<FCRSSortWrapperList>>();
		for (FCRSSortWrapperList swl: swlList){
			if (!map.containsKey(swl.getFirstFeature())){
				map.put(swl.getFirstFeature(), new ArrayList<FCRSSortWrapperList>());
			}
			map.get(swl.getFirstFeature()).add(swl);
		}
		root.removeAllChildren();
		String view;
		int count = 0;
		for (Double d: map.keySet()){
			ArrayList<FCRSSortWrapperList> list = map.get(d);
			if (sortPanel.showAbsolutePos()) {
				view = "" + d;
			} else {
				view = list.get(0).firstBarPosToString();
			}
			String branchName = "pos " + view + " (" + list.size() + ")";
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(new UniqueIcon(branchName, iconPath, null));
			Collections.sort(list, FCRSSortWrapperList.featureLengthComparator);
			
			for (FCRSSortWrapperList swl: list){
				if (sortPanel.showAbsolutePos()) {
					view = swl.keyArrayToString();
				} else {
					view = swl.relativeBarPosToString();
				}
				DefaultMutableTreeNode twig = new DefaultMutableTreeNode(new UniqueIcon(view, slugIconPath, swl.getFirstSortWrapper()));
				Iterator<FeatureChunkRepeatSchemaSortWrapper> swIterator = swl.iterator();
				while (swIterator.hasNext()){
					FeatureChunkRepeatSchemaSortWrapper sw = swIterator.next();
					twig.add(new DefaultMutableTreeNode(new UniqueIcon(sw.fcrs.featureShortName(), sw)));
					count++;
				}
				branch.add(twig);
			}
			root.add(branch);
		}
		return count;
	}
	private String getGroupSelectionString(ButtonGroup group) {
		String str = FeatureSortForTreePanel.EMPTY_STRING;
		for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                str = button.getText();
            }
        }
		return str;
	}
	private void setListLength(int size) {
		lengthLabel.setText("listLength=" + size);
		
	}







	private ArrayList<FeatureChunkRepeatSchema> makeMasterRepeatDisplayList(ArrayList<Integer> featureIDList) {
		ArrayList<FeatureChunkRepeatSchema> list = new ArrayList<FeatureChunkRepeatSchema>();
		if (ra != null){
			for (SchemaListObject slo: ra.getSchemaList().schemaList){
				if (slo instanceof FeatureChunkRepeatSchema){
					if (featureIDList.contains(((FeatureChunkRepeatSchema) slo).featureID())){
						list.add((FeatureChunkRepeatSchema) slo);
					}
				}
			}
		}		
		return list;
	}

	private void setSelectionListOptions(ArrayList<String> list) {
		masterSelectionItems.clear();
		for (String str: list){
			masterSelectionItems.addElement(str);
		}		
	}
	
	public void updateBackgroundColor() {
		if (ra == null){
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
	
	private void sendPathsAndCellLengthToContentBrowser(){
		mspParent.setLinkedFieldsInContentBrowser(getSelectedFilePath(), getSelectedChordsPath(), lengthSelectionPanel.selectedIndexList());
	}
// FileBrowserParent methods ----------------------------------------------------------------	
	@Override
	public void fileBrowserIsUpdated() {
		System.out.println("MasterRepeatSchemaGamePanel.fileBrowserIsUpdated() called...");
		recalculateRA();
		refreshSelectionDisplay();
//		updateRepeatList(sortPanel.getfeatureIDList());
//		updateBackgroundColor();
		colourParent.updateSubPanelColours();
		setFeatureChooserAttachLists();
		sendPathsAndCellLengthToContentBrowser();
	}

// MasterSchemaPanelParent methods ----------------------------------------------------------
	@Override
	public void updateSortedPanel(ArrayList<Integer> list) {
		updateRepeatList(list);
		
	}
//-------------------------------------------------------------------------------------------


	public FeatureChunkRepeatSchemaSortWrapper getSelectedSchema() {
		return selectedSchema;
	}
// testing methods ------------------------------------------------------------------
	protected void addTestContentToList() {
		//System.out.println("addTestContent called...");
		DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("Vegetables");
		vegetableNode.add(new DefaultMutableTreeNode("Capsicum"));
		vegetableNode.add(new DefaultMutableTreeNode("Carrot"));
		vegetableNode.add(new DefaultMutableTreeNode("Tomato"));
		vegetableNode.add(new DefaultMutableTreeNode("Potato"));
		DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("Fruits");
		fruitNode.add(new DefaultMutableTreeNode("Banana"));
		fruitNode.add(new DefaultMutableTreeNode("Mango"));
		fruitNode.add(new DefaultMutableTreeNode("Apple"));
		fruitNode.add(new DefaultMutableTreeNode("Grapes"));
		fruitNode.add(new DefaultMutableTreeNode("Orange"));
		
		root.removeAllChildren();
	//	DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
	//	model.reload();
		root.add(vegetableNode);
		root.add(fruitNode);
		tree.expandRow(0);
		model.reload();
		
	}







	public void setOutputFormBarCountAndRefresh(int barCount) {
		//System.out.println("MasterRepeatSchemaGamePanel.setOutputFormBarCountAndRefresh() called.....");
		if (barCount < 1) barCount = 1;	
		outputBarCount = barCount;
		if (cncBrowser.hasSelectedClipFile()){			
			refreshSelectionDisplay();
		}
		
	}







	public ArrayList<Integer> cellLengthSelections() {
		return lengthSelectionPanel.selectedIndexList();
	}







	public String cellLengthsToString() {
		return this.lengthSelectionPanel.cellLengthsToString();
	}








}
