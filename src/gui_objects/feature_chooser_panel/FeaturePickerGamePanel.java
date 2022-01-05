package gui_objects.feature_chooser_panel;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JTextPane;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.incomplete_note_utils.FinalListNote;
import ResourceUtils.ChordForm;
import gui_objects.ColourUpdateParent;
import gui_objects.content_clip_and_chord_panel.ContentClipAndChordBrowser;
import gui_objects.content_clip_and_chord_panel.ContentClipAndChordBrowserParent;
import gui_objects.copyover_rule_panel.CopyOverRulePanel;
import gui_objects.main_ui.RepetitionAnalysisParent;
import gui_objects.register_panel.RegisterInfoPanel;
import gui_objects.repeat_schema_panel.MasterRepeatSchemaGamePanel;
import gui_objects.repeat_schema_panel.MasterSchemaPanelParent;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_handler.FeatureHandler;
import repetition_analysis.feature_handler.Handler;
import repetition_analysis.feature_picker.FeatureGamePicker;
import repetition_analysis.feature_picker.FeaturePicker;
import repetition_analysis.index_list.IndexList;
import repetition_analysis.info_objects.RegisterInfo;
import repetition_analysis.repeat_schema_list.SchemaListObject;
import repetition_analysis.repetition_generator.GenThree;

