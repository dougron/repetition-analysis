package gui_objects.main_ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.combo_variables.DoubleAndString;
import GUIObjects.FileBrowserParent;
import ResourceUtils.ChordForm;
import XMLMaker.MXM;
import XMLMaker.MusicXMLMaker;
import XMLMaker.XMLKeyZone;
import XMLMaker.XMLTimeSignatureZone;
import gui_objects.chord_panel.ChordFormGamePanel;
import gui_objects.feature_chooser_panel.FeaturePickerGamePanel;
import gui_objects.feature_chooser_panel.FeaturePickerManagerParent;
import gui_objects.feature_chooser_panel.FeaturePickerPanel;
import gui_objects.render_panel.RenderGamePanel;
import gui_objects.repeat_schema_panel.RepeatSchemaGraphic;
import ra_utils.ClipInjectorObject;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_picker.FeaturePicker;
import repetition_analysis.gen_output.CombineGenOutputs;
import repetition_analysis.gen_output.GenOutput;
import repetition_analysis.info_objects.RegisterInfo;
import repetition_analysis.repetition_generator.GenThree;

public class RepetitionAnalysisGamePanel extends JPanel 
implements 
RepetitionAnalysisParent, 
FeaturePickerManagerParent,
Runnable, 
FileBrowserParent 

