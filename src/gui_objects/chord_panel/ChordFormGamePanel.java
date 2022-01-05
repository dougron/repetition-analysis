package gui_objects.chord_panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import DataObjects.ableton_live_clip.LiveClip;
import GUIObjects.FileBrowserPanel;
import GUIObjects.FileBrowserParent;
import ResourceUtils.ChordForm;
import gui_objects.main_ui.RepetitionAnalysisGameFrame;
import gui_objects.main_ui.RepetitionAnalysisMainFrame;
import gui_objects.main_ui.RepetitionAnalysisParent;

public class ChordFormGamePanel extends JPanel implements FileBrowserParent {
	
	private JTextField nameField;
	private ChordForm cf;
	JTextArea infoPane;
	private JScrollPane scrollPane;
	private JComboBox formCount;
	private int[] formCountContent = new int[]{1,2,3,4,5,6,7,8,9,10};		// theoretically no upper limit but we have to stop somewhere
	private int[] denomContent = new int[]{4,8};							// for more denominators, update XMLMeasure or else there will be an error exporting to musicXML
	private JScrollPane chordsScrollPane;
	JTextArea chordsPane;
	private JLabel lblFormlength;
	private JTextField formLength;
	private JLabel lblFormCount;
	private JLabel lblTimeSignature;
	private JComboBox num;
	private JComboBox denom;
	private int DEFAULT_NUM = 4;
	private int DEFAULT_DENOM = 4;
	private RepetitionAnalysisParent mainFrame;
	private FileBrowserPanel browser;
	private String homeDir;// = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ChordProgressionTestFiles";
	private String dialogTitle = "Select a chords clip";
	private String[] filter = new String[]{"chords.liveclip"};
	private String savePath;
	private int listLength = 10;

	/**
	 * Create the panel.
	 * @param parent 
	 */
	public ChordFormGamePanel(RepetitionAnalysisParent parent) {
		
		this.mainFrame = parent;
		
		setFilePaths();
					
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 100, 0};
		gridBagLayout.rowHeights = new int[]{40, 30, 30, 30, 30, 40, 100, 40};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0};
		setLayout(gridBagLayout);
		
		//setBackground(Color.green);
		
		makeHeader();
		makeFileBrowser();
		
		makeChordsPane();
		makeFormLengthBox();
		makeTimeSignatureGUI();
		makeTransposePanels();
		
		populatePanel();
		makeFormCountComboBox();
		bgTestAndSet();
	}


	public ChordForm cf(){
		return cf;
	}
	public boolean hasChordForm(){
		if (cf == null){
			return false;
		} else {
			return true;
		}
	}
	public void setSelectedChordFormFromPath(String path){
		browser.setSelectedFile(path);
	}
	
	
