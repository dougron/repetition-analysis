package gui_objects.feature_chooser_panel;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import gui_objects.main_ui.RepetitionAnalysisParent;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_picker.FeaturePicker;
import repetition_analysis.index_list.IndexList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;


public class FeatureGameChooser extends JPanel {
	
	JList optionList;
	DefaultListModel optionModel = new DefaultListModel();
	JList selectedList;
	private DefaultListModel selectedModel = new DefaultListModel();
	private int selectedListIndex = -1;
	JList attachList;
	private DefaultListModel attachModel = new DefaultListModel();
	ArrayList<FeatureChunkRepeatSchema> fcwOptionList = new ArrayList<FeatureChunkRepeatSchema>();
	ArrayList<FeatureChunkRepeatSchema> fcwSelectedList = new ArrayList<FeatureChunkRepeatSchema>();
	ArrayList<IndexList> ilAttachList = new ArrayList<IndexList>();
	ArrayList<Integer> selectedIndexList = new ArrayList<Integer>();
	ArrayList<FeatureChunkRepeatSchema> previousList = new ArrayList<FeatureChunkRepeatSchema>();		// previousList is not a format that GenThree can process correctly. this is for GenFour and for internal checking whether parameter changes have made a difference to the final outputList
	//FeaturePicker fp;
	int elementType;
	private int attachListIndex = 0;
	private RepetitionAnalysisParent raParent;
	private String name;
//	private ColourUpdateParent colourParent;
	private Comparator optionListComparator;
	
	public String toString() {
		return "FeatureGameChooser:" + name + " elementType=" + elementType;
	}

