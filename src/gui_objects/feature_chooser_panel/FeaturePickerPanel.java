package gui_objects.feature_chooser_panel;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
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
import gui_objects.main_ui.RepetitionAnalysisMainFrame;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_handler.FeatureHandler;
import repetition_analysis.feature_handler.Handler;
import repetition_analysis.feature_picker.FeaturePicker;
import repetition_analysis.gen_output.GenOutput;
import repetition_analysis.index_list.IndexList;
import repetition_analysis.info_objects.RegisterInfo;
import repetition_analysis.repeat_schema_list.SchemaListObject;
import repetition_analysis.repetition_generator.GenThree;

public class FeaturePickerPanel extends JPanel {
	
	private LiveClip contentClip = null;
	private LiveClip chordsClip = null;
	private JTextField contentNameField;
	private JTextField chordsNameField;
	private JTextPane textPane;
	private int cellLength = 3;		// default value
	private int[] cellLengthOptions = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};	// no theoretical upper limit, 1 may actuall be an option that would need to be tested
	private JComboBox cellLengthBox;
//	private RepetitionAnalysis ra = null;
	private FeaturePicker fp = new FeaturePicker(this);
	private DefaultListModel masterSelectionItems;
	private JList masterSelectionList;
//	private ListAndModel secondInfoPane;
//	private ListAndModel rhythmOptionList;
	private int[] rhythmFeatureOptions = new int[]{FeatureChunk.GAP_VALUE};
	FeatureChooser rhythmChooser = null;
	private int[] durationFeatureOptions = new int[]{FeatureChunk.ABSOLUTE_DURATION, FeatureChunk.STACCATO_PORTATO_TENUTO};
	FeatureChooser durationChooser = null;
	private int[] dynamicsFeatureOptions = new int[]{FeatureChunk.LOUD_SOFT_DYNAMICS, FeatureChunk.SIMPLE_DYNAMICS};
	FeatureChooser dynamicsChooser = null;
//	private int[] pitchFeatureOptions = new int[]{FeatureChunk.LOUD_SOFT_DYNAMICS, FeatureChunk.SIMPLE_DYNAMICS};
	FeatureChooser pitchChooser = null;
	private int[] contourFeatureOptions = new int[]{FeatureChunk.SIMPLE_MELODY_CONTOUR, FeatureChunk.HALF_SIMPLE_MELODY_CONTOUR};
	FeatureChooser contourChooser = null;
	GenThree gen = new GenThree();
	// these need to become panel members....
	private RegisterInfo ri = RegisterInfo.registerMap.get(RegisterInfo.TRUMPET);
	private double xover = 0.5;
	private Color onColor = Color.GREEN;
	private Color offColor = Color.GRAY;
	public static final String copyOverRuleTrueText = "copyOver=TRUE";
	public static final String copyOverRuleFalseText = "copyOver=FALSE";
	//.......................................
	
//	private ChordForm outputCF = null;
	private RepetitionAnalysisMainFrame mainFrame;

	/**
	 * Create the panel.
	 * @param parent 
	 */
	public FeaturePickerPanel(RepetitionAnalysisMainFrame parent) {
		this.mainFrame = parent;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{100, 0, 0};
		gridBagLayout.rowHeights = new int[]{20, 20, 20, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		setBackground(offColor);
		
		makeNameField();
		makeMasterSelectionPane();
		makeCellLength();
		setupCopyOverRuleToggle();
//		makeSecondaryInfoPane();
		makeRhythmOptionList();
		makeDurationOptionList();
		makeDynamicsOptionList();
		makePitchOptionList();
		makeContourOptionList();

	}
	public void bang(){
		//tests for completeness and then realculates
		//System.out.println("FeaturePickerPanel.bang()");
		if (fp.complete()){
			setBackground(onColor);
			if (gen == null){
				gen = new GenThree(fp, xover, ri);
			} else {
				gen.recalc(fp, xover, ri);
			}
			
		} else {
			setBackground(offColor);
		}
		mainFrame.bang();
	}
	public FeaturePicker getFp (){
		return fp;
	}
	private void setupCopyOverRuleToggle() {
		final JButton copyRuleButton = new JButton();
		if (gen.copyOverRule()){
			copyRuleButton.setText(copyOverRuleTrueText);
		} else {
			copyRuleButton.setText(copyOverRuleFalseText);
		}
		copyRuleButton.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
			    if (gen.copyOverRule() == true){
			    	gen.setCopyOverRule(false);
			    	copyRuleButton.setText(copyOverRuleFalseText);			    	
			    } else {
			    	gen.setCopyOverRule(true);
			    	copyRuleButton.setText(copyOverRuleTrueText);
			    }
			    bang();
			  } 
			});
		GridBagConstraints gbc_copyRule = new GridBagConstraints();
		gbc_copyRule.gridx = 0;
		gbc_copyRule.gridy = 7;
		gbc_copyRule.ipady = 15;
		gbc_copyRule.insets = new Insets(20, 20, 20, 20);
