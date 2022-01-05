package gui_objects.main_ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileFilter;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.combo_variables.DoubleAndString;
import GUIObjects.FileBrowserFilter;
import ResourceUtils.ChordForm;
import XMLMaker.MXM;
import XMLMaker.MusicXMLMaker;
import XMLMaker.XMLKeyZone;
import XMLMaker.XMLTimeSignatureZone;
import gui_objects.LiveClipFileFilter;
import gui_objects.chord_panel.ChordFormPanel;
import gui_objects.chord_panel.ChordsLiveClipFileFilter;
import gui_objects.feature_chooser_panel.FeaturePickerPanel;
import ra_utils.ClipInjectorObject;
import repetition_analysis.feature_picker.FeaturePicker;
import repetition_analysis.gen_output.GenOutput;

public class RepetitionAnalysisMainFrame extends JFrame {
	
//	private String homeDirectory = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ChordProgressionTestFiles";
//	private String testContentPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ShortTest.liveclip";
//	private String liveclipFolderPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/RepetitionAnalysisMainFrameLiveClips/";
//	private String musicXMLFolderPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/RepetitionAnalysisMainFrameMusicXML/";
//	private String musicInfoSheetPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/RepetitionAnalysisMainFrameInfoSheets/";
	
	private static String defaultCfFile;
	private static String testContentPath;
	private static String chordProgressionFolderPath;
	private static String melodyFolderPath;
	private static String liveclipFolderPath;
	private static String musicXMLFolderPath;
	private static String musicInfoSheetPath;
	private static String fileBrowserDataPath;
	private static String instrumentRegisterInfoPath;
	
	private File outputFormPath;
	private static final FileFilter chordFileFilter = new ChordsLiveClipFileFilter();
	private static final FileFilter liveclipFileFilter = new LiveClipFileFilter();

	private JPanel contentPane;
	private ChordFormPanel cfPanel = new ChordFormPanel(this);
	private FeaturePickerPanel fpPanel; 

	protected File contentFormPath;
	protected File chordsFormPath;
	private JPanel renderDisplay;
	private Random rnd = new Random();
	JCheckBox injectIntoLive;
	JCheckBox renderMusicXML;
	JCheckBox saveAsLiveClip;
	HashMap<String, ArrayList<GenOutput>> goMap = new HashMap<String, ArrayList<GenOutput>>();
//	HashMap<String, ArrayList<ArrayList<FinalListNote>>> flnMap = new HashMap<String, ArrayList<ArrayList<FinalListNote>>>();
//	HashMap<String, ArrayList<ChordForm>> cfMap = new HashMap<String, ArrayList<ChordForm>>();
	HashMap<String, Integer> goMapOldSize = new HashMap<String, Integer>();
	JTextPane clipMapPane;
	private static final DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH mm_ss_SSS");
	ClipInjectorObject cio;
	private boolean autoRender = true;
	private boolean renderWhenAutoRenderIsActivated = false;
	private Color autoRenderFalseColor = new Color(100, 200, 200);
	private Color autoRenderTrueColor = new Color(200, 100, 100);
	private String autoRenderOnText = "autoRenderON";
	private String autoRenderOffText = "autoRenderOFF";
//	private TreeMap<Double, FinalListNote> currentOutputTreeMap;
//	private LiveClip currentClip;
	private int masterRepeatSchemaXMLTextSize = 6;
	private int masterRepeatSchemaXMLYOffset = 50;
	private static final String outputOnlyNewText = "output only new";
	private static final String outputAllText = "output all";
	private GenOutput currentGenOutput = null;
	private boolean outputOnlyNew = false;
	private int liveTrackIndex = 3;
	private int liveClipIndex = 0;
	private FileBrowserFilter liveClipFileFilter = new FileBrowserFilter(new String[]{"liveclip"});

	

