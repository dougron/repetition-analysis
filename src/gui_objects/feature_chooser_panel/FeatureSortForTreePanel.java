package gui_objects.feature_chooser_panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import gui_objects.main_ui.RepetitionAnalysisGameFrame;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchemaSortWrapper;

public class FeatureSortForTreePanel extends JPanel {
	
	private FeatureSortForTreeParent parent;
	private ButtonGroup sortGroup;
	private ButtonGroup sortPriorityGroup;


	private JButton lvl1Button;
	private boolean level1IsExpanded;
	private JButton lvl2Button;
	private boolean level2IsExpanded;
	private JButton absPosButton;
	private boolean showAbsolutePos;
	private JComboBox<Integer> featureLengthLow;
	private JComboBox<Integer> featureLengthHigh;
	private Integer minimumFeatureLength;
	private Integer maximumFeatureLength;
	
	public FeatureSortForTreePanel(FeatureSortForTreeParent parent){
		setPathsFromPropertiesFile();
		this.parent = parent;
		setOpaque(false);
		makeSortPane();	
	}
	public void loadFeatureConstrainers(ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList, String sortContent) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (FeatureChunkRepeatSchemaSortWrapper sw: swList){
			int i;
			if (sortContent == POSITION_STRING){
				i = sw.posArray().length;
			} else {
				i = sw.gapArray().length;
			}
			if (!list.contains(i)) list.add(i);
		}
		Collections.sort(list);
		
		for (ActionListener al: featureLengthLow.getActionListeners()){
			featureLengthLow.removeActionListener(al);
		}
		Integer previousI = featureLengthLow.getItemAt(featureLengthLow.getSelectedIndex());
		if (previousI == null) previousI = 0;
		int indexToSetLow = 0;
		int index = 0;
		featureLengthLow.removeAllItems();
		for (Integer I: list){
			featureLengthLow.addItem(I);
			if (I <= previousI) indexToSetLow = index;
			index++;
		}
		featureLengthLow.setSelectedIndex(indexToSetLow);  		// sets to lowest value. ie no lower constraints
		featureLengthLow.addActionListener(featureLengthComboListener());
		
		for (ActionListener al: featureLengthHigh.getActionListeners()){
			featureLengthHigh.removeActionListener(al);
		}
		Integer previousJ = featureLengthHigh.getItemAt(featureLengthHigh.getSelectedIndex());
		if (previousJ == null) previousJ = 10000;		// arb high number
		int indexToSetHigh = 0;
		index = 0;
		featureLengthHigh.removeAllItems();
		for (int i = list.size() - 1; i > -1; i--){
			int I = list.get(i);
			featureLengthHigh.addItem(I);
			if (I >= previousJ) indexToSetHigh = index;
			index++;
		}
		featureLengthHigh.setSelectedIndex(indexToSetHigh);  		// sets to highest value. ie no upper constraints
		featureLengthHigh.addActionListener(featureLengthComboListener());
		
		minimumFeatureLength = featureLengthLow.getItemAt(featureLengthLow.getSelectedIndex());
		maximumFeatureLength = featureLengthHigh.getItemAt(featureLengthHigh.getSelectedIndex());
		
	}
	public ButtonGroup getSortGroup (){
		return sortGroup;
	}
	public ButtonGroup getSortPriorityGroup (){
		return sortPriorityGroup;
	}
	public Integer minimumFeatureLength(){
		return minimumFeatureLength;
	}
	public Integer maximumFeatureLength(){
		return maximumFeatureLength;
	}
	public boolean showAbsolutePos(){
		return showAbsolutePos;
	}
	public void setLvl1Button(String str, boolean b){
		lvl1Button.setText(str);
		level1IsExpanded = b;
	}
	public void setLvl2Button(String str, boolean b){
		lvl2Button.setText(str);
		level2IsExpanded = b;
	}
	
// privates -------------------------------------------------------------------------------------
	
	private void setPathsFromPropertiesFile (){
		try (InputStream input = new FileInputStream(RepetitionAnalysisGameFrame.propertiesPath))
		{
			Properties props = new Properties();
			props.load(input);
			String graphicsPath = props.getProperty("graphics_path");
			iconPath = graphicsPath = "heart.png";
			slugIconPath = graphicsPath = "slug.png";
		}
		catch (IOException ex)
		{
			
		}	
	}
	private ActionListener featureLengthComboListener() {
		return new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				parent.refreshSelectionDisplay();
				
			}
			
		};
	}
	private void makeSortPane() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		GridBagLayout gbl = new GridBagLayout();
		
		panel.setLayout(gbl);
		gbl.columnWeights = new double[]{1.0, 1.0};
		
		
		GridBagConstraints gbc_sortPanel = new GridBagConstraints();
		gbc_sortPanel.gridx = 0;
		gbc_sortPanel.gridy = 0;

		
		addOrderChoices(panel);
		addSortPriorityChoices(panel);
		addDoButtons(panel);
