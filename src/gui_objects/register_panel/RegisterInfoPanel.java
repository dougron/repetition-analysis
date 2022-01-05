package gui_objects.register_panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import GUIObjects.FileBrowserPanel;
import GUIObjects.FileBrowserParent;
import gui_objects.ColourUpdateParent;
import gui_objects.feature_chooser_panel.FeaturePickerGamePanel;
import gui_objects.main_ui.RepetitionAnalysisGameFrame;
import gui_objects.main_ui.RepetitionAnalysisMainFrame;
import gui_objects.main_ui.RepetitionAnalysisParent;
import repetition_analysis.info_objects.RegisterInfo;

public class RegisterInfoPanel extends JPanel implements FileBrowserParent {
	
	private String savePath = "src/resources/file_browser_data/registerInfoPanel.data";
	private int listLength = 15;
	private String folderPath;// = "D:/Documents/miscForBackup/Repetition text files/RepetitionAnalysisRegisterInfoFiles";
	private String dialogTitle = "Select a .registerinfo file";
	private String[] extensionFilter = new String[]{".registerinfo"};
	private FileBrowserPanel riBrowser;
	private Color bg = Color.pink;
	private RepetitionAnalysisParent raParent;
	private RegisterInfo ri = null;
	private JTextField extremeHi;
	private JLabel nameLabel;
	private JTextField high;
	private JTextField low;
	private JTextField extremeLow;
	private JTextField average;
	private ColourUpdateParent colourParent;

	public RegisterInfoPanel(FeaturePickerGamePanel raParent) {
		setPathsFromPropertiesFile();
		this.raParent = (RepetitionAnalysisParent)raParent;
		this.colourParent = (ColourUpdateParent) raParent;
		//System.out.println("RegisterInfoPanel started");
		setLayout();
		setGuiObjects();
	}
	private void setPathsFromPropertiesFile (){
		try (InputStream input = new FileInputStream(RepetitionAnalysisGameFrame.propertiesPath))
		{
			Properties props = new Properties();
			props.load(input);
			folderPath = props.getProperty("instrument_register_data");
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}	
	}

	public void RegisterInfoPanel(RepetitionAnalysisParent raParent){
		this.raParent = raParent;
		System.out.println("RegisterInfoPanel started");
		setLayout();
		setGuiObjects();
		
	}
	public RegisterInfo getRegisterInfo(){
		return ri;
	}
	public boolean hasRegisterInfo(){
		if (ri == null){
			return false;
		} else {
			return true;
		}
	}

	private void setGuiObjects() {
		makeHeader();
		makeBrowser();
		makeNameLabel();
		makeExtremeHigh();
		makeHigh();
		makeLow();
		makeExtremeLow();
		makeAverage();
	}
	private void makeHeader() {
		JLabel label = new JLabel("register info panel");
		GridBagConstraints gbc_label = getGridBagConstraints(0, 0);
		add(label, gbc_label);
	}

	private void makeNameLabel() {
		//makeJLabel("ExtremeHigh", 0, 1);
		nameLabel = new JLabel();
		GridBagConstraints gbc_nameLabel = getGridBagConstraints(0, 2);
		gbc_nameLabel.fill = GridBagConstraints.HORIZONTAL;
		Font f = nameLabel.getFont();
		Font f2 = new Font(f.getFontName(), f.getStyle(), f.getSize() + 2);
		nameLabel.setFont(f2);
		add(nameLabel, gbc_nameLabel);
	}
	private void makeExtremeHigh() {
		makeJLabel("extreme high", 0, 3);
		extremeHi = new JTextField();
		extremeHi.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_exHi = getGridBagConstraints(1, 3);
		//gbc_exHi.fill = GridBagConstraints.HORIZONTAL;
		//gbc_exHi.anchor = GridBagConstraints.EAST;
		add(extremeHi, gbc_exHi);
	}
	private void makeHigh() {
		makeJLabel("high", 0, 4);
		high = new JTextField();
		high.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_exHi = getGridBagConstraints(1, 4);
		//gbc_exHi.fill = GridBagConstraints.HORIZONTAL;
		//gbc_exHi.anchor = GridBagConstraints.EAST;
		add(high, gbc_exHi);
	}
	private void makeLow() {
		makeJLabel("low", 0, 5);
		low = new JTextField();
		low.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_exHi = getGridBagConstraints(1, 5);
		//gbc_exHi.fill = GridBagConstraints.HORIZONTAL;
		//gbc_exHi.anchor = GridBagConstraints.EAST;
		add(low, gbc_exHi);
	}
	private void makeExtremeLow() {
		makeJLabel("extreme low", 0, 6);
		extremeLow = new JTextField();
		extremeLow.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_exHi = getGridBagConstraints(1, 6);
		//gbc_exHi.fill = GridBagConstraints.HORIZONTAL;
		//gbc_exHi.anchor = GridBagConstraints.EAST;
		add(extremeLow, gbc_exHi);
	}