	/**
	 * Create the frame.
	 */
	public RepetitionAnalysisMainFrame() {
		
		processPropertiesFile();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(900, 0, 1000, 1000);

		fpPanel = new FeaturePickerPanel(this);
		setLayout(new GridBagLayout());
		setupMenuBar();
		setupContentPane();
		setupClipInjectorObject();
		
		openDefaultContentClip();
		cfPanel.setFileWithoutUpdating(defaultCfFile);
		bang();
	}

	public static String getFileBrowserDataPath (){
		return fileBrowserDataPath;
	}

	public static String getInstrumentRegisterInfoPath (){
		return instrumentRegisterInfoPath;
	}

	public static String getChordProgressionFolderPath (){
		return chordProgressionFolderPath;
	}

	public static String getMelodyFolderPath (){
		return melodyFolderPath;
	}

	public void processPropertiesFile (){
		InputStream stream = this.getClass().getResourceAsStream("/resources/repetition_analysis.properties");
		try {
			Properties prop = new Properties();
			prop.load(stream);
			defaultCfFile = prop.getProperty("default_cf_file");
			testContentPath = prop.getProperty("test_content_path");
			chordProgressionFolderPath = prop.getProperty("default_chords_directory");
			melodyFolderPath = prop.getProperty("default_melody_directory");
			liveclipFolderPath = prop.getProperty("live_clip_save_directory");
			musicXMLFolderPath = prop.getProperty("musicxml_save_directory");
			musicInfoSheetPath = prop.getProperty("music_info_save_directory");
			fileBrowserDataPath = prop.getProperty("file_browser_data");
			instrumentRegisterInfoPath = prop.getProperty("instrument_register_data");
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
		}
	}

	private void setupClipInjectorObject() {
		cio = new ClipInjectorObject(liveTrackIndex, liveClipIndex, ClipInjectorObject.ofTrackType);
		cio.sendResetInitializationMessage();
		cio.sendInitializationMessage();
	}

	private void openDefaultContentClip() {
		try {
			LiveClip lc = new LiveClip(0, 0);
			BufferedReader b = new BufferedReader(new FileReader(testContentPath));
			lc.instantiateClipFromBufferedReader(b);
			fpPanel.setContentClip(lc);
		} catch (FileNotFoundException ex){
			ex.printStackTrace();
		}	
	}

	public void bang(){
		// gets the TreeMap from the Generator in the fpPanel (or later maybe from many such panels
		// and generates output
		System.out.println("--------------\nMainFrame.bang() test for viable output.");
		if (fpPanel.getFp().complete()){
			//System.out.println("FeaturePicker is Complete and wants a chocolate donut");
			if (autoRender){
				setRenderDisplayToRandomColour();
				GenOutput tempOutput = fpPanel.getGenOutput();
				if (currentGenOutput == null){
					doit(tempOutput);
				} else {
					if (outputOnlyNew){
						if (!currentGenOutput.isSameAs(tempOutput)){
							doit(tempOutput);
						} else {
							System.out.println("rerender resulted in same output as previous time. Boring.....");
						}
					} else {
						doit(tempOutput);
					}	
				}				
			} else {
				renderWhenAutoRenderIsActivated = true;
			}			
		}
	}
	
	public FeaturePickerPanel getFpPanel (){
		return fpPanel;
	}
	
	private void doit(GenOutput tempOutput){
		System.out.println("rendered file: " + tempOutput.renderName());
		currentGenOutput = tempOutput;
		doOutputRender(currentGenOutput);
		saveInfoSheet();
	}