// --------privates------------------------------------------
	private void setFilePaths ()
	{
		savePath ="src/resources/file_browser_data/chordFormPanel.data";
		setHomeDirFromPropertiesFile();
	}
	
	private void setHomeDirFromPropertiesFile ()
	{
		try (InputStream input = new FileInputStream(RepetitionAnalysisGameFrame.propertiesPath))
		{
			Properties props = new Properties();
			props.load(input);
			homeDir = props.getProperty("default_chords_directory");
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
 	private void makeTransposePanels() {
		GridBagConstraints gbc_up = new GridBagConstraints();
		gbc_up.insets = new Insets(0, 0, 0, 5);
		gbc_up.gridx = 0;
		gbc_up.gridy = 7;
		gbc_up.gridwidth = 2;
		add(transposePanel(), gbc_up);
		
//		GridBagConstraints gbc_up = new GridBagConstraints();
//		gbc_up.insets = new Insets(0, 0, 0, 5);
//		gbc_up.gridx = 3;
//		gbc_up.gridy = 1;
//		add(transposeUpPanel(), gbc_up);
		
//		GridBagConstraints gbc_down = new GridBagConstraints();
//		gbc_down.insets = new Insets(0, 0, 0, 5);
//		gbc_down.gridx = 3;
//		gbc_down.gridy = 2;
//		add(transposeDownPanel(), gbc_down);
		
	}
	private void makeTimeSignatureGUI() {
		lblTimeSignature = new JLabel("time signature");
		GridBagConstraints gbc_ts = new GridBagConstraints();
		gbc_ts.insets = new Insets(0, 0, 0, 5);
		gbc_ts.gridx = 0;
		gbc_ts.gridy = 4;
		add(lblTimeSignature, gbc_ts);
		
		num = new JComboBox();
		num.setPreferredSize(new Dimension(40, 25));
		num.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (cf != null){
					newTimeSignature();
				}
			}			
		});
		for (int i = 2; i < 32; i++){
			num.addItem(i);
		}
		GridBagConstraints gbc_num = new GridBagConstraints();
		gbc_num.insets = new Insets(0, 0, 0, 5);
		gbc_num.gridx = 1;
		gbc_num.gridy = 4;
		add(num, gbc_num);
		
		denom = new JComboBox();
		denom.setPreferredSize(new Dimension(40, 25));
		denom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (cf != null){
					newTimeSignature();
				}
			}
		});
		GridBagConstraints gbc_denom = new GridBagConstraints();
		gbc_denom.insets = new Insets(0, 0, 5, 5);
		//gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_denom.gridx = 1;
		gbc_denom.gridy = 5;
		
		for (int i: denomContent){
			denom.addItem(i);
		}
		//formCount.setSelectedItem(4);
		add(denom, gbc_denom);
	}
	private void newTimeSignature() {
		int[] ts = cf.getTimeSignature();
		if (ts[0] == (int)num.getSelectedItem() && ts[1] == (int)denom.getSelectedItem()){
			// lol do nothing
		} else {
			//System.out.println("ChordFormPanel.newTimeSignature changed to: " + (int)num.getSelectedItem() + "/" + (int)denom.getSelectedItem());
			ChordForm newcf = new ChordForm(cf.lc().cloneIntoNewTimeSignature(new int[]{
					(int)num.getSelectedItem(),
					(int)denom.getSelectedItem()
			}));
			//mainFrame.setOutputChordForm(newcf);	
			setChordForm(newcf);
			mainFrame.setParameterHasChanged(true);
			mainFrame.updateRSGraphic();
		}
		
	}
	private void makeFormLengthBox() {
		lblFormlength = new JLabel("formLength(total)");
		GridBagConstraints gbc_lblFormlength = new GridBagConstraints();
		gbc_lblFormlength.insets = new Insets(0, 0, 0, 5);
		gbc_lblFormlength.gridx = 0;
		gbc_lblFormlength.gridy = 3;
		add(lblFormlength, gbc_lblFormlength);
		
		formLength = new JTextField();
		formLength.setPreferredSize(new Dimension(60, 25));
		GridBagConstraints gbc_formlength = new GridBagConstraints();
		gbc_formlength.insets = new Insets(0, 0, 0, 5);
		gbc_formlength.gridx = 1;
		gbc_formlength.gridy = 3;
		gbc_formlength.fill = GridBagConstraints.HORIZONTAL;
		add(formLength, gbc_formlength);
	}
	private void makeFormCountComboBox()
	{
		lblFormCount = new JLabel("formCount");
		GridBagConstraints gbc_lblFormcount = new GridBagConstraints();
		gbc_lblFormcount.insets = new Insets(0, 0, 0, 5);
		gbc_lblFormcount.gridx = 0;
		gbc_lblFormcount.gridy = 2;
		add(lblFormCount, gbc_lblFormcount);
		
		
		formCount = new JComboBox();
		formCount.setPreferredSize(new Dimension(60, 25));
		formCount.addActionListener(formCountActionListener());
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		//gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 2;
		
		for (int i: formCountContent){
			formCount.addItem(i);
		}
		//formCount.setSelectedItem(4);
		add(formCount, gbc_comboBox);
		
		
	}
	private void setformCount(int item) {
		for (ActionListener l: formCount.getActionListeners()){
			formCount.removeActionListener(l);
		}
		formCount.setSelectedItem(item);
		formCount.addActionListener(formCountActionListener());
		
	}
	private ActionListener formCountActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (cf != null){
					cf.setNumberOfForms((int)formCount.getSelectedItem());
					mainFrame.setParameterHasChanged(true);
					setFormLengthText();
					updateMasterRepeatSchemaSelectionPanelBarCount();
					mainFrame.updateRSGraphic();
				}
			}
		};
	}

	private void populatePanel() {
//		setNameFieldText();
		//setInfoPaneText();
		setChordsPaneText();
		setFormLengthText();
		setTimeSignatureValues();
	}
	private void setTimeSignatureValues() {
		if (cf == null){
			num.setSelectedItem(DEFAULT_NUM);
			denom.setSelectedItem(DEFAULT_DENOM);
		} else {
			int[] ts = cf.getTimeSignature();
			num.setSelectedItem(ts[0]);
			denom.setSelectedItem(ts[1]);
		}
	}
	private void setFormLengthText() {
		if (cf == null){
			formLength.setEditable(true);
		} else {
			formLength.setEditable(false);
			formLength.setText(cf.barCount() + " (" + cf.totalBarCount() + ")");
		}
		
	}
	private void setInfoPaneText() {
		if (cf == null){
			infoPane.setText(".....");
		} else {
			infoPane.setText(cf.toString());
			infoPane.setCaretPosition(0);
		}
		
	}
	private void setChordsPaneText(){
		if (cf == null){
			chordsPane.setText("no chords..");
		} else {
			//System.out.println("ChordFormPanel.setChordsPaneText:" + cf.slashChordsToString());
			chordsPane.setText(cf.slashChordsToString());
			chordsPane.setCaretPosition(0);
		}
	}
	private void makeInfoPane() {
		{
			scrollPane = new JScrollPane();
			scrollPane.setPreferredSize(new Dimension(250, 250));
			GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.insets = new Insets(5, 5, 0, 5);
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridx = 0;
			gbc_scrollPane.gridy = 2;
			gbc_scrollPane.gridheight = 2;
			add(scrollPane, gbc_scrollPane);
			infoPane = new JTextArea();
			infoPane.setLineWrap(true);
			infoPane.setWrapStyleWord(true);
			scrollPane.setViewportView(infoPane);
		}
	
	}
	private void makeChordsPane() {
		{
			chordsScrollPane = new JScrollPane();
			chordsScrollPane.setPreferredSize(new Dimension(250, 120));
			GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.insets = new Insets(5, 5, 5, 0);
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridx = 0;
			gbc_scrollPane.gridy = 6;
			gbc_scrollPane.gridwidth = 2;
			add(chordsScrollPane, gbc_scrollPane);
			chordsPane = new JTextArea();
			chordsPane.setLineWrap(true);
			chordsPane.setWrapStyleWord(true);
			Font f = chordsPane.getFont();
			Font f2 = new Font(f.getFontName(), f.getStyle(), f.getSize() - 2);
			chordsPane.setFont(f2);
			chordsScrollPane.setViewportView(chordsPane);
		}
	
	}
	private void makeHeader() {
		JLabel label = new JLabel("output chord form panel");
		GridBagConstraints gbc_label = getGridBagConstraints(0, 0);
		add(label, gbc_label);
	}