{

	private static final double DEFAULT_RESOLUTION = 0.5;		// this needs to be globalized in the system, at some point
	private boolean parameterHasChanged = false;
	private boolean isCalculating = false;
	private long DELAY = 100;
	private Thread animator;
	Color bg = new Color (100, 100, 120);
	ChordFormGamePanel cfPanel;
	private String testFilePath = "D:/Documents/repos/ChordProgressionTestFiles/DescendingFlamenco.chords.liveclip";
	private String musicInfoSheetPath = "D:/Documents/miscForBackup/Repetition text files/RepetitionAnalysisMainFrameInfoSheets/";
	private String liveclipFolderPath = "D:/Documents/miscForBackup/Repetition text files/RepetitionAnalysisMainFrameLiveClips/";
	private String musicXMLFolderPath = "D:/Documents/miscForBackup/Repetition text files/RepetitionAnalysisMainFrameMusicXML/";
	

	//	MasterRepeatSchemaGamePanel master;
//	private FeaturePickerGamePanel fpPanel;
	private ArrayList<FeaturePickerGamePanel> fpPanelList = new ArrayList<FeaturePickerGamePanel>();
	private JTabbedPane fpTabPane;
	private JButton testButton;
	private RenderGamePanel renderPanel;
	private GenThree gen = new GenThree();
	private double xover = 0.5;		// this will need a panel eventually
	private GenOutput currentGenOutput = null;
	ClipInjectorObject cio;
	private int liveTrackIndex;   		// clip inject track
	private int liveClipIndex;
	private int masterRepeatSchemaXMLTextSize = 6;
	private int masterRepeatSchemaXMLYOffset = 50;
	
	private boolean copyOverStartRule = false;		// eventually these will need a selection panel
	private boolean copyOverEndRule = true;
	private RepeatSchemaGraphic rsGraphic;
	private static final int DEFAULT_RSGRAPHIC_LINELENGTH = 4;
	private int tabPaneXsize = 1100;
	private int tabPaneYsize = 830;
	private JScrollPane scrollPane;
	
	
	public RepetitionAnalysisGamePanel(){
		setParamsFromPropertiesFile();
		setupClipInjectorObject();
		setupLayout();
		setBackground(Color.gray);
//		addTestButton();
		addChordFormPanel();
//		addMasterRepeatSchemaPanel();		// this is now in the RepetitionSchemaPanel
//		addFeaturePickerPanel();
		addMasterRepeatSchemaGraphicPanel();
		addFeaturePickerTabbedPanel();
		addRenderPanel();
		
		
	}

	

	public String getLiveclipFolderPath (){
		return liveclipFolderPath;
	}

// private --------------------------------------------------------------------------------


	private void setParamsFromPropertiesFile (){
		try (InputStream input = new FileInputStream(RepetitionAnalysisGameFrame.propertiesPath))
		{
			Properties props = new Properties();
			props.load(input);
			testFilePath = props.getProperty("testContentPath");
			musicInfoSheetPath = props.getProperty("music_info_save_directory");
			liveclipFolderPath = props.getProperty("live_clip_save_directory");
			musicXMLFolderPath = props.getProperty("musicxml_save_directory");
			liveTrackIndex = Integer.parseInt(props.getProperty("live_track_index"));
			liveClipIndex = Integer.parseInt(props.getProperty("live_clip_index"));
		}
		catch (IOException ex)
		{
			
		}	
	}
	
	
//	private void addClipAndChordBrowser() {
//		ClipAndChordBrowser cncBrowser = new ClipAndChordBrowser(this, FileBrowserPanel.BUTTONS_BELOW);
//		GridBagConstraints gbc_cncBrowser = new GridBagConstraints();
//		gbc_cncBrowser.gridx = 2;
//		gbc_cncBrowser.gridy = 0;
//		add(cncBrowser, gbc_cncBrowser);
//		
//	}
	private void setupClipInjectorObject() {
		cio = new ClipInjectorObject(liveTrackIndex, liveClipIndex, ClipInjectorObject.ofTrackType);
		cio.sendResetInitializationMessage();
		cio.sendInitializationMessage();
	}
	private void addRenderPanel() {
		renderPanel = new RenderGamePanel();
		GridBagConstraints gbc_renderPanel = new GridBagConstraints();
		gbc_renderPanel.gridx = 0;
		gbc_renderPanel.gridy = 1;
		gbc_renderPanel.fill = GridBagConstraints.HORIZONTAL;
		add(renderPanel, gbc_renderPanel);
	}
//	private void addFeaturePickerPanel() {
//		fpPanel = new FeaturePickerGamePanel(this);
//		GridBagConstraints gbc_fpPanel = new GridBagConstraints();
//		gbc_fpPanel.gridx = 1;
//		gbc_fpPanel.gridy = 0;
//		gbc_fpPanel.gridheight = 2;
//		add(fpPanel, gbc_fpPanel);
//		
//	}
	private void addFeaturePickerTabbedPanel() {
		fpPanelList.add(new FeaturePickerGamePanel(this, this));
		fpPanelList.add(new FeaturePickerGamePanel(this, this));
		GridBagConstraints gbc_fpPanel = new GridBagConstraints();
		gbc_fpPanel.gridx = 1;
		gbc_fpPanel.gridy = 0;
		gbc_fpPanel.gridheight = 2;
		fpTabPane = new JTabbedPane();
		fpTabPane.setPreferredSize(new Dimension(tabPaneXsize, tabPaneYsize));
		add(fpTabPane, gbc_fpPanel);
		reloadFPTabPane();
	}
	private void reloadFPTabPane() {
		fpTabPane.removeAll();
		int count = 0;
		for (FeaturePickerGamePanel fp: fpPanelList){
			fpTabPane.addTab("" + fp.id(), fp);
//			fp.setTabLabelText("" + fp.id());
			fpTabPane.setTabComponentAt(count, fp.tabPanel());
			count++;
		}
		this.fpTabPane.add("rsView", rsGraphic);

	}

	//	private void addMasterRepeatSchemaPanel() {
//		master = new MasterRepeatSchemaGamePanel(this);
//		GridBagConstraints gbc_masterPanel = new GridBagConstraints();
//		gbc_masterPanel.gridx = 1;
//		gbc_masterPanel.gridy = 0;
//		add(master, gbc_masterPanel);
//		
//	}
	private void addChordFormPanel() {
		cfPanel = new ChordFormGamePanel(this);
		GridBagConstraints gbc_cfPanel = new GridBagConstraints();
		gbc_cfPanel.gridx = 0;
		gbc_cfPanel.gridy = 0;
		add(cfPanel, gbc_cfPanel);
		
	}
	private void setupLayout(){
		setBackground(bg);
		GridBagLayout layout = new GridBagLayout();
		layout.rowHeights = new int[]{500, 20, 75};
		layout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(layout);
	}
	
	private void addTestButton() {
		testButton = new JButton("set new parameter");
		testButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				cfPanel.setSelectedChordFormFromPath(testFilePath);
			}
		});
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.gridx = 0;
		gbc_button.gridy = 1;
		add(testButton, gbc_button);
		
		addTestDisableButton();
	}

	private void addTestDisableButton() {
		JButton button = new JButton("toggle disable set new parameter");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if (testButton.isEnabled()){
					testButton.setEnabled(false);
				} else {
					testButton.setEnabled(true);
				}
			}
		});
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.gridx = 0;
		gbc_button.gridy = 2;
		add(button, gbc_button);
		
	}











	@Override
	public void run() {
		long beforeTime, timeDiff, sleep;
		beforeTime = System.currentTimeMillis();


		while (true) {		
		    cycle();
		    repaint();
		    timeDiff = System.currentTimeMillis() - beforeTime;
		    sleep = DELAY  - timeDiff;
		
		    if (sleep < 0) {
		        sleep = 2;
		    }
		
		    try {
		        Thread.sleep(sleep);
		    } catch (InterruptedException e) {
		        System.out.println("Interrupted: " + e.getMessage());
		    }
		
		    beforeTime = System.currentTimeMillis();
		}
		
	}

	private void cycle() {
		String str = "cycle() called....parameterHasChanged=" + parameterHasChanged;
		for (FeaturePickerGamePanel fpgp: fpPanelList){
			str += " cellLengths=" +  fpgp.cellLengthsToString() + "\n";
		}
		
		//System.out.println(str);
		if (parameterHasChanged && !isCalculating){
			renderOutput();
		}		
	}