	private void saveInfoSheet() {
		String outputPath = musicInfoSheetPath + fpPanel.latestRenderName() + ".info";
		fpPanel.getFp().saveInfoSheet(outputPath);
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
		
		mxm.addTextDirection(partName, fp.getMasterRepeatSchema().positionsToString().replace(",", ", "), 0.0, MXM.PLACEMENT_ABOVE, masterRepeatSchemaXMLTextSize, masterRepeatSchemaXMLYOffset);
		mxm.addTextDirection(partName, fp.getMasterRepeatSchema().barXOverPosArrayToString(fp.xover()).replace(",", ", "), 0.0, MXM.PLACEMENT_ABOVE, masterRepeatSchemaXMLTextSize, masterRepeatSchemaXMLYOffset);

	}

//	private ArrayList<FinalListNote> makeFinalNoteList(TreeMap<Double, FinalListNote> outputTreeMap) {
//		ArrayList<FinalListNote> flnList = new ArrayList<FinalListNote>();
//		for (Double d: outputTreeMap.keySet()){
//			flnList.add(outputTreeMap.get(d));
//		}
//		return flnList;
//	}

//	private LiveClip makeLiveClip(TreeMap<Double, FinalListNote> outputTreeMap) {
//		LiveClip outputClip = new LiveClip(0, 0);
//		for (FinalListNote fln: outputTreeMap.values()){			
//			outputClip.addNoteList(fln.getNoteList());
//		}
//		outputClip.loopEnd = cfPanel.cf().totalLength();
//		outputClip.length = outputClip.loopEnd;
//		outputClip.name = fpPanel.latestRenderName();
//		return outputClip;
//	}

	private void doOutputRender(GenOutput go) {
		if (injectIntoLive.isSelected()){
			cio.sendClip(go.lc());
		}
		System.out.println("clip rendered");
		if (saveAsLiveClip.isSelected()){
			renderLiveClipToTextFile(go.lc());			
		}
		System.out.println("text file rendered");
		if (renderMusicXML.isSelected()){
			renderMusicXML(go);
			System.out.println("xml from liveclip rendered");
//			ArrayList<FinalListNote> list = makeFinalNoteList(go);
			renderMusicXMLFromFinalNoteList(go);
			System.out.println("xml from fnlList rendered");
			renderGroupXMLs(go);
			System.out.println("group xml rendered");
		}
	}

	private void renderGroupXMLs(GenOutput go) {
		manageXMLGroupMap(go);
		renderTheGoMapKeyThatHasChangedSize();
		updateGoMapOldSize();
	}