//		addFormLengthSelector(panel);
		addSortButtonListeners();
		addAbsoluteRelativeToggle(panel);
		addFeatureLengthConstrainers(panel);
		for (Component c: panel.getComponents()){
			if (c instanceof JComponent){
				((JComponent) c).setOpaque(false);
			}
			
		}
		add(panel, gbc_sortPanel);
		
		
	}
	private void addFeatureLengthConstrainers(JPanel panel) {
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWeights = new double[]{1.0, 1.0};
		
		JPanel lilPanel = new JPanel(gbl);
		
		GridBagConstraints gbc_label = getGBC(0, 0);
		gbc_label.gridwidth = 2;
		lilPanel.add(new JLabel("lower/upper featureLength"), gbc_label);

		Dimension boxSize = new Dimension(55, 25);
		int boxRowCount = 16;
		
		featureLengthLow = new JComboBox<Integer>();
		featureLengthLow.setPreferredSize(boxSize);
		featureLengthLow.setMaximumRowCount(boxRowCount);
		GridBagConstraints gbc_low = getGBC(0, 1);
		lilPanel.add(featureLengthLow, gbc_low);
		
		featureLengthHigh = new JComboBox<Integer>();
		featureLengthHigh.setPreferredSize(boxSize);
		featureLengthHigh.setMaximumRowCount(boxRowCount);
		GridBagConstraints gbc_high = getGBC(1, 1);
		lilPanel.add(featureLengthHigh, gbc_high);
		
		JButton max = new JButton("max");
		max.addActionListener(maxButtonActionListener());
		GridBagConstraints gbc_maxButton = getGBC(2, 1);
		lilPanel.add(max, gbc_maxButton);
		
		GridBagConstraints gbc_lilPanel = getGBC(0, 3);
		gbc_lilPanel.gridwidth = 2;
		panel.add(lilPanel, gbc_lilPanel );
	}

	private ActionListener maxButtonActionListener() {
		
		return new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean refresh = false;
				if (featureLengthLow.getItemCount() > 0){
					for (ActionListener al: featureLengthLow.getActionListeners()){
						featureLengthLow.removeActionListener(al);
					}
					featureLengthLow.setSelectedIndex(0);
					refresh = true;
				}
				if (featureLengthHigh.getItemCount() > 0){
					for (ActionListener al: featureLengthHigh.getActionListeners()){
						featureLengthHigh.removeActionListener(al);
					}
					featureLengthHigh.setSelectedIndex(0);
					refresh = true;
				}
				if (refresh) parent.refreshSelectionDisplay();
			}
			
		};
	}
	private void addAbsoluteRelativeToggle(JPanel panel) {
		absPosButton = new JButton();
		if (showAbsolutePos){
			absPosButton.setText(ABSOLUTE);
		} else {
			absPosButton.setText(RELATIVE);
		}
		absPosButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (showAbsolutePos){
					showAbsolutePos = false;
				} else {
					showAbsolutePos = true;
				}
				if (showAbsolutePos){
					absPosButton.setText(ABSOLUTE);
				} else {
					absPosButton.setText(RELATIVE);
				}
				System.out.println("showAbsolutePos=" + showAbsolutePos);
				parent.refreshSelectionDisplay();
			}
			
		});
		GridBagConstraints gbc_absPos = getGBC(0, 2);
		panel.add(absPosButton, gbc_absPos);
	}
	private void addSortButtonListeners() {
		addRefreshListener(sortGroup, refreshListener());
		addRefreshListener(sortPriorityGroup, refreshListener());
	}

	private void addRefreshListener(ButtonGroup group, ItemListener listener) {
		for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            button.addItemListener(listener);
        }
		
	}

	private ItemListener refreshListener() {
		ItemListener l = new ItemListener() {
			 
		    @Override
		    public void itemStateChanged(ItemEvent event) {
		        int state = event.getStateChange();
		        if (state == ItemEvent.SELECTED) {
		 
		            parent.refreshSelectionDisplay();
		 
		        } 
		    }
		};
		return l;
	}
	private void addDoButtons(JPanel panel) {
		JPanel doPanel = new JPanel();
		doPanel.setLayout(new FlowLayout());


		makeExpandCollapseLvl1Button();
		doPanel.add(lvl1Button);	
		makeExpandCollapseLvl2Button();
		doPanel.add(lvl2Button);
		
		GridBagConstraints gbc = getGBC(0, 4);
		gbc.gridwidth = 2;
		panel.add(doPanel, gbc);
	}
	private void makeExpandCollapseLvl2Button() {
		lvl2Button = new JButton();
		if (level2IsExpanded) lvl2Button.setText(COLLAPSE_LEVEL_2); else lvl2Button.setText(EXPAND_LEVEL_2);
		lvl2Button.addActionListener(new ActionListener() {			 
		    @Override
		    public void actionPerformed(ActionEvent event) {	
		    	if (level2IsExpanded) level2IsExpanded = false; else level2IsExpanded = true;
		    	if (level2IsExpanded) lvl2Button.setText(COLLAPSE_LEVEL_2); else lvl2Button.setText(EXPAND_LEVEL_2);
		    	if (parent.root().getChildCount() > 0) {
					if (level2IsExpanded) {
						DefaultMutableTreeNode currentNode = parent.root().getNextNode();
						do {
							if (currentNode.getLevel() == 2)
								parent.tree().expandPath(new TreePath(currentNode.getPath()));
							currentNode = currentNode.getNextNode();
						} while (currentNode != null);
					} else {
						DefaultMutableTreeNode currentNode = parent.root().getNextNode();
						do {
							if (currentNode.getLevel() == 2)
								parent.tree().collapsePath(new TreePath(currentNode.getPath()));
							currentNode = currentNode.getNextNode();
						} while (currentNode != null);
					} 
				}
		    	
		    	    	 
		    }
		});
		
	}


	private void makeExpandCollapseLvl1Button() {
		lvl1Button = new JButton();
		if (level1IsExpanded) lvl1Button.setText(COLLAPSE_LEVEL_1); else lvl1Button.setText(EXPAND_LEVEL_1);
		lvl1Button.addActionListener(new ActionListener() {			 
		    @Override
		    public void actionPerformed(ActionEvent event) {	
		    	if (level1IsExpanded) level1IsExpanded = false; else level1IsExpanded = true;
		    	if (level1IsExpanded) lvl1Button.setText(COLLAPSE_LEVEL_1); else lvl1Button.setText(EXPAND_LEVEL_1);
		    	if (parent.root().getChildCount() > 0){
		    		if (level1IsExpanded){
			    		DefaultMutableTreeNode currentNode = parent.root().getNextNode();
			    		 do {
				    	       if (currentNode.getLevel() == 1) 
				    	            parent.tree().expandPath(new TreePath(currentNode.getPath()));
				    	       currentNode = currentNode.getNextNode();
				    	       }
				    	    while (currentNode != null);
			    	} else {
			    		 DefaultMutableTreeNode currentNode = parent.root().getNextNode();
			    		 do {
				    	       if (currentNode.getLevel() == 1) 
				    	    	   parent.tree().collapsePath(new TreePath(currentNode.getPath()));
				    	       currentNode = currentNode.getNextNode();
				    	       }
				    	    while (currentNode != null);	
			    	}
		    	} 
		    }
		});		
	}
	
	private void addSortPriorityChoices(JPanel panel) {
		JRadioButton option_startpos = new JRadioButton(START_POSITION_STRING);
        JRadioButton option_featurelength = new JRadioButton(FEATURE_LENGTH_STRING);
        JRadioButton option_popularity = new JRadioButton(POPULARITY_STRING);

		
        sortPriorityGroup = new ButtonGroup();
        sortPriorityGroup.add(option_startpos);
        sortPriorityGroup.add(option_featurelength);
        sortPriorityGroup.add(option_popularity);
        
        option_startpos.setSelected(true);
        
        GridBagConstraints gbc_startpos = getGBC(1, 0);
        gbc_startpos.weightx = 0.5;
//        option_startpos.setPreferredSize(new Dimension(100, 30));
        panel.add(option_startpos, gbc_startpos);
        GridBagConstraints gbc_featurelength = getGBC(1, 1);
        panel.add(option_featurelength, gbc_featurelength);
        GridBagConstraints gbc_popularity = getGBC(1, 2);
        panel.add(option_popularity, gbc_popularity);
		
	}
	private void addOrderChoices(JPanel panel) {
		JRadioButton option_pos = new JRadioButton(POSITION_STRING);
        JRadioButton option_gap = new JRadioButton(GAP_STRING);
		
        sortGroup = new ButtonGroup();
        sortGroup.add(option_pos);
        sortGroup.add(option_gap);
        
        option_pos.setSelected(true);
        
        GridBagConstraints gbc_optionpos = getGBC(0, 0);
        gbc_optionpos.weightx = 0.5;
        panel.add(option_pos, gbc_optionpos);
        GridBagConstraints gbc_optiongap = getGBC(0, 1);
        panel.add(option_gap, gbc_optiongap);
	}
	
	private GridBagConstraints getGBC(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.anchor = GridBagConstraints.WEST;
		return gbc;
	}
	
	
	public static final String POSITION_STRING = "position";
	public static final String GAP_STRING = "gap";
	public static final String START_POSITION_STRING = "start position";
	public static final String FEATURE_LENGTH_STRING = "feature length";
	public static final String POPULARITY_STRING = "popularity";
	public static final String EMPTY_STRING = "";
	public static final int IMAGE_WIDTH = 20;
	public String iconPath;// = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/icons/heart.png";
	public String slugIconPath;// = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/icons/slug.png";
	public static final String RELATIVE = "RelPos ";
	public static final String ABSOLUTE = "AbsPos";
	public static final String EXPAND_LEVEL_1 = "Expand1";
	public static final String COLLAPSE_LEVEL_1 = "Collapse1";
	public static final String EXPAND_LEVEL_2 = "Expand2";
	public static final String COLLAPSE_LEVEL_2 = "Collapse2";


}