//	private void renderOutput() {
	// this one for use with single fpPanel
//		parameterHasChanged = false;
//		isCalculating = true;
//		
//		System.out.println("RepetionAnalysisGamePanel.renderOutput() called....");
//		if (cfPanel.hasChordForm() && fpPanel.hasCompleteData()){
//			ChordForm outputCF = cfPanel.cf();
//			RegisterInfo ri = fpPanel.getRegisterInfo();
//			FeaturePicker fp = fpPanel.getFeaturePicker();
//			fp.setMasterRepeatSchema(fpPanel.getMasterRepeatSchema());
//			fp.setCopyOverRule(fpPanel.getCopyOverRule());
//			boolean copyOverRule = fpPanel.getCopyOverRule();
//			GenOutput go = gen.getGenOutput(outputCF, xover, ri, fp, copyOverRule);
//			//System.out.println("GenOutput generated........\n" + go.lc().toString());
//			doit(go);
//		}
//		
//		
//		isCalculating = false;		
//	}
	private void renderOutput() {
		parameterHasChanged = false;
		isCalculating = true;
		
		System.out.println("RepetionAnalysisGamePanel.renderOutput() called....");
		if (cfPanel.hasChordForm() && fpTabPanelHasData()){
			ChordForm outputCF = cfPanel.cf();
			ArrayList<GenOutput> goList = new ArrayList<GenOutput>();
			for (FeaturePickerGamePanel fpgp: fpPanelList) {
				if (fpgp.isOn() && fpgp.hasCompleteData()) {
					RegisterInfo ri = fpgp.getRegisterInfo();
					FeaturePicker fp = fpgp.getFeaturePicker();
					fp.setMasterRepeatSchema(fpgp.getMasterRepeatSchema());
					fp.setCopyOverRule(fpgp.getCopyOverRule());
					boolean copyOverRule = fpgp.getCopyOverRule();
					goList.add(gen.getGenOutput(outputCF, xover, ri, fp, copyOverRule));
				}
			}
			
			GenOutput go = makeFinalGenOutput(goList);
			if (currentGenOutput == null){
				doit(go);
			} else if (!currentGenOutput.isSameAs(go)){
				doit(go);
			}
			
		}
		
		
		isCalculating = false;		
	}
	private GenOutput makeFinalGenOutput(ArrayList<GenOutput> goList) {
		if (goList.size() == 1){
			return goList.get(0);
		} else {
			return CombineGenOutputs.getFinalGenOutput(goList, copyOverStartRule, copyOverEndRule);
		}

	}

	private boolean fpTabPanelHasData() {
		for (FeaturePickerGamePanel fp: fpPanelList){
			if (fp.hasCompleteData()) return true;
		}
		return false;
	}

	private void doit(GenOutput go){
		System.out.println("rendered file: " + go.renderName());
		currentGenOutput = go;
		doOutputRender(currentGenOutput);
		saveInfoSheet(go);
	}
	private void saveInfoSheet(GenOutput go) {
		String outputPath = musicInfoSheetPath + go.renderName() + ".info";
		if (go.hasFeaturePicker()) go.fp().saveInfoSheet(outputPath);
	}
	private void doOutputRender(GenOutput go) {
		if (renderPanel.injectIntoLive()){
			cio.sendClip(go.lc());
		}
		updateRenderName(go);
		//System.out.println("clip rendered");
		if (renderPanel.saveAsLiveClip()){
			renderLiveClipToTextFile(go);			
		}
		//System.out.println("text file rendered");
		if (renderPanel.renderMusicXML()){
			renderMusicXML(go);
			//System.out.println("xml from liveclip rendered");
//			ArrayList<FinalListNote> list = makeFinalNoteList(go);
			renderMusicXMLFromFinalNoteList(go);
			//System.out.println("xml from fnlList rendered");
			renderGroupXMLs(go);
			//System.out.println("group xml rendered");
		}
	}
	private void updateRenderName(GenOutput go) {
		renderPanel.setRenderName(go.renderName());
		
	}

	private void renderGroupXMLs(GenOutput go) {
		renderPanel.manageXMLGroupMap(go);
		renderTheGoMapKeyThatHasChangedSize();
		renderPanel.updateGoMapOldSize();
	}
	private void renderTheGoMapKeyThatHasChangedSize() {
		HashMap<String, ArrayList<GenOutput>> goMap = renderPanel.getGoMap();
		for (String key: goMap.keySet()){
			if (goMap.get(key).size() != renderPanel.getOldSizeOfGoMapValue(key)){
				renderMultiPartXML(goMap.get(key), key);
				renderMultiPartXMLFromFLNMap(goMap.get(key), key);
			}
		}
		
	}
	private void renderMultiPartXMLFromFLNMap(ArrayList<GenOutput> arrayList, String key) {
		MusicXMLMaker mxm = new MusicXMLMaker(MXM.KEY_OF_C);
		//System.out.println("lc.barCount()=" + lc.barCount());
		GenOutput go = arrayList.get(0);
//		LiveClip lc = lcList.get(0);
		mxm.measureMap.addNewTimeSignatureZone(new XMLTimeSignatureZone(go.lc().signatureNumerator, go.lc().signatureDenominator, go.lc().barCount())); 
		mxm.keyMap.addNewKeyZone(new XMLKeyZone(MXM.KEY_OF_C, go.lc().barCount()));
		//adjustOctave(lc, -1);
		//adjustOctave(outputClip, -1);
		for (int i = 0; i < arrayList.size();i++){
			go = arrayList.get(i);
			String name = go.renderName();
//			name = name.substring(0,  11) + "\n" + name.substring(11);
			mxm.addPart(name, go.flnList());
			addChordSymbols(name, mxm, go.outputCF());
//			addMasterRepeatSchemaTextDirection(name, mxm, fpPanel.fp);
			addMasterRepeatSchemaTextDirection(name, mxm, go.fp());
			addCopyOverRuleTextDirection(name, mxm, go.copyOverRule());
		}
		String keyName = key.replace("/", "-");
		keyName = keyName.replace("-", "_");
//		Date dateobj = new Date();
		String fileName = arrayList.get(arrayList.size() - 1).renderName();

		String outputPath = musicXMLFolderPath + "multiFLNList_" + keyName + "_bars_" + arrayList.size() + "_items_" + fileName + ".xml";
		//System.out.println(outputPath);
		mxm.makeXML(outputPath);
		
	}




	private void renderMultiPartXML(ArrayList<GenOutput> arrayList, String key) {
		MusicXMLMaker mxm = new MusicXMLMaker(MXM.KEY_OF_C);
		//System.out.println("lc.barCount()=" + lc.barCount());
		LiveClip lc = arrayList.get(0).lc();
		mxm.measureMap.addNewTimeSignatureZone(new XMLTimeSignatureZone(lc.signatureNumerator, lc.signatureDenominator, lc.barCount())); 
		mxm.keyMap.addNewKeyZone(new XMLKeyZone(MXM.KEY_OF_C, lc.barCount()));
		//adjustOctave(lc, -1);
		//adjustOctave(outputClip, -1);
		
		for (GenOutput go: arrayList){
			String partName = go.renderName();
//			partName = partName.substring(0,  11) + "\n" + partName.substring(11);
			mxm.addPart(partName, go.lc());
		}
		String keyName = key.replace("/", "-");
		keyName = keyName.replace("-", "_");
//		Date dateobj = new Date();
		String fileName = arrayList.get(arrayList.size() - 1).renderName();

		String outputPath = musicXMLFolderPath + keyName + "_bars_" + arrayList.size() + "_items_" + fileName + ".xml";
//		System.out.println(outputPath);
		mxm.makeXML(outputPath);
		
	}



	private void renderMusicXMLFromFinalNoteList(GenOutput go) {
		MusicXMLMaker mxm = new MusicXMLMaker(MXM.KEY_OF_C);
		//System.out.println("lc.barCount()=" + lc.barCount());
		mxm.measureMap.addNewTimeSignatureZone(new XMLTimeSignatureZone(go.lc().signatureNumerator, go.lc().signatureDenominator, go.lc().barCount())); 
		mxm.keyMap.addNewKeyZone(new XMLKeyZone(MXM.KEY_OF_C, go.lc().barCount()));
		//adjustOctave(lc, -1);
		//adjustOctave(outputClip, -1);
		String partName = go.lc().name;
		//partName = partName.substring(0,  11) + "\n" + partName.substring(11);
		mxm.addPart(partName, go.flnList());
		addChordSymbols(partName, mxm, go.outputCF());
		addMasterRepeatSchemaTextDirection(partName, mxm, go.fp());
		addCopyOverRuleTextDirection(partName, mxm, go.copyOverRule());
//		addChordSymbols(partName, mxm, cfPanel.cf());
		String outputPath = musicXMLFolderPath + "finalListNote" + go.renderName() + ".xml";
		mxm.makeXML(outputPath);
	}
	private void addCopyOverRuleTextDirection(String name, MusicXMLMaker mxm, boolean bool) {
		String str;
		if (bool == true){
			str = FeaturePickerPanel.copyOverRuleTrueText;
		} else {
			str = FeaturePickerPanel.copyOverRuleFalseText;
		}
		mxm.addTextDirection(name, str, 0.0, MXM.PLACEMENT_ABOVE, masterRepeatSchemaXMLTextSize, masterRepeatSchemaXMLYOffset);

		
	}
	private void addMasterRepeatSchemaTextDirection(String partName, MusicXMLMaker mxm, FeaturePicker fp) {
		
		if (fp != null) {
			mxm.addTextDirection(partName, fp.getMasterRepeatSchema().positionsToString().replace(",", ", "), 0.0,
					MXM.PLACEMENT_ABOVE, masterRepeatSchemaXMLTextSize, masterRepeatSchemaXMLYOffset);
			mxm.addTextDirection(partName,
					fp.getMasterRepeatSchema().barXOverPosArrayToString(fp.xover()).replace(",", ", "), 0.0,
					MXM.PLACEMENT_ABOVE, masterRepeatSchemaXMLTextSize, masterRepeatSchemaXMLYOffset);
		}

	}
	private void renderMusicXML(GenOutput go) {
		MusicXMLMaker mxm = new MusicXMLMaker(MXM.KEY_OF_C);
		//System.out.println("lc.barCount()=" + lc.barCount());
		mxm.measureMap.addNewTimeSignatureZone(new XMLTimeSignatureZone(go.lc().signatureNumerator, go.lc().signatureDenominator, go.lc().barCount())); 
		mxm.keyMap.addNewKeyZone(new XMLKeyZone(MXM.KEY_OF_C, go.lc().barCount()));
		//adjustOctave(lc, -1);
		//adjustOctave(outputClip, -1);
		
		String partName = go.lc().name;
		//partName = partName.substring(0,  11) + "\n" + partName.substring(11);
		mxm.addPart(partName, go.lc());
		
		addChordSymbols(partName, mxm, go.outputCF());
		String outputPath = musicXMLFolderPath + go.renderName() + ".xml";
		mxm.makeXML(outputPath);
		
	}
	private void addChordSymbols(String partName, MusicXMLMaker mxm, ChordForm cf) {
		for (DoubleAndString das: cf.getListOfSimpleKeyChordAnalysis()){
			mxm.addTextDirection(partName, das.str, das.d, MXM.PLACEMENT_ABOVE);
		}
		
	}
	private void renderLiveClipToTextFile(GenOutput go) {
		String filepath = liveclipFolderPath + "RAMainFrameOutput_" + go.renderName() + ".liveclip";
		//System.out.println(lc.getClipAsTextFile());
		try {
			FileWriter fw = new FileWriter(filepath);
			BufferedWriter bw = new BufferedWriter(fw);
//			System.out.println("writeLiveClipToFile called:");
//			System.out.println(lc.toString());
			String str = go.lc().getClipAsTextFile();
//			System.out.println(str);
			bw.write(str);
			bw.close();
			fw.close();
		} catch (IOException ex){
			System.out.println("RepetitionAnlysisMainFrame.renderLiveClipToTextFile() failed to work");
			System.out.println(ex.getMessage());
		}
		
	}
	