public class FeaturePickerGamePanel extends JPanel 
implements RepetitionAnalysisParent, MasterSchemaPanelParent, ContentClipAndChordBrowserParent, ColourUpdateParent {
	

	FeatureChooser rhythmChooser = null;
//	private int[] durationFeatureOptions = new int[]{FeatureChunk.ABSOLUTE_DURATION, FeatureChunk.STACCATO_PORTATO_TENUTO};
	FeatureChooser durationChooser = null;
//	private int[] dynamicsFeatureOptions = new int[]{FeatureChunk.LOUD_SOFT_DYNAMICS, FeatureChunk.SIMPLE_DYNAMICS};
	FeatureChooser dynamicsChooser = null;
//	private int[] pitchFeatureOptions = new int[]{FeatureChunk.LOUD_SOFT_DYNAMICS, FeatureChunk.SIMPLE_DYNAMICS};
	FeatureChooser pitchChooser = null;
//	private int[] contourFeatureOptions = new int[]{FeatureChunk.SIMPLE_MELODY_CONTOUR, FeatureChunk.HALF_SIMPLE_MELODY_CONTOUR};
	FeatureChooser contourChooser = null;
	GenThree gen = new GenThree();
	// these need to become panel members....
//	private RegisterInfo ri = RegisterInfo.registerMap.get(RegisterInfo.TRUMPET);
	private double xover = 0.5;
	private Color onColor = Color.GREEN;
	private Color offColor = Color.GRAY;
	public static final String copyOverRuleTrueText = "copyOver=TRUE";
	public static final String copyOverRuleFalseText = "copyOver=FALSE";
	//.......................................
	
//	private ChordForm outputCF = null;
	private RepetitionAnalysisParent parent;
	private MasterRepeatSchemaGamePanel masterRepeatSchemaPanel;
	private ContentClipAndChordBrowser contentBrowser;
	private FeatureChooserGamePanel featureChooserPanel;
	private RegisterInfoPanel registerInfoPanel;
	private CopyOverRulePanel copyOverPanel;
	private FeaturePickerTab tabPanel;
	private FeaturePickerManagerParent fpParent;
	private static int counter = 0;
	private int id;
	private boolean isOn = true;

	

	/**
	 * Create the panel.
	 * @param parent 
	 */
	public FeaturePickerGamePanel(RepetitionAnalysisParent parent, FeaturePickerManagerParent fpParent) {
		this.parent = parent;
		this.fpParent = fpParent;
		id = counter;
		counter++;
		makeLayout();
		makeMasterRepeatSchemaPanel();
		addContentBrowserPanel();
		addFeatureChooserPanel();
		addRegisterInfoPanel();
		addCopyOverRulePanel();
		tabPanel = new FeaturePickerTab(this);
		setTabLabelText("" + id);
		tabPanel.setOn(isOn);
	}
	public int id(){
		return id;
	}
	public boolean hasCompleteData(){
		if (featureChooserPanel.hasEnoughToGenerateOutput() && registerInfoPanel.hasRegisterInfo()){
			return true;
		} else {
			return false;
		}
	}
	public JPanel tabPanel(){
		return tabPanel;
	}
	public void setTabLabelText(String str) {
		tabPanel.setLabelText(str);
		
	}

// privates --------------------------------------------------------------------------------
	
	



	private void addCopyOverRulePanel() {
		copyOverPanel = new CopyOverRulePanel(featureChooserPanel);
		GridBagConstraints gbc_copyOverPanel = getGridBagConstraints(1, 2, 1, 1);
		gbc_copyOverPanel.fill = GridBagConstraints.BOTH;
		add(copyOverPanel, gbc_copyOverPanel);
		
	}

	private void addRegisterInfoPanel() {
		registerInfoPanel = new RegisterInfoPanel(this);
		GridBagConstraints gbc_renderPanel = getGridBagConstraints(1, 1, 1, 1);
		add(registerInfoPanel, gbc_renderPanel);
		
	}

	private void addFeatureChooserPanel() {
		featureChooserPanel = new FeatureChooserGamePanel(this);
		GridBagConstraints gbc_featureChooserPanel = getGridBagConstraints(2, 0, 1, 3);
		add(featureChooserPanel, gbc_featureChooserPanel);
	}

	private void addContentBrowserPanel() {
		contentBrowser = new ContentClipAndChordBrowser(this, masterRepeatSchemaPanel, this);
		GridBagConstraints gbc_contentBrowser = getGridBagConstraints(1, 0, 1, 1);
		add(contentBrowser, gbc_contentBrowser);
		
	}
	private void makeMasterRepeatSchemaPanel() {
		masterRepeatSchemaPanel = new MasterRepeatSchemaGamePanel(this, this);
		GridBagConstraints gbc_master = getGridBagConstraints(0, 0, 1, 3);
		gbc_master.fill = GridBagConstraints.BOTH;
		add(masterRepeatSchemaPanel, gbc_master);
		
	}
	private void makeLayout() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{100, 0, 0};
		gridBagLayout.rowHeights = new int[]{300, 20, 20, 20, 20, 20};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		setBackground(offColor);
		
	}
	private GridBagConstraints getGridBagConstraints(int xpos, int ypos, int width) {
		GridBagConstraints gbc_trns = getGridBagConstraints(xpos, ypos);
		gbc_trns.gridwidth = width;
		return gbc_trns;
	}
	private GridBagConstraints getGridBagConstraints(int xpos, int ypos, int width, int height) {
		GridBagConstraints gbc_trns = getGridBagConstraints(xpos, ypos);
		gbc_trns.gridwidth = width;
		gbc_trns.gridheight = height;
		return gbc_trns;
	}
	private GridBagConstraints getGridBagConstraints(int xpos, int ypos) {
		GridBagConstraints gbc_trns = new GridBagConstraints();
		gbc_trns.insets = new Insets(5, 5, 5, 5);
		gbc_trns.gridx = xpos;
		gbc_trns.gridy = ypos;
		return gbc_trns;
	}
	
	
// overridden methods --------------------------------------------------------------------------
	@Override
	public ChordForm cf() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean parameterHasChanged() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setParameterHasChanged(boolean b) {
		//System.out.println("FeaturePickerGamePanel.setParameterHasChanged() called...");
		parent.setParameterHasChanged(true);
	}
	@Override
	public boolean isCalculating() {
		// TODO Auto-generated method stub
		return false;
	}