	private void renderTheGoMapKeyThatHasChangedSize() {
		for (String key: goMap.keySet()){
			if (goMap.get(key).size() != goMapOldSize.get(key)){
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

	private void updateGoMapOldSize() {
		for (String key: goMap.keySet()){
			goMapOldSize.put(key, goMap.get(key).size());
		}
		
	}

	private void manageXMLGroupMap(GenOutput go) {
		String key = go.lc().signatureNumerator + "/" + go.lc().signatureDenominator + "-" + go.lc().barCount();
		if (!goMap.containsKey(key)){
			goMap.put(key, new ArrayList<GenOutput>());
			goMapOldSize.put(key, 0);
		}
		goMap.get(key).add(go);
		updateClipMapPane();
	}

	private void updateClipMapPane() {
		String str = "";
		for (String key: goMap.keySet()){
			str += key + " " + goMap.get(key).size() + " items\n";
		}
		clipMapPane.setText(str);
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

	private void renderLiveClipToTextFile(LiveClip lc) {
		String filepath = liveclipFolderPath + "RAMainFrameOutput_" + fpPanel.latestRenderName() + ".liveclip";
		//System.out.println(lc.getClipAsTextFile());
		try {
			FileWriter fw = new FileWriter(filepath);
			BufferedWriter bw = new BufferedWriter(fw);
//			System.out.println("writeLiveClipToFile called:");
//			System.out.println(lc.toString());
			String str = lc.getClipAsTextFile();
//			System.out.println(str);
			bw.write(str);
			bw.close();
			fw.close();
		} catch (IOException ex){
			System.out.println("RepetitionAnlysisMainFrame.renderLiveClipToTextFile() failed to work");
			System.out.println(ex.getMessage());
		}
		
	}

	private void setRenderDisplayToRandomColour() {
		int red = rnd.nextInt(256);
		int green = rnd.nextInt(256);
		int blue = rnd.nextInt(256);
		renderDisplay.setBackground(new Color(red, green, blue));
	}

	private void setupContentPane() {
		GridBagConstraints gbc_cfp = new GridBagConstraints();
		gbc_cfp.gridx = 0;
		gbc_cfp.gridy = 0;
		add(cfPanel, gbc_cfp);
		
		GridBagConstraints gbc_rap = new GridBagConstraints();
		gbc_rap.gridx = 1;
		gbc_rap.gridy = 0;
		gbc_rap.gridheight = 2;
		add(fpPanel, gbc_rap);
				
		GridBagConstraints gbc_rd = new GridBagConstraints();
		gbc_rd.gridx = 0;
		gbc_rd.gridy = 1;
		gbc_rd.fill = GridBagConstraints.BOTH;
		renderDisplay = getRenderPanel();
		add(renderDisplay, gbc_rd);

	}

	private void setupOutputRuleToggle(JPanel panel) {
		final JButton outputRuleButton = new JButton();
		if (outputOnlyNew){
			outputRuleButton.setText(outputOnlyNewText);
		} else {
			outputRuleButton.setText(outputAllText);
		}
		outputRuleButton.addActionListener(new ActionListener(){
			
			

			public void actionPerformed(ActionEvent e){
			    if (outputOnlyNew == true){
			    	outputOnlyNew = false;
			    	outputRuleButton.setText(outputAllText);			    	
			    } else {
			    	outputOnlyNew = true;
			    	outputRuleButton.setText(outputOnlyNewText);
			    }
			  } 
			});
		GridBagConstraints gbc_outputRule = new GridBagConstraints();
		gbc_outputRule.gridx = 0;
		gbc_outputRule.gridy = 6;
		gbc_outputRule.ipady = 15;
		gbc_outputRule.insets = new Insets(20, 20, 20, 20);
//		gbc_autoRender.anchor = GridBagConstraints.WEST;
		
		panel.add(outputRuleButton, gbc_outputRule);
	}

	private void setupRenderToggleButton(JPanel panel) {
		final JButton autoRenderButton = new JButton();
		if (autoRender){
			autoRenderButton.setText(autoRenderOnText);
			autoRenderButton.setBackground(autoRenderTrueColor);
		} else {
			autoRenderButton.setText(autoRenderOffText);
			autoRenderButton.setBackground(autoRenderFalseColor);
		}
		autoRenderButton.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
			    if (autoRender == true){
			    	autoRender = false;
			    	autoRenderButton.setBackground(autoRenderFalseColor);
			    	autoRenderButton.setText(autoRenderOnText);
			    	renderWhenAutoRenderIsActivated = false;
			    } else {
			    	autoRender = true;
			    	autoRenderButton.setBackground(autoRenderTrueColor);
			    	autoRenderButton.setText(autoRenderOffText);
			    	if (renderWhenAutoRenderIsActivated){
			    		bang();
			    	}
			    }
			  } 
			});
		GridBagConstraints gbc_autoRender = new GridBagConstraints();
		gbc_autoRender.gridx = 0;
		gbc_autoRender.gridy = 5;
		gbc_autoRender.ipady = 15;
		gbc_autoRender.insets = new Insets(20, 20, 20, 20);
//		gbc_autoRender.anchor = GridBagConstraints.WEST;
		
		panel.add(autoRenderButton, gbc_autoRender);
	}

	private JPanel getRenderPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc_injectIntoLive = new GridBagConstraints();
		gbc_injectIntoLive.gridx = 0;
		gbc_injectIntoLive.gridy = 0;
		gbc_injectIntoLive.anchor = GridBagConstraints.WEST;
		injectIntoLive = new JCheckBox("injectIntoLive");
		injectIntoLive.setOpaque(false);
		injectIntoLive.setSelected(true);
		panel.add(injectIntoLive, gbc_injectIntoLive);
				
		GridBagConstraints gbc_renderMusicXML = new GridBagConstraints();
		gbc_renderMusicXML.gridx = 0;
		gbc_renderMusicXML.gridy = 1;
		gbc_renderMusicXML.anchor = GridBagConstraints.WEST;
		renderMusicXML = new JCheckBox("renderMusicXML");
		renderMusicXML.setOpaque(false);
		renderMusicXML.setSelected(true);
		panel.add(renderMusicXML, gbc_renderMusicXML);
				
		GridBagConstraints gbc_saveAsLiveClip = new GridBagConstraints();
		gbc_saveAsLiveClip.gridx = 0;
		gbc_saveAsLiveClip.gridy = 2;
		gbc_saveAsLiveClip.anchor = GridBagConstraints.WEST;
		saveAsLiveClip = new JCheckBox("saveAsLiveClip");
		saveAsLiveClip.setOpaque(false);
		saveAsLiveClip.setSelected(true);
		panel.add(saveAsLiveClip, gbc_saveAsLiveClip);
		
		clipMapPane = new JTextPane();
		clipMapPane.setPreferredSize(new Dimension(100, 100));
		GridBagConstraints gbc_clipMapPane = new GridBagConstraints();
		gbc_clipMapPane.gridx = 0;
		gbc_clipMapPane.gridy = 3;
		gbc_clipMapPane.fill = GridBagConstraints.BOTH;
		panel.add(clipMapPane, gbc_clipMapPane);
		
		JButton clearButton = new JButton("Clear clipMap");
		clearButton.addActionListener(new ActionListener(){
		  public void actionPerformed(ActionEvent e){
		    goMap.clear();
		    updateClipMapPane();
		  } 
		});
		GridBagConstraints gbc_clearButton = new GridBagConstraints();
		gbc_clearButton.gridx = 0;
		gbc_clearButton.gridy = 4;
		panel.add(clearButton, gbc_clearButton);
		
		setupRenderToggleButton(panel);
		setupOutputRuleToggle(panel);		
		return panel;
	}

	