//		gbc_autoRender.anchor = GridBagConstraints.WEST;
		
		add(copyRuleButton, gbc_copyRule);
	}
	private void makeRhythmOptionList() {
		
		rhythmChooser = new FeatureChooser(fp, FeatureChooser.RHYTHM, "RHYTHM");
		GridBagConstraints gbc_rc = getGridBagConstraints(2, 0);
		gbc_rc.gridwidth = 1;
		gbc_rc.gridheight = 4;
		add(rhythmChooser, gbc_rc);
		updateRhythmOptions();
	}
	private void makeDurationOptionList() {
		
		durationChooser = new FeatureChooser(fp, FeatureChooser.DURATION, "DURATION");
		GridBagConstraints gbc_dc = getGridBagConstraints(2, 4);
//		gbc_rc.gridwidth = 1;
//		gbc_rc.gridheight = 4;
		add(durationChooser, gbc_dc);
		updateDurationOptions();
	}
	private void makeDynamicsOptionList() {
		
		dynamicsChooser = new FeatureChooser(fp, FeatureChooser.DYNAMICS, "DYNAMICS");
		GridBagConstraints gbc_dyc = getGridBagConstraints(2, 5);
//		gbc_rc.gridwidth = 1;
//		gbc_rc.gridheight = 4;
		add(dynamicsChooser, gbc_dyc);
		updateDynamicsOptions();
	}
	private void makePitchOptionList() {
		
		pitchChooser = new FeatureChooser(fp, FeatureChooser.PITCH, "PITCH");
		GridBagConstraints gbc_pc = getGridBagConstraints(2, 6);
//		gbc_rc.gridwidth = 1;
//		gbc_rc.gridheight = 4;
		add(pitchChooser, gbc_pc);
		updatePitchOptions();
	}
	private void makeContourOptionList() {
		
		contourChooser = new FeatureChooser(fp, FeatureChooser.CONTOUR, "CONTOUR");
		GridBagConstraints gbc_cc = getGridBagConstraints(2, 7);
//		gbc_rc.gridwidth = 1;
//		gbc_rc.gridheight = 4;
		add(contourChooser, gbc_cc);
		updateContourOptions();
	}

	private void updateContourOptions(){
		ArrayList<FeatureChunkRepeatSchema> fList = fp.getFeatureList(contourFeatureOptions);
		//System.out.println("dynamics.fList.size()=" + fList.size());
		if (contourChooser != null){
			contourChooser.setOptionList(fList);
		}
	}

	private void updatePitchOptions(){
		ArrayList<FeatureChunkRepeatSchema> fList = fp.getFeatureList(getPitchFeatureOptions(fp));
		//System.out.println("dynamics.fList.size()=" + fList.size());
		if (pitchChooser != null){
			pitchChooser.setOptionList(fList);
		}
	}

	private void updateDynamicsOptions(){
		ArrayList<FeatureChunkRepeatSchema> fList = fp.getFeatureList(dynamicsFeatureOptions);
		//System.out.println("dynamics.fList.size()=" + fList.size());
		if (dynamicsChooser != null){
			dynamicsChooser.setOptionList(fList);
		}
	}
	private void updateDurationOptions(){
		ArrayList<FeatureChunkRepeatSchema> fList = fp.getFeatureList(durationFeatureOptions);
		//System.out.println("duration.fList.size()=" + fList.size());
		if (durationChooser != null){
			durationChooser.setOptionList(fList);
		}
	
	}
	private void updateRhythmOptions(){
		ArrayList<FeatureChunkRepeatSchema> fList = fp.getFeatureList(rhythmFeatureOptions);
		//System.out.println("rhythm.fList.size()=" + fList.size());
		if (rhythmChooser != null){
			rhythmChooser.setOptionList(fList);
		}
		
	}