//	private void makeNullButton() {
//		JButton btnNewButton = new JButton("make Null ChordForm");
//		btnNewButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				cf = null;
//				mainFrame.setParameterHasChanged(true);
//			}
//		});
//		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
//		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
//		gbc_btnNewButton.gridx = 1;
//		gbc_btnNewButton.gridy = 0;
//		add(btnNewButton, gbc_btnNewButton);
//		
//	}
//	private void makeNameField() {
//		nameField = new JTextField();
//		nameField.setEditable(false);
//		GridBagConstraints gbc_nameField = new GridBagConstraints();
//		gbc_nameField.insets = new Insets(5, 5, 5, 5);
///		gbc_nameField.anchor = GridBagConstraints.CENTER;
//		gbc_nameField.gridx = 0;
//		gbc_nameField.gridy = 0;
//		gbc_nameField.gridwidth = 2;
//		gbc_nameField.fill = GridBagConstraints.BOTH;
//		add(nameField, gbc_nameField);
////		nameField.setColumns(25);
//		
//	}
	private void makeFileBrowser(){
		browser = new FileBrowserPanel(homeDir, dialogTitle, filter, savePath, listLength, this, FileBrowserPanel.BUTTONS_BELOW);
		GridBagConstraints gbc_fileBrowser = new GridBagConstraints();
		gbc_fileBrowser.insets = new Insets(5, 5, 5, 5);
//		gbc_nameField.anchor = GridBagConstraints.CENTER;
		gbc_fileBrowser.gridx = 0;
		gbc_fileBrowser.gridy = 1;
		gbc_fileBrowser.gridwidth = 2;
		gbc_fileBrowser.fill = GridBagConstraints.BOTH;
		add(browser, gbc_fileBrowser);
	}
	private void setChordForm(ChordForm cf){

		this.cf = cf;
		populatePanel();
		bgTestAndSet();

	}

	
	public void setFileWithoutUpdating(String path){
		browser.setFile(path);
	}

	private void bgTestAndSet() {
		if (cf == null){
			setBackground(Color.pink);
		} else {
			setBackground(Color.green);
		}
		
	}

	private JPanel transposePanel(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);
		
		GridBagConstraints bb;
		bb = getGridBagConstraints(0, 0, 3);
		panel.add(new JLabel("Transpose"));
		bb = getGridBagConstraints(0, 1);
		panel.add(transposeButton("+1", 1), bb);
		bb = getGridBagConstraints(1, 1);	
		panel.add(transposeButton("+2", 2), bb);
		bb = getGridBagConstraints(2, 1);		
		panel.add(transposeButton("+3", 3), bb);
		bb = getGridBagConstraints(0, 2);	
		panel.add(transposeButton("+4", 4), bb);
		bb = getGridBagConstraints(1, 2);		
		panel.add(transposeButton("+5", 5), bb);
		bb = getGridBagConstraints(2, 2);
		panel.add(transposeButton("+6", 6), bb);
		
		bb = getGridBagConstraints(0, 3);
		panel.add(transposeButton("-1", -1), bb);
		bb = getGridBagConstraints(1, 3);	
		panel.add(transposeButton("-2", -2), bb);
		bb = getGridBagConstraints(2, 3);
		panel.add(transposeButton("-3", -3), bb);
		bb = getGridBagConstraints(0, 4);	
		panel.add(transposeButton("-4", -4), bb);
		bb = getGridBagConstraints(1, 4);	
		panel.add(transposeButton("-5", -5), bb);
		bb = getGridBagConstraints(2, 4);	
		panel.add(transposeButton("-6", -6), bb);
		
		return panel;
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
	private JPanel transposeUpPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		panel.add(transposeButton("+1", 1));
		panel.add(transposeButton("+2", 2));
		panel.add(transposeButton("+3", 3));
		panel.add(transposeButton("+4", 4));
		panel.add(transposeButton("+5", 5));
		panel.add(transposeButton("+6", 6));
		
		return panel;
	}
	private JPanel transposeDownPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		panel.add(transposeButton("-1", -1));
		panel.add(transposeButton("-2", -2));
		panel.add(transposeButton("-3", -3));
		panel.add(transposeButton("-4", -4));
		panel.add(transposeButton("-5", -5));
		panel.add(transposeButton("-6", -6));
		
		return panel;
	}
	private JButton transposeButton(String string, final int i) {
		JButton butt = new JButton(string);
		butt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (cf != null){
					LiveClip clip = cf.lc().clone();
					clip.transpose(i);
					//System.out.println(clip.toString());
					setChordForm(new ChordForm(clip));
					mainFrame.setParameterHasChanged(true);
				}
			}
		});
		return butt;
	}
	