	private void makeAverage() {
		makeJLabel("average", 0, 7);
		average = new JTextField();
		average.setPreferredSize(new Dimension(40, 20));
		GridBagConstraints gbc_exHi = getGridBagConstraints(1, 7);
		//gbc_exHi.fill = GridBagConstraints.HORIZONTAL;
		//gbc_exHi.anchor = GridBagConstraints.EAST;
		add(average, gbc_exHi);
	}

	private void makeJLabel(String string, int i, int j) {
		JLabel label = new JLabel(string);
		GridBagConstraints gbc = getGridBagConstraints(i, j);
		gbc.anchor = GridBagConstraints.EAST;
		add(label, gbc);

	}

	private void makeBrowser() {
		riBrowser = new FileBrowserPanel(
				folderPath, 
				dialogTitle, 
				extensionFilter, 
				savePath, 
				listLength, 
				this, 
				FileBrowserPanel.BUTTONS_BELOW);
		GridBagConstraints gbc_riPanel = getGridBagConstraints(0, 1, 2, 1);
		gbc_riPanel.fill = GridBagConstraints.HORIZONTAL;
		add(riBrowser, gbc_riPanel);
		
	}

	private void setLayout(){
		setBackground(bg);
		GridBagLayout layout = new GridBagLayout();
		layout.rowHeights = new int[]{20, 50, 30, 20, 20, 20, 20};
//		layout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE, Double.MIN_VALUE};
		layout.columnWidths = new int[]{120, 30};
		setLayout(layout);
	}
	private void updateSelectedRegisterInfo() {
		if (riBrowser.hasSelectedFile()){
			ri = RegisterInfo.getNewRegisterInfoFromFile(riBrowser.getSelectedFile());
			updateInfoFields();
			//System.out.println(ri.toString());
		} else {
			ri = null;
			//System.out.println("RegisterInfo is null.");
		}
		
		
	}
	private void updateInfoFields() {
		if (ri == null){
			nameLabel.setText("");
			extremeHi.setText("");
			high.setText("");
			low.setText("");
			extremeLow.setText("");
			average.setText("");
		} else {
			nameLabel.setText(ri.name);
			extremeHi.setText("" + ri.max);
			high.setText("" + ri.comfortableMax);
			low.setText("" + ri.comfortableMin);
			extremeLow.setText("" + ri.min);
			average.setText("" + ri.avg);
		}
		
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
	public void updateBackgroundColor() {
		if (ri == null){
			if (colourParent.isOn()){
				setBackground(Color.pink);
			} else {
				setBackground(Color.lightGray);
			}
		} else {
			setBackground(Color.green);
		}
		
	}

	@Override
	public void fileBrowserIsUpdated() {
		//System.out.println("RegisterInfoPanel.fileBrowserIsUpdated() called");
		updateSelectedRegisterInfo();
		//updateBackgroundColor();
		
		raParent.setParameterHasChanged(true);
		colourParent.updateSubPanelColours();
	}

	


}