//	private void makeSecondaryInfoPane() {
//		secondInfoPane = new ListAndModel();
//		GridBagConstraints gbc_sip = getGridBagConstraints(2, 2);
//		gbc_sip.fill = GridBagConstraints.BOTH;
//		JScrollPane sp = new JScrollPane();
//		sp.setPreferredSize(new Dimension(150, 100));
//		sp.setViewportView(secondInfoPane.list());
//		add(sp, gbc_sip);
//	}
	private void makeCellLength() {
		JLabel cellLengthLabel = new JLabel("cell length");
		GridBagConstraints gbc_cll = getGridBagConstraints(0, 2);
		add(cellLengthLabel, gbc_cll);
		
		cellLengthBox = new JComboBox();
		cellLengthBox.setPreferredSize(new Dimension(40, 25));
		cellLengthBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cellLength = (int)cellLengthBox.getSelectedItem();
				recalculateRA();
			}
		});
		GridBagConstraints gbc_clb = getGridBagConstraints(1, 2);
		
		
		for (int i: cellLengthOptions){
			cellLengthBox.addItem(i);
		}
		add(cellLengthBox, gbc_clb);
	}
	protected void setRepetitionAnalysis(RepetitionAnalysis repetitionAnalysis) {
		this.fp.setRepetitionAnalysis(repetitionAnalysis);
		
	}
	public void setInfoPaneText(String str){
		textPane.setText(str);
		textPane.setCaretPosition(0);
		
	}

	private void makeMasterSelectionPane() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(200, 250));
		GridBagConstraints gbc_scrollPane =  getGridBagConstraints(0, 3);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridheight = 3;
		gbc_scrollPane.gridwidth = 2;
		add(scrollPane, gbc_scrollPane);
		
//		JPanel panel = new JPanel(new BorderLayout());
//		scrollPane.setViewportView(panel);
		