// FileBrowserParent methods -------------------------------------------------------------------
	@Override
	public void fileBrowserIsUpdated() {
		//System.out.println("ChordFormGamePanel.fileBrowserIsUpdated() called: browser.hasSelectedFile()=" + browser.hasSelectedFile());
		if (browser.hasSelectedFile()){
			try{
				LiveClip lc = new LiveClip(0, 0);
				BufferedReader b = new BufferedReader(new FileReader(browser.getSelectedFile()));
				lc.instantiateClipFromBufferedReader(b);
				setChordForm(new ChordForm(lc));
				setformCount(cf.numberOfForms());
				updateMasterRepeatSchemaSelectionPanelBarCount();
				//System.out.println("and it works....");
			} catch (Exception ex){
				System.out.println("ChordFormGamePanel.fileBrowserIsUpdated() error making LiveClip for output ChordForm");
			}
			
		} else {
			setChordForm(null);
		}
		mainFrame.setParameterHasChanged(true);
		mainFrame.updateRSGraphic();
	}


	// -----------------------------------------------------------------------------------------------
	private void updateMasterRepeatSchemaSelectionPanelBarCount(){
		//System.out.println("ChordFormGamePanel.updateMasterRepeatSchemaSelectionPanelBarCount() cf.barCount()=" + cf.totalBarCount());
		mainFrame.setMasterRepeatSchemaPanelBarCount(cf.totalBarCount());
	}
}