//	public int cellLength() {
//		return masterRepeatSchemaPanel.cellLength();
//	}

	// MasterSchemaPanelParent -----------------------------------------------------
	@Override
	public void setAttachListContentInFeatureChooserPanel(ArrayList<IndexList> list) {
		featureChooserPanel.setAttachListContent(list);
		
	}

	// ContentAndClipBrowserParent methods -----------------------------------------
	
	public void updateFeatureChooserOptionLists(RepetitionAnalysis ra) {
		//System.out.println("(ContentAndClipBrowserParent)FeaturePickerGamePanel.updateFeatureChooserOptionLists() called....");
		featureChooserPanel.setOptionListContent(ra);
		
	}

	@Override
	public void setLinkedFieldsInContentBrowser(String clipPath, String chordsPath, ArrayList<Integer> list) {
		if (contentBrowser.isLinkedToMasterPanel()){
			contentBrowser.setClipFile(clipPath);
			contentBrowser.setChordsFile(chordsPath);
			contentBrowser.setCellLengthIndexList(list);
		}
		
	}
	@Override
	public void setLinkedCellLengthIndexInContentBrowser(ArrayList<Integer> cellLengthSelections) {
		if (contentBrowser != null && contentBrowser.isLinkedToMasterPanel()){
			contentBrowser.setCellLengthIndexList(cellLengthSelections);
		}
		
	}

//	@Override
//	public void setLinkedCellLengthIndexInContentBrowser(int cellLengthIndex) {
//		if (contentBrowser != null && contentBrowser.isLinkedToMasterPanel()){
//			contentBrowser.setCellLengthIndex(cellLengthIndex);
//		}
//		
//	}
	public RegisterInfo getRegisterInfo() {
		return registerInfoPanel.getRegisterInfo();
	}
	public boolean getCopyOverRule() {
		return copyOverPanel.getCopyOverRule();

	}
	public FeaturePicker getFeaturePicker() {
		return featureChooserPanel.getFeaturePicker();
	}
	public FeatureChunkRepeatSchema getMasterRepeatSchema() {
		if (masterRepeatSchemaPanel.getSelectedSchema() != null){
			return masterRepeatSchemaPanel.getSelectedSchema().fcrs;
		} else {
			return null;
		}
		
	}
	public void setOutputFormBarCount(int barCount) {
		masterRepeatSchemaPanel.setOutputFormBarCountAndRefresh(barCount);
		
	}
	@Override
	public void setMasterRepeatSchemaPanelBarCount(int barCount) {
		masterRepeatSchemaPanel.setOutputFormBarCountAndRefresh(barCount);
		
	}
	public String cellLengthsToString(){
		return masterRepeatSchemaPanel.cellLengthsToString();
	}
	@Override
	public void updateRSGraphic() {
		parent.updateRSGraphic();
		
	}
	public void removeSelfFromTabList() {
		fpParent.removeTab(this);
		setParameterHasChanged(true);
		updateRSGraphic();
	}
	public void addNewToRightOfSelf() {
		fpParent.addNewToRightOfTab(this);
		updateRSGraphic();
		
	}
	public void moveLeft() {
		fpParent.moveLeft(this);
		setParameterHasChanged(true);
		updateRSGraphic();
	}
	public void moveRight() {
		fpParent.moveRight(this);
		setParameterHasChanged(true);
		updateRSGraphic();
	}
	public void toggleOnOff() {
		if (isOn){
			isOn = false;
		} else {
			isOn = true;
		}
		tabPanel.setOn(isOn);
		setParameterHasChanged(true);
		updateSubPanelColours();
		updateRSGraphic();
	}
	public void updateSubPanelColours() {
		tabPanel.updateBackgroundColours();
		masterRepeatSchemaPanel.updateBackgroundColor();
		contentBrowser.updateBackgroundColor();
		registerInfoPanel.updateBackgroundColor();
		featureChooserPanel.testAndSetBackground();
	}
	@Override
	public boolean isOn() {
		return isOn;
	}
	


}