// overridden stuff -----------------------------------------------------------
	@Override
	public void addNotify() {
		super.addNotify();
		animator = new Thread(this);
		animator.start();
	}
// RepetitionAnalysisParent methods ----------------------------------------------------------------
	@Override
	public ChordForm cf() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean parameterHasChanged() {
		return parameterHasChanged;
	}

	@Override
	public void setParameterHasChanged(boolean b) {
		parameterHasChanged = b;
		//System.out.println("RepetitionAnalysisGamePanel.setParameterHasChanged() called");
	}

	@Override
	public boolean isCalculating() {
		return isCalculating ;
	}

	@Override
	public void setMasterRepeatSchemaPanelBarCount(int barCount) {
		//System.out.println("RepetitionAnalysisGamePanel.setMasterRepeatSchemaPanelBarCount() called....");
		for (FeaturePickerGamePanel fpPanel: fpPanelList){
			fpPanel.setOutputFormBarCount(barCount);
		}
		
		
	}
	private void addMasterRepeatSchemaGraphicPanel() {
		
		rsGraphic = new RepeatSchemaGraphic();
//		GridBagConstraints gbc_rsGraphic = new GridBagConstraints();
//		gbc_rsGraphic.gridx = 0;
//		gbc_rsGraphic.gridy = 2;
//		gbc_rsGraphic.gridwidth = 2;
//		gbc_rsGraphic.fill = GridBagConstraints.HORIZONTAL;
		//gbc_rsGraphic.fill = GridBagConstraints.VERTICAL;
//		updateGraphic();
		
//		scrollPane = new JScrollPane(rsGraphic); 
//		scrollPane.setPreferredSize(new Dimension(this.getWidth(), 100));
//		add(scrollPane, gbc_rsGraphic);
	}
	@Override
	public void updateRSGraphic() {
		
		updateGraphic();

	}
	private void updateGraphic(){
		if (this.cfPanel.hasChordForm()){
			System.out.println("RepetitionAnalysisGameFrame.updateRSGraphic called......");
			ArrayList<Double[]> posList = new ArrayList<Double[]>();
			for (FeaturePickerGamePanel fpgp: fpPanelList){
				FeatureChunkRepeatSchema mrs = fpgp.getMasterRepeatSchema();
				if (mrs != null){
					posList.add(makeDoubleArray(mrs.posArray()));
				} else {
					posList.add(new Double[]{});
				}
			}
			double totalLength = cfPanel.cf().totalLength();
			double barLen = cfPanel.cf().barLength();
			rsGraphic.setGraphic(posList, totalLength, DEFAULT_RESOLUTION, barLen, DEFAULT_RSGRAPHIC_LINELENGTH);
			rsGraphic.revalidate();
			rsGraphic.repaint();
		} else {
			rsGraphic.clear();
		}
	}



	private Double[] makeDoubleArray(double[] arr) {
		Double[] dArr = new Double[arr.length];
		for (int i = 0; i < arr.length; i++){
			dArr[i] = arr[i];
		}
		return dArr;
	}

	// FileBrowserParent methods --------------------------------------------------------------------
	@Override
	public void fileBrowserIsUpdated() {
		System.out.println("RepetitionAnalysisGamePanel.fileBrowserIsUpdated() called...");
		
	}
// ----------------------------------------------------------------------------------------------


// FeaturePickerManagerParent methods ---------------------------------------------------------------
	@Override
	public void removeTab(FeaturePickerGamePanel fpPanel) {
		if (fpPanelList.size() > 1){
			fpPanelList.remove(fpPanel);
		}
		
		reloadFPTabPane();
		
	}



	@Override
	public void addNewToRightOfTab(FeaturePickerGamePanel fpPanel) {
		int index = fpPanelList.indexOf(fpPanel);
		fpPanelList.add(index + 1, new FeaturePickerGamePanel(this, this));
		reloadFPTabPane();
	}



	@Override
	public void moveLeft(FeaturePickerGamePanel fpPanel) {
		int index = fpPanelList.indexOf(fpPanel);
		if (index > 0){
			Collections.swap(fpPanelList, index, index - 1);
		}
		reloadFPTabPane();
	}



	@Override
	public void moveRight(FeaturePickerGamePanel fpPanel) {
		int index = fpPanelList.indexOf(fpPanel);
		if (index < fpPanelList.size() - 1){
			Collections.swap(fpPanelList, index, index + 1);
		}
		reloadFPTabPane();
	}





// ---------------------------------------------------------------------------------------------------
	

	
}
