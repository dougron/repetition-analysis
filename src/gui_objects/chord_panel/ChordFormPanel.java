package gui_objects.chord_panel;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JTextField;

import ResourceUtils.ChordForm;
import gui_objects.main_ui.RepetitionAnalysisMainFrame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;

import DataObjects.ableton_live_clip.LiveClip;
import GUIObjects.FileBrowserPanel;
import GUIObjects.FileBrowserParent;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public class ChordFormPanel extends JPanel implements FileBrowserParent {
	
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
	private RepetitionAnalysisMainFrame mainFrame;
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
	public ChordFormPanel(RepetitionAnalysisMainFrame parent) {
		
		savePath = RepetitionAnalysisMainFrame.getFileBrowserDataPath() + "chordFormPanel.data";
		homeDir = RepetitionAnalysisMainFrame.getChordProgressionFolderPath();
		
		this.mainFrame = parent;
			
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 100, 0};
		gridBagLayout.rowHeights = new int[]{30, 30, 30, 30, 30, 100, 30};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		//setBackground(Color.green);
		
//		makeNameField();
		makeFileBrowser();
		//makeNullButton();
		//makeInfoPane();		
		makeChordsPane();
		makeFormLengthBox();
		makeTimeSignatureGUI();
		makeTransposePanels();
		
		populatePanel();
		makeFormCountComboBox();
		bgTestAndSet();
	}
	public void bang(){
		mainFrame.bang();
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
 	private void makeTransposePanels() {
		GridBagConstraints gbc_up = new GridBagConstraints();
		gbc_up.insets = new Insets(0, 0, 0, 5);
		gbc_up.gridx = 0;
		gbc_up.gridy = 6;
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
		gbc_ts.gridy = 3;
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
		gbc_num.gridy = 3;
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
		gbc_denom.gridy = 4;
		
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
			System.out.println("ChordFormPanel.newTimeSignature changed to: " + (int)num.getSelectedItem() + "/" + (int)denom.getSelectedItem());
			ChordForm newcf = new ChordForm(cf.lc().cloneIntoNewTimeSignature(new int[]{
					(int)num.getSelectedItem(),
					(int)denom.getSelectedItem()
			}));
			mainFrame.setOutputChordForm(newcf);	
			mainFrame.getFpPanel().bang();
			
		}
		
	}
	private void makeFormLengthBox() {
		lblFormlength = new JLabel("formLength");
		GridBagConstraints gbc_lblFormlength = new GridBagConstraints();
		gbc_lblFormlength.insets = new Insets(0, 0, 0, 5);
		gbc_lblFormlength.gridx = 0;
		gbc_lblFormlength.gridy = 2;
		add(lblFormlength, gbc_lblFormlength);
		
		formLength = new JTextField();
		formLength.setPreferredSize(new Dimension(60, 25));
		GridBagConstraints gbc_formlength = new GridBagConstraints();
		gbc_formlength.insets = new Insets(0, 0, 0, 5);
		gbc_formlength.gridx = 1;
		gbc_formlength.gridy = 2;
		gbc_formlength.fill = GridBagConstraints.HORIZONTAL;
		add(formLength, gbc_formlength);
	}
	private void makeFormCountComboBox()
	{
		lblFormCount = new JLabel("formCount");
		GridBagConstraints gbc_lblFormcount = new GridBagConstraints();
		gbc_lblFormcount.insets = new Insets(0, 0, 0, 5);
		gbc_lblFormcount.gridx = 0;
		gbc_lblFormcount.gridy = 1;
		add(lblFormCount, gbc_lblFormcount);
		
		
		formCount = new JComboBox();
		formCount.setPreferredSize(new Dimension(60, 25));
		formCount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (cf != null){
					cf.setNumberOfForms((int)formCount.getSelectedItem());
					//mainFrame.rerender();
					bang();
				}
			}
		});
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		//gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 1;
		
		for (int i: formCountContent){
			formCount.addItem(i);
		}
		//formCount.setSelectedItem(4);
		add(formCount, gbc_comboBox);
		
		
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
			formLength.setText("" + cf.barCount());
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
			gbc_scrollPane.gridy = 1;
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
			gbc_scrollPane.gridy = 5;
			gbc_scrollPane.gridwidth = 2;
			add(chordsScrollPane, gbc_scrollPane);
			chordsPane = new JTextArea();
			chordsPane.setLineWrap(true);
			chordsPane.setWrapStyleWord(true);
			chordsScrollPane.setViewportView(chordsPane);
		}
	
	}
	private void makeNullButton() {
		JButton btnNewButton = new JButton("make Null ChordForm");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mainFrame.setOutputChordFormToNull();
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 0;
		add(btnNewButton, gbc_btnNewButton);
		
	}
	private void makeNameField() {
		nameField = new JTextField();
		nameField.setEditable(false);
		GridBagConstraints gbc_nameField = new GridBagConstraints();
		gbc_nameField.insets = new Insets(5, 5, 5, 5);
//		gbc_nameField.anchor = GridBagConstraints.CENTER;
		gbc_nameField.gridx = 0;
		gbc_nameField.gridy = 0;
		gbc_nameField.gridwidth = 2;
		gbc_nameField.fill = GridBagConstraints.BOTH;
		add(nameField, gbc_nameField);
//		nameField.setColumns(25);
		
	}
	private void makeFileBrowser(){
		browser = new FileBrowserPanel(homeDir, dialogTitle, filter, savePath, listLength, this);
		GridBagConstraints gbc_fileBrowser = new GridBagConstraints();
		gbc_fileBrowser.insets = new Insets(5, 5, 5, 5);
//		gbc_nameField.anchor = GridBagConstraints.CENTER;
		gbc_fileBrowser.gridx = 0;
		gbc_fileBrowser.gridy = 0;
		gbc_fileBrowser.gridwidth = 2;
		gbc_fileBrowser.fill = GridBagConstraints.BOTH;
		add(browser, gbc_fileBrowser);
	}
	public void setChordForm(ChordForm cf){
//		mainFrame.setOutputChordForm(cf);
		this.cf = cf;
		populatePanel();
		bgTestAndSet();
//		mainFrame.rerender();
//		bang();
	}
//	public void setFile(String path){
//		browser.setFile(path);
//	}
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
//	private void setNameFieldText() {
//		if (cf == null){
//			nameField.setText("no output form selected");
//		} else {
//			nameField.setText(cf.name());
//		}
//	}
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
					mainFrame.setOutputChordForm(new ChordForm(clip));
					bang();
				}
			}
		});
		return butt;
	}
	@Override
	public void fileBrowserIsUpdated() {
		if (browser.hasSelectedFile()){
			mainFrame.setOutputChordForm(browser.getSelectedFile());
		} else {
			mainFrame.setOutputChordFormToNull();
		}
		bang();
	}

}