	/**
	 * Create the panel.
	 */
	public FeatureGameChooser(final int elementType, String name, RepetitionAnalysisParent raParent, Comparator c) {
		this.optionListComparator = c;
		this.raParent = raParent;
//		this.colourParent = (ColourUpdateParent) raParent;
		//this.fp = fp;
		this.elementType = elementType;
		this.name = name;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{200, 0, 150, 0};
		gridBagLayout.rowHeights = new int[]{130, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JScrollPane optionScrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.gridheight = 2;
		gbc_scrollPane_2.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.gridx = 0;
		gbc_scrollPane_2.gridy = 0;
		add(optionScrollPane, gbc_scrollPane_2);
		
		optionList = new JList();
		optionList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2){
					fcwSelectedList.add(fcwOptionList.get(optionList.getSelectedIndex()));
					selectedIndexList.add(optionList.getSelectedIndex());
					testAndSendIfParameterHasChangedOutput();
					updateSelectedList();
					//fp.bang();
				}
			}
		});
		optionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		optionList.setModel(optionModel);
		optionScrollPane.setViewportView(optionList);
		
		JScrollPane selectedScrollPane = new JScrollPane();
		GridBagConstraints gbc_selectedScrollPane = new GridBagConstraints();
		gbc_selectedScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_selectedScrollPane.fill = GridBagConstraints.BOTH;
		gbc_selectedScrollPane.gridx = 1;
		gbc_selectedScrollPane.gridy = 0;
		add(selectedScrollPane, gbc_selectedScrollPane);
		
		selectedList = new JList();
		selectedList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				 if (e.getClickCount() == 1) {
					 selectedListIndex = selectedList.getSelectedIndex();
				 } else if (e.getClickCount() == 2){
					 selectedListIndex = selectedList.getSelectedIndex();
					 fcwSelectedList.remove(selectedListIndex);
					 selectedIndexList.remove(selectedListIndex);
					 if (selectedListIndex == 0){
						 selectedListIndex = -1;
					 } else if (selectedListIndex == fcwSelectedList.size()){
						 selectedListIndex--;
					 }
					 testAndSendIfParameterHasChangedOutput();
					 updateSelectedList();
					 //fp.bang();
				 }
				
			}
		});
		selectedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedList.setModel(selectedModel);
		selectedScrollPane.setViewportView(selectedList);
		
		JScrollPane attachedScrollPane = new JScrollPane();
		GridBagConstraints gbc_attachedScrollPane = new GridBagConstraints();
		gbc_attachedScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_attachedScrollPane.fill = GridBagConstraints.BOTH;
		gbc_attachedScrollPane.gridx = 2;
		gbc_attachedScrollPane.gridy = 0;
		add(attachedScrollPane, gbc_attachedScrollPane);
		
		attachList = new JList();
		attachList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1){
					attachListIndex = attachList.getSelectedIndex();
					testAndSendIfParameterHasChangedOutput();
					//fp.setMasterFeatureID(ilAttachList.get(attachList.getSelectedIndex()).featureID(), elementType);
					//fp.bang();
				}
			}
		});
		attachList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		attachList.setModel(attachModel);
		attachedScrollPane.setViewportView(attachList);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.setOpaque(false);
		
		JButton moveUp = new JButton("MoveUp");
		moveUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (selectedListIndex > 0){
					FeatureChunkRepeatSchema temp = fcwSelectedList.remove(selectedListIndex - 1);
					Integer itemp = selectedIndexList.remove(selectedListIndex - 1);
					fcwSelectedList.add(selectedListIndex, temp);
					selectedIndexList.add(selectedListIndex, itemp);
					selectedListIndex--;
					testAndSendIfParameterHasChangedOutput();
					updateSelectedList();
					//fp.bang();
				}
			}
		});
		panel.add(moveUp);
		
		JButton moveDown = new JButton("MoveDown");
		moveDown.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				if (selectedListIndex > -1 && selectedListIndex < fcwSelectedList.size() - 1){
					FeatureChunkRepeatSchema temp = fcwSelectedList.remove(selectedListIndex + 1);
					Integer itemp = selectedIndexList.remove(selectedListIndex + 1);
					fcwSelectedList.add(selectedListIndex, temp);
					selectedIndexList.add(selectedListIndex, itemp);
					selectedListIndex++;
					testAndSendIfParameterHasChangedOutput();
					updateSelectedList();
					//fp.bang();
				}
			}
		});
		panel.add(moveDown);
		
		JLabel nameLabel = new JLabel(name);
		nameLabel.setFont(new Font("Serif", Font.BOLD, 25));
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.anchor = GridBagConstraints.CENTER;
		gbc_nameLabel.gridx = 2;
		gbc_nameLabel.gridy = 1;
		gbc_nameLabel.fill = GridBagConstraints.BOTH;
		add(nameLabel, gbc_nameLabel);
		
		setOpaque(false);

	}
	public String getName(){
		return name;
	}
	public ArrayList<FeatureChunkRepeatSchema> getOutputList(){
		return previousList;
	}
	public boolean hasOutputList(){
		if (previousList.size() > 0){
			return true;
		} else {
			return false;
		}
	}
	public void clearSelectedList(){
		fcwSelectedList.clear();
		selectedIndexList.clear();
		testAndSendIfParameterHasChangedOutput();
		updateSelectedList();
	}
	public int getMasterRepeatSchemaAttachIndex(){
		return ilAttachList.get(attachListIndex).featureID();
	}
	protected void testAndSendIfParameterHasChangedOutput() {
		//fp.setArray(fcwSelectedList, elementType);
		ArrayList<FeatureChunkRepeatSchema> tempList = new ArrayList<FeatureChunkRepeatSchema>();
		if (fcwSelectedList.size() > 0 && ilAttachList.size() > 0){
			for (int index: ilAttachList.get(attachListIndex).getiList()){
				tempList.add(fcwSelectedList.get(index % fcwSelectedList.size()));
			}
		}		
		if (!compareLists(tempList, previousList)){
			previousList = tempList;
			raParent.setParameterHasChanged(true);
		}		
	}
	private boolean compareLists(ArrayList<FeatureChunkRepeatSchema> list1,
			ArrayList<FeatureChunkRepeatSchema> list2) {
		// this does a pointer check not a value check
		//System.out.println("list1.size()=" + list1.size() + " list2.size()=" + list2.size());
		if (list1.size() == list2.size()){
			for (int i = 0; i < list1.size(); i++){
				//System.out.println("compare:" + list1.get(i).uniqueID() + " to " + list2.get(i).uniqueID());
				if (list1.get(i) != list2.get(i)){
					//System.out.println("not the same: return false");
					return false;
				}
			}
			//System.out.println("lists are the same");
			return true;
		} else {
			//System.out.print("not the same: return false");
			return false;
		}
		
	}
	public void setOptionList(ArrayList<FeatureChunkRepeatSchema> list){
		fcwOptionList = list;
		Collections.sort(fcwOptionList, optionListComparator);
		updateGUI();
	}
	public void setSelectedList(ArrayList<FeatureChunkRepeatSchema> list){
		fcwSelectedList = list;
		updateGUI();
	}
	public void setAttachList(ArrayList<IndexList> list){
		ilAttachList = list;
		testAndSendIfParameterHasChangedOutput();
		updateGUI();
	}
	public void rebuildSelectedListFromIndexList(){
		fcwSelectedList.clear();
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (Integer i: selectedIndexList){
			int index = i % fcwOptionList.size();
			fcwSelectedList.add(fcwOptionList.get(index));
			list.add(index);
		}
		testAndSendIfParameterHasChangedOutput();
		updateSelectedList();
	}
	
	private void updateGUI() {
		updateOptionList();
		updateSelectedList();
		updateAttachList();
	}

	private void updateAttachList() {
		attachModel.clear();
		for (IndexList il: ilAttachList){
			attachModel.addElement(il.listToString());
		}
		if (ilAttachList.size() == 0){
			attachListIndex = 0;
		} else {
			attachListIndex = attachListIndex % ilAttachList.size();
		}
		attachList.setSelectedIndex(attachListIndex);
	}
	private void updateSelectedList() {
		selectedModel.clear();
		for (FeatureChunkRepeatSchema fcrs: fcwSelectedList){
			selectedModel.addElement(fcrs.nameFeatureStringAndRepeatCountToString());
		}
		if (selectedListIndex > -1 && selectedListIndex < fcwOptionList.size()){
			selectedList.setSelectedIndex(selectedListIndex);
		}
		
	}
	private void updateOptionList() {
		optionModel.clear();
		for (FeatureChunkRepeatSchema fcrs: fcwOptionList){
			optionModel.addElement(fcrs.nameFeatureStringAndRepeatCountToString());
		}
		
	}

	public static final int RHYTHM = 0;				// This has been duplicated in FeatureChunk but in a different 
	public static final int DURATION = 1;			// sequence. rather use FeatureChunk as the values represent indices for
	public static final int DYNAMICS = 2;			// names and featureID arrays and so forth.
	public static final int PITCH = 3;
	public static final int CONTOUR = 4;
	

}