	private void setupMenuBar() {
		JMenuBar jmb = new JMenuBar();
		
		setJMenuBar(jmb);
		
		jmb.add(outputMenu());
		jmb.add(raMenu());
		jmb.add(liveMenu());
		
		
//		mnOutput.add(mntmTransposeOutputForm()); // going to make this a crazy push button thing in the CHordFormPanel
	}

	private JMenu liveMenu() {
		JMenu mnOutput = new JMenu("Live");
		mnOutput.add(mntmResendInitMessage());
		mnOutput.add(mntmLoadLiveClipFromFile());
		return mnOutput;
	}



	private JMenuItem mntmLoadLiveClipFromFile() {

		JMenuItem menuItem = new JMenuItem("Load liveclip");
		menuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				JButton open = new JButton();
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(liveclipFolderPath));
				fc.setDialogTitle("Select liveclip");
				fc.setFileFilter(liveClipFileFilter);
				if (fc.showOpenDialog(open) == JFileChooser.APPROVE_OPTION){
					File f = fc.getSelectedFile();
					//System.out.println(selectedFile.getPath());
					//System.out.println(selectedFile.getName());
					try {
						BufferedReader b = new BufferedReader(new FileReader(f));
						LiveClip lc = new LiveClip(0, 0);
						lc.instantiateClipFromBufferedReader(b);
						cio.sendClip(lc);
					} catch (Exception ex){
						
					}
				}	
			}
		});
		return menuItem;
	}

	private JMenu raMenu() {
		JMenu mnOutput = new JMenu("RepetitionAnalysis");
		mnOutput.add(mntmGetContentClip());
		mnOutput.add(mntmGetChordsClip());
		mnOutput.add(mntmClearChordsClip());
		return mnOutput;
	}

	private JMenu outputMenu() {
		JMenu mnOutput = new JMenu("Output");
		mnOutput.add(mntmSelectOutputForm());
		mnOutput.add(mntmNullOutputForm());
		return mnOutput;
	}

	private JMenuItem mntmResendInitMessage() {
		JMenuItem menuItem = new JMenuItem("Resend init message(s)");
		menuItem.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent arg0) {
				cio.sendResetInitializationMessage();
				cio.sendInitializationMessage();
			}
		});
		return menuItem;
	}

	private JMenuItem mntmNullOutputForm() {
		JMenuItem menuItem = new JMenuItem("Null output form");
		menuItem.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent arg0) {
				setOutputChordFormToNull();
			}
		});
		return menuItem;
	}
	private JMenuItem mntmClearChordsClip() {
		JMenuItem mntmSelectOutputForm = new JMenuItem("Clear chords clip");
		mntmSelectOutputForm.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent arg0) {
				fpPanel.setChordsClip(null);
				
			}
		});
		return mntmSelectOutputForm;
	}
	
	private JMenuItem mntmGetChordsClip() {
		JMenuItem mntmSelectOutputForm = new JMenuItem("Select chord clip");
		mntmSelectOutputForm.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent arg0) {
				JButton open = new JButton();
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(chordProgressionFolderPath));
				fc.setDialogTitle("Select chord clip for RepetitionAnalysis");
				fc.setFileFilter(chordFileFilter);
				if (fc.showOpenDialog(open) == JFileChooser.APPROVE_OPTION){
					contentFormPath = fc.getSelectedFile();
					//System.out.println(outputFormPath.getAbsolutePath());
					try {
						LiveClip lc = new LiveClip(0, 0);
						BufferedReader b = new BufferedReader(new FileReader(contentFormPath.getAbsolutePath()));
						lc.instantiateClipFromBufferedReader(b);
						fpPanel.setChordsClip(lc);
					} catch (Exception ex){
						
					}
				}
				
			}
		});
		return mntmSelectOutputForm;
	}
	
	private JMenuItem mntmGetContentClip() {
		JMenuItem mntmSelectOutputForm = new JMenuItem("Select content clip");
		mntmSelectOutputForm.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent arg0) {
				JButton open = new JButton();
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(melodyFolderPath));
				fc.setDialogTitle("Select content clip for RepetitionAnalysis");
				fc.setFileFilter(liveclipFileFilter);
				if (fc.showOpenDialog(open) == JFileChooser.APPROVE_OPTION){
					contentFormPath = fc.getSelectedFile();
					//System.out.println(outputFormPath.getAbsolutePath());
					try {
						LiveClip lc = new LiveClip(0, 0);
						BufferedReader b = new BufferedReader(new FileReader(contentFormPath.getAbsolutePath()));
						lc.instantiateClipFromBufferedReader(b);
						fpPanel.setContentClip(lc);
					} catch (Exception ex){
						
					}
				}
				
			}
		});
		return mntmSelectOutputForm;
	}
	
	
	private JMenuItem mntmSelectOutputForm() {
		JMenuItem mntmSelectOutputForm = new JMenuItem("Select output form");
		mntmSelectOutputForm.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent arg0) {
				JButton open = new JButton();
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(chordProgressionFolderPath));
				fc.setDialogTitle("Select output chord progression");
				fc.setFileFilter(chordFileFilter);
				if (fc.showOpenDialog(open) == JFileChooser.APPROVE_OPTION){
					outputFormPath = fc.getSelectedFile();
					//System.out.println(outputFormPath.getAbsolutePath());
					setOutputChordForm(outputFormPath.getAbsolutePath());
					//bang();
				}
				
			}
		});
		return mntmSelectOutputForm;
	}
	private void setOutputChordForm(String path){
//		cfPanel.setFile(path);
//		try {
//			LiveClip lc = new LiveClip(0, 0);
//			BufferedReader b = new BufferedReader(new FileReader(path));
//			lc.instantiateClipFromBufferedReader(b);
//			ChordForm cf = new ChordForm(lc);
//			setOutputChordForm(cf);
//		} catch (Exception ex){
//			
//		}
		
	}
	public void setOutputChordForm(File file){
		setOutputChordForm(file.getPath());
	}
	public void setOutputChordFormToNull(){
		cfPanel.setChordForm(null);
		fpPanel.setOutputChordForm(null);
	}
	public void setOutputChordForm(ChordForm cf) {
		cfPanel.setChordForm(cf);
		fpPanel.setOutputChordForm(cf);
		//bang();
	}
//	public void rerender(){
//		fpPanel.rerender();
//	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RepetitionAnalysisMainFrame frame = new RepetitionAnalysisMainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	

}