//		textPane = new JTextPane();
//		panel.add(textPane);
		
		masterSelectionItems = new DefaultListModel();
		masterSelectionList = new JList(masterSelectionItems);
		masterSelectionList.addMouseListener(new MouseAdapter() {
	        public void mouseClicked(MouseEvent e) {
	            if (e.getClickCount() == 1) {
	                //al.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "ENTER"));
	            	int index = masterSelectionList.getSelectedIndex();
	            	//System.out.println("index=" + index);
	            	FeatureChunkRepeatSchema fcrs = fp.getRepeatSchema(index);
	            	fp.setMasterRepeatSchema(fcrs);
	            	setAttachListsModelContent(fcrs.secondaryIndexList());
	            	fp.bang();
	            }
	        }
	    });
		scrollPane.setViewportView(masterSelectionList);
	}

	protected void setAttachListsModelContent(ArrayList<IndexList> arrayList) {
		rhythmChooser.setAttachList(arrayList);
		durationChooser.setAttachList(arrayList);
		dynamicsChooser.setAttachList(arrayList);
		pitchChooser.setAttachList(arrayList);
		contourChooser.setAttachList(arrayList);
		
	}
	private void makeNameField() {
		JLabel contentLabel = new JLabel("Content clip name");
		GridBagConstraints gbc_contentLabel = getGridBagConstraints(0, 0);
		add(contentLabel, gbc_contentLabel);
		
		contentNameField = new JTextField();
		GridBagConstraints gbc_textField = getGridBagConstraints(1, 0);
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		add(contentNameField, gbc_textField);
		contentNameField.setColumns(10);
		
		JLabel chordLabel = new JLabel("Chord clip name");
		GridBagConstraints gbc_chordLabel = getGridBagConstraints(0, 1);
		add(chordLabel, gbc_chordLabel);
		
		chordsNameField = new JTextField();
		GridBagConstraints gbc_chords = getGridBagConstraints(1, 1);
		gbc_chords.insets = new Insets(0, 0, 5, 0);
		gbc_chords.fill = GridBagConstraints.HORIZONTAL;
		add(chordsNameField, gbc_chords);
		chordsNameField.setColumns(10);
		
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
	public void setContentClip(LiveClip lc) {
		contentClip = lc;
		contentNameField.setText(lc.name);
		recalculateRA();
		
	}
	public void setChordsClip(LiveClip lc) {
		if (lc == null){
			chordsClip = null;
			chordsNameField.setText("no chords selected");
			recalculateRA();
		} else {
			chordsClip = lc;
			chordsNameField.setText(lc.name);
			recalculateRA();
		}
		
		
	}
	private void recalculateRA() {
		if (contentClip != null){
			if (chordsClip == null){
				setRepetitionAnalysis(new RepetitionAnalysis(contentClip, (int)cellLengthBox.getSelectedItem()));
			} else {
				setRepetitionAnalysis(new RepetitionAnalysis(contentClip, (int)cellLengthBox.getSelectedItem(), chordsClip));
			}	
		}
		updateData();
		//rebuildSelectionListsFromIndexLists();  // rebuilds selectedLists. may work without doing this....
	}
	private void rebuildSelectionListsFromIndexLists() {
		rhythmChooser.rebuildSelectedListFromIndexList();
		durationChooser.rebuildSelectedListFromIndexList();
		dynamicsChooser.rebuildSelectedListFromIndexList();
		pitchChooser.rebuildSelectedListFromIndexList();
		contourChooser.rebuildSelectedListFromIndexList();
		
	}
	private void updateData() {
//		String str = "";
//		if (ra != null){
//			for (SchemaListObject slo: ra.getSchemaList().schemaList){
//				str += slo.toShortString() + "\n";
//			}
//			setInfoPaneText(str);
//		}
		//System.out.println("updateData();");
		ArrayList<String> list = new ArrayList<String>();
		if (fp.hasRepetitionAnalysis()){
			for (SchemaListObject slo: fp.getSchemaList().schemaList){
//				list.add(slo.toShortString());
				list.add(slo.repeatsToString());
				
			}
		} else {
			list.add("no options at this time....");
		}
		setSelectionListOptions(list);
		updateRhythmOptions();
		updateDurationOptions();
		updateDynamicsOptions();
		updatePitchOptions();
		updateContourOptions();
	}
	private void setSelectionListOptions(ArrayList<String> list) {
		masterSelectionItems.clear();
		for (String str: list){
			masterSelectionItems.addElement(str);
		}
		
	}
	public void setOutputChordForm(ChordForm cForm){
		fp.setOutputChordForm(cForm);
		updateData();
		
	}
	private int[] getPitchFeatureOptions(FeaturePicker fp) {
		ArrayList<Integer> iArr = new ArrayList<Integer>();
		for (Integer index: fp.getHandlerMap().keySet()){
			FeatureHandler fh = fp.getHandlerMap().get(index);
//			if (fh.needsAnalysisChordForm() == ra.hasChordsClip() && fh.needsOutputChordForm() == outputCF.hasNoteListContent()){
//				iArr.add(index);
//			}
			if (fh.type() == Handler.MELODY_TYPE && !fh.isPolyphonic()){		
				boolean add = true;
				if (fh.needsAnalysisChordForm() && !fp.hasChordsClip()) add = false;
				if (fh.needsOutputChordForm() && !fp.outputCF().hasNoteListContent()) add = false;
				if (add){
					iArr.add(index);
				}
			}
			
		}
		int[] pitchFeatureOptions = new int[iArr.size()];
		for (int i = 0; i < iArr.size(); i++){
			pitchFeatureOptions[i] = iArr.get(i);
		}
		return pitchFeatureOptions;
	}
//	public TreeMap<Double, FinalListNote> getOutputTreeMap() {
//		return gen.getFinalNoteMap();
//	}
	public GenOutput getGenOutput(){
		GenOutput go = new GenOutput(gen.getFinalNoteMap(), gen.latestRenderName(), fp.outputCF(), fp, gen.copyOverRule());
		return go;
	}
	public String latestRenderName(){
		return gen.latestRenderName();
	}
//	public void rerender() {
//		bang();
//		
//	}
	public String getCopyOverRuleText(){
		if (gen.copyOverRule()){
			return copyOverRuleTrueText;
		} else {
			return copyOverRuleFalseText;
		}
	}
	

}
