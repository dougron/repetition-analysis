package tests;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import DataObjects.ableton_live_clip.LiveClip;
import jtree_utils.UniqueIcon;
import jtree_utils.UniqueIconRenderer;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchemaSortWrapper;
import repetition_analysis.repeat_schema_list.FCRSSortWrapperList;

import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

public class NewMasterRepeatTreeTest extends JFrame {

	private JPanel contentPane;
	private String path = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/CorpusMelodyFiles/StraightNoChaser.liveclip";
	private String chordspath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ChordProgressionTestFiles/FJazzBlues.chords.liveclip";
	private JTree tree;
	private DefaultMutableTreeNode root;
	private JScrollPane chooserScrollPane;
	private DefaultTreeModel model;
	private RepetitionAnalysis ra;
	private double start = 0;			// for testing
	private double end = 1000;			// for testing
	private ButtonGroup sortPriorityGroup;
	private ButtonGroup sortGroup;
	private static final String START_POSITION_STRING = "start position";
	private static final String FEATURE_LENGTH_STRING = "feature length";
	private static final String POPULARITY_STRING = "popularity";
	private static final String EMPTY_STRING = "";
//	private boolean expandCollapse = true;
	private static final String POSITION_STRING = "position";
	private static final String GAP_STRING = "gap";
	private static final int IMAGE_WIDTH = 20;
	private String iconPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/icons/heart.png";
	private String slugIconPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/icons/slug.png";
	private static final String RELATIVE = "RelPos ";
	private static final String ABSOLUTE = "AbsPos";
	private static final String EXPAND_LEVEL_1 = "Expand1";
	private static final String COLLAPSE_LEVEL_1 = "Collapse1";
	private static final String EXPAND_LEVEL_2 = "Expand2";
	private static final String COLLAPSE_LEVEL_2 = "Collapse2";
	private boolean level1IsExpanded = false;
	private boolean level2IsExpanded = false;
	private boolean showAbsolutePos = true;
	private JButton absPosButton;
	private JButton lvl1Button;
	private JButton lvl2Button;
	private JComboBox<Integer> featureLengthLow;
	private JComboBox<Integer> featureLengthHigh;
	private int minimumFeatureLength = 0;
	private int maximumFeatureLength = 1000;		// arbitrarily large


	/**
	 * Create the frame.
	 */
 	public NewMasterRepeatTreeTest() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
//		gbl_contentPane.columnWidths = new int[]{0};
//		gbl_contentPane.rowHeights = new int[]{0};
//		gbl_contentPane.columnWeights = new double[]{Double.MIN_VALUE};
//		gbl_contentPane.rowWeights = new double[]{Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		ra = getRA();
		makeChooserPane();
		makeSortPane();		
	}
	
	private void makeChooserPane() {
		GridBagConstraints gbc_chooser = getGBC(0, 1);
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
							System.out.println("Tree item selected: " + selectedNode.toString() + " obj=" + fcrs.toString());
						} else {
							System.out.println("null object selected");
						}
					}
				} 
			}
			
		});

		tree.setRootVisible(false);

		chooserScrollPane = new JScrollPane(tree);
		chooserScrollPane.setPreferredSize(new Dimension(150, 200));

		contentPane.add(chooserScrollPane, gbc_chooser);
		
	}

	private void makeSortPane() {
		JPanel panel = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		
		panel.setLayout(gbl);
		gbl.columnWeights = new double[]{1.0, 1.0};
		
		
		GridBagConstraints gbc_sortPanel = new GridBagConstraints();
		gbc_sortPanel.gridx = 0;
		gbc_sortPanel.gridy = 0;

		
		addOrderChoices(panel);
		addSortPriorityChoices(panel);
		addDoButtons(panel);
		addFormLengthSelector(panel);
		addSortButtonListeners();
		addAbsoluteRelativeToggle(panel);
		addFeatureLengthConstrainers(panel);
		
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
				if (refresh) refreshSelectionDisplay();
			}
			
		};
	}

	private ActionListener featureLengthComboListener() {
		return new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshSelectionDisplay();
				
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
				refreshSelectionDisplay();
			}
			
		});
		GridBagConstraints gbc_absPos = getGBC(0, 2);
		panel.add(absPosButton, gbc_absPos);
	}

	private void addFormLengthSelector(JPanel panel) {
		Integer[] options = new Integer[32];
		for (int i = 0; i < options.length; i++){
			options[i] = i + 1;
		}
		final JComboBox<Integer> combo = new JComboBox<Integer>(options);
		combo.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent ae){
				double lengthInQuarters = combo.getItemAt(combo.getSelectedIndex()) * 4; 
				System.out.println("lengthInQuarters=" + lengthInQuarters);
				end = lengthInQuarters;
				refreshSelectionDisplay();
			}
		});
//		combo.setToolTipText("yabba dabba doo\nmolzipoepsi poo");
		GridBagConstraints gbc_label = getGBC(2, 0);
		panel.add(new JLabel("output bar count"), gbc_label);
		GridBagConstraints gbc_combo = getGBC(2, 1);
        panel.add(combo, gbc_combo);
		
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
		 
		            refreshSelectionDisplay();
		 
		        } 
		    }
		};
		return l;
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


	private void addDoButtons(JPanel panel) {
		JPanel doPanel = new JPanel();
		doPanel.setLayout(new FlowLayout());

//		doPanel.add(addButton());
//		doPanel.add(clearButton());
		makeExpandCollapseLvl1Button();
		doPanel.add(lvl1Button);	
		makeExpandCollapseLvl2Button();
		doPanel.add(lvl2Button);
//		doPanel.add(refreshButton());
//		ImageIcon icon = new ImageIcon("C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/icons/heart.png");		
//		
//		doPanel.add(new JLabel("", icon, JLabel.CENTER));
//		
		GridBagConstraints gbc = getGBC(0, 4);
		gbc.gridwidth = 2;
		panel.add(doPanel, gbc);
	}


//	private Component refreshButton() {
//		JButton button = new JButton("Refresh");
//		button.addActionListener(new ActionListener() {			 
//		    @Override
//		    public void actionPerformed(ActionEvent event) {		 
//		    	 refreshSelectionDisplay();	 
//		    }
//		});
//		return button;
//	}

	protected void refreshSelectionDisplay() {
		String sortContent = getGroupSelectionString(sortGroup);
		String priorityContent = getGroupSelectionString(sortPriorityGroup);
		
		System.out.println("refresh display parameters: " + sortContent + ", " + priorityContent);
		
		ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList = ra.getSchemaList().makePositionConstrainedSchemaList(start, end);

		loadFeatureConstrainers(swList, sortContent);
//		for (FeatureChunkRepeatSchemaSortWrapper sw: swList){
//			System.out.println(sw.toString());
//		}
		
		
		ArrayList<FCRSSortWrapperList> swlList = new ArrayList<FCRSSortWrapperList>();
//		TreeMap<Double, ArrayList<FCRSSortWrapperList>> map;
		
		if (sortContent == POSITION_STRING){
			swlList = ra.getSchemaList().getPosSortWrapperList(swList, minimumFeatureLength, maximumFeatureLength);	
			if (priorityContent == START_POSITION_STRING){
				doStartPosMap(swlList);
			} else if (priorityContent == FEATURE_LENGTH_STRING){
				doFeatureLengthMap(swlList);
			} else if (priorityContent == POPULARITY_STRING){
				doPopularityMap(swlList);
			}
		} else if (sortContent == GAP_STRING){
			swlList = ra.getSchemaList().getGapSortWrapperList(swList, minimumFeatureLength, maximumFeatureLength);
			doGapSortMap(swlList);
		}
		
		
		lvl1Button.setText(EXPAND_LEVEL_1);
		level1IsExpanded = false;
		lvl2Button.setText(EXPAND_LEVEL_2);
		level2IsExpanded = false;
		
        tree.setCellRenderer(new UniqueIconRenderer());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.expandRow(0);
		model.reload();
		
	}



	private void loadFeatureConstrainers(ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList, String sortContent) {
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

	private void doGapSortMap(ArrayList<FCRSSortWrapperList> swlList) {
		Collections.sort(swlList, FCRSSortWrapperList.featureLengthComparator);
		root.removeAllChildren();
		TreeMap<Double, ArrayList<FeatureChunkRepeatSchemaSortWrapper>> map = new TreeMap<Double, ArrayList<FeatureChunkRepeatSchemaSortWrapper>>();
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
				if (showAbsolutePos) {
					view = "" + d;
				} else {
					view = map.get(d).get(0).firstBarPosToString();
				}
				DefaultMutableTreeNode twig = new DefaultMutableTreeNode(new UniqueIcon("startPos " + view + " (" + map.get(d).size() + ")", slugIconPath, map.get(d).get(0)));		// 0 - if you select the group it returns the first item, as they ball have the same posArray
				for (FeatureChunkRepeatSchemaSortWrapper sw: map.get(d)){
					String twigName = sw.fcrs.featureShortName() + ": " + sw.fcrs.featureString();
					twig.add(new DefaultMutableTreeNode(new UniqueIcon(twigName, sw)));
				}
				branch.add(twig);
			}
		
//			swl.sortList(FeatureChunkRepeatSchemaSortWrapper.sizeAndFirstPosComparator);
						
			root.add(branch);
		}
		
	}

	private void doPopularityMap(ArrayList<FCRSSortWrapperList> swlList) {
		TreeMap<Integer, ArrayList<FCRSSortWrapperList>> map = new TreeMap<Integer, ArrayList<FCRSSortWrapperList>>();
		for (FCRSSortWrapperList swl: swlList){
			if (!map.containsKey(swl.size())){
				map.put(swl.size(), new ArrayList<FCRSSortWrapperList>());
			}
			map.get(swl.size()).add(swl);
		}
		root.removeAllChildren();
		for (Integer i: map.keySet()){
			ArrayList<FCRSSortWrapperList> list = map.get(i);
			String branchName = "popularity " + i + " (" + list.size() + ")";
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(new UniqueIcon(branchName, iconPath, null));
			Collections.sort(list, FCRSSortWrapperList.positionComparator);
			String view;
			for (FCRSSortWrapperList swl: list){
				if (showAbsolutePos) {
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
				}
				branch.add(twig);
			}
			root.add(branch);
		}
		
	}

	private void doFeatureLengthMap(ArrayList<FCRSSortWrapperList> swlList) {
		TreeMap<Integer, ArrayList<FCRSSortWrapperList>> map = new TreeMap<Integer, ArrayList<FCRSSortWrapperList>>();
		for (FCRSSortWrapperList swl: swlList){
			if (!map.containsKey(swl.featureArrayLength())){
				map.put(swl.featureArrayLength(), new ArrayList<FCRSSortWrapperList>());
			}
			map.get(swl.featureArrayLength()).add(swl);
		}
		root.removeAllChildren();
		for (Integer i: map.keySet()){
			ArrayList<FCRSSortWrapperList> list = map.get(i);
			String branchName = "featureLength " + i + " (" + list.size() + ")";
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(new UniqueIcon(branchName, iconPath, null));
			Collections.sort(list, FCRSSortWrapperList.positionComparator);
			String view;
			for (FCRSSortWrapperList swl: list){
				if (showAbsolutePos) {
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
				}
				branch.add(twig);
			}
			root.add(branch);
		}
		
	}

	private void doStartPosMap(ArrayList<FCRSSortWrapperList> swlList) {
		TreeMap<Double, ArrayList<FCRSSortWrapperList>> map = new TreeMap<Double, ArrayList<FCRSSortWrapperList>>();
		for (FCRSSortWrapperList swl: swlList){
			if (!map.containsKey(swl.getFirstFeature())){
				map.put(swl.getFirstFeature(), new ArrayList<FCRSSortWrapperList>());
			}
			map.get(swl.getFirstFeature()).add(swl);
		}
		root.removeAllChildren();
		String view;
		for (Double d: map.keySet()){
			ArrayList<FCRSSortWrapperList> list = map.get(d);
			if (showAbsolutePos) {
				view = "" + d;
			} else {
				view = list.get(0).firstBarPosToString();
			}
			String branchName = "pos " + view + " (" + list.size() + ")";
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(new UniqueIcon(branchName, iconPath, null));
			Collections.sort(list, FCRSSortWrapperList.featureLengthComparator);
			
			for (FCRSSortWrapperList swl: list){
				if (showAbsolutePos) {
					view = swl.keyArrayToString();
				} else {
					view = swl.relativeBarPosToString();
				}
				DefaultMutableTreeNode twig = new DefaultMutableTreeNode(new UniqueIcon(view, slugIconPath, swl.getFirstSortWrapper()));
				Iterator<FeatureChunkRepeatSchemaSortWrapper> swIterator = swl.iterator();
				while (swIterator.hasNext()){
					FeatureChunkRepeatSchemaSortWrapper sw = swIterator.next();
					twig.add(new DefaultMutableTreeNode(new UniqueIcon(sw.fcrs.featureShortName(), sw)));
				}
				branch.add(twig);
			}
			root.add(branch);
		}
	}

//	private ImageIcon getScaledIcon(String path) {
//		ImageIcon icon = new ImageIcon(path);
//		Image img = icon.getImage() ;  
//		Image newimg = img.getScaledInstance(IMAGE_WIDTH, IMAGE_WIDTH,  java.awt.Image.SCALE_SMOOTH) ;  
//		icon = new ImageIcon( newimg );
//		return icon;
//	}

	private String getGroupSelectionString(ButtonGroup group) {
		String str = EMPTY_STRING;
		for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                str = button.getText();
            }
        }
		return str;
	}

	private void makeExpandCollapseLvl2Button() {
		lvl2Button = new JButton();
		if (level2IsExpanded) lvl2Button.setText(COLLAPSE_LEVEL_2); else lvl2Button.setText(EXPAND_LEVEL_2);
		lvl2Button.addActionListener(new ActionListener() {			 
		    @Override
		    public void actionPerformed(ActionEvent event) {	
		    	if (level2IsExpanded) level2IsExpanded = false; else level2IsExpanded = true;
		    	if (level2IsExpanded) lvl2Button.setText(COLLAPSE_LEVEL_2); else lvl2Button.setText(EXPAND_LEVEL_2);
		    	if (root.getChildCount() > 0) {
					if (level2IsExpanded) {
						DefaultMutableTreeNode currentNode = root.getNextNode();
						do {
							if (currentNode.getLevel() == 2)
								tree.expandPath(new TreePath(currentNode.getPath()));
							currentNode = currentNode.getNextNode();
						} while (currentNode != null);
					} else {
						DefaultMutableTreeNode currentNode = root.getNextNode();
						do {
							if (currentNode.getLevel() == 2)
								tree.collapsePath(new TreePath(currentNode.getPath()));
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
		    	if (root.getChildCount() > 0){
		    		if (level1IsExpanded){
			    		DefaultMutableTreeNode currentNode = root.getNextNode();
			    		 do {
				    	       if (currentNode.getLevel() == 1) 
				    	            tree.expandPath(new TreePath(currentNode.getPath()));
				    	       currentNode = currentNode.getNextNode();
				    	       }
				    	    while (currentNode != null);
			    	} else {
			    		 DefaultMutableTreeNode currentNode = root.getNextNode();
			    		 do {
				    	       if (currentNode.getLevel() == 1) 
				    	            tree.collapsePath(new TreePath(currentNode.getPath()));
				    	       currentNode = currentNode.getNextNode();
				    	       }
				    	    while (currentNode != null);	
			    	}
		    	}
	    	 
		    }
		});
		
	}


//	private Component clearButton() {
//		JButton clearButton = new JButton("Clear");
//		clearButton.addActionListener(new ActionListener() {			 
//		    @Override
//		    public void actionPerformed(ActionEvent event) {		 
//		        clearList();		 
//		    }
//		});
//		return clearButton;
//	}


//	private JButton addButton() {
//		JButton addButton = new JButton("Add");
//		addButton.addActionListener(new ActionListener() {			 
//		    @Override
//		    public void actionPerformed(ActionEvent event) {		 
//		        addTestContentToList();		 
//		    }
//		});
//		return addButton;
//	}


	protected void clearList() {
		System.out.println("clearList called...");
		root.removeAllChildren();
		model.reload();
//		tree.setRootVisible(false);
		

	}


	protected void addTestContentToList() {
		System.out.println("addTestContent called...");
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


	private RepetitionAnalysis getRA() {
		LiveClip lc = getLiveClip();
		LiveClip chords = getChordsClip();
		System.out.println(lc.toString());
		int cellLength = 3;
		RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLength, chords);
		return ra;
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






	protected void populateListWithGapInfo() {
		System.out.println("Populate with gap info....");
		ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList = ra.getSchemaList().makePositionConstrainedSchemaList(start, end);
		ArrayList<FCRSSortWrapperList> swlList = ra.getSchemaList().getGapSortWrapperList(swList);
		Collections.sort(swlList, FCRSSortWrapperList.featureLengthAndPosComparator);
		
		printToConsole(swlList, "gap");
		
		root.removeAllChildren();
		for (FCRSSortWrapperList swl: swlList){
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode("gap \u221E " + swl.keyArrayToString());
			Iterator<FeatureChunkRepeatSchemaSortWrapper> swIterator = swl.iterator();
			while (swIterator.hasNext()){
				branch.add(new DefaultMutableTreeNode(swIterator.next().fcrs.toShortString()));
			}
			root.add(branch);
		}
		tree.expandRow(0);
		model.reload();
	}


	protected void populateListWithPosInfo() {
		System.out.println("Populate with pos info....");
		ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList = ra.getSchemaList().makePositionConstrainedSchemaList(start, end);
		ArrayList<FCRSSortWrapperList> swlList = ra.getSchemaList().getPosSortWrapperList(swList, 2, 5);
		Collections.sort(swlList, FCRSSortWrapperList.featureLengthAndPosComparator);
		
		printToConsole(swlList, "pos");
		
		root.removeAllChildren();
		for (FCRSSortWrapperList swl: swlList){
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode("pos " + swl.keyArrayToString());
			Iterator<FeatureChunkRepeatSchemaSortWrapper> swIterator = swl.iterator();
			while (swIterator.hasNext()){
				branch.add(new DefaultMutableTreeNode(swIterator.next().fcrs.featureShortName()));
			}
			root.add(branch);
		}
		tree.expandRow(0);
		model.reload();
		
	}


	private void printToConsole(ArrayList<FCRSSortWrapperList> swlList, String string) {
		String keyList = "";
		for (FCRSSortWrapperList swl: swlList){
			
			System.out.println(swl.keyArrayToString() + " ------------------------------------------");
			Iterator<FeatureChunkRepeatSchemaSortWrapper> swit = swl.iterator();
			while (swit.hasNext()){
				System.out.println("   " + swit.next().toString());
			}
			keyList += swl.keyArrayToString() + "\n";
		}
		System.out.println(keyList);
		System.out.println(swlList.size() + " unique " + string + " items");
		
	}


	private GridBagConstraints getGBC(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.anchor = GridBagConstraints.WEST;
		return gbc;
	}
	private GridBagConstraints getGBC(int x, int y, int anchor) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.anchor = anchor;
		return gbc;
	}

	private LiveClip getChordsClip() {
		LiveClip lc = new LiveClip(0, 0);
		try {
			lc.instantiateClipFromBufferedReader(new BufferedReader(new FileReader(chordspath)));			
		} catch (Exception ex){
			
		}
		return lc;
	}
	private LiveClip getLiveClip() {
		LiveClip lc = new LiveClip(0, 0);
		try {
			lc.instantiateClipFromBufferedReader(new BufferedReader(new FileReader(path)));
//			BufferedReader in = new BufferedReader(new FileReader(path));
//			while(true){
//				String str = in.readLine();
//				if (str == null) break;
//				println(str);
//				String[] strArr = str.split(",");
//				if (strArr[0].equals("LiveMidiNote")){
//					int note = Integer.valueOf(strArr[1]);
//					double pos = Double.valueOf(strArr[4]);
//					double len = Double.valueOf(strArr[5]);
//					int vel = Integer.valueOf(strArr[6]);
//					lc.addNote(note, pos, len, vel, 0);
//				} else if (strArr[0].equals("length")){
//					lc.length = Double.valueOf(strArr[1]);
//				} else if (strArr[0].equals("loopStart")){
//					lc.loopStart = Double.valueOf(strArr[1]);
//				} else if (strArr[0].equals("loopEnd")){
//					lc.loopEnd = Double.valueOf(strArr[1]);
//				} else if (strArr[0].equals("startMarker")){
//					lc.startMarker = Double.valueOf(strArr[1]);
//				} else if (strArr[0].equals("endMarker")){
//					lc.endMarker = Double.valueOf(strArr[1]);
//				} else if (strArr[0].equals("signatureNumerator")){
//					lc.signatureNumerator = Integer.valueOf(strArr[1]);
//				} else if (strArr[0].equals("signatureDenominator")){
//					lc.signatureDenominator = Integer.valueOf(strArr[1]);
//				} else if (strArr[0].equals("name")){
//					lc.name = strArr[1];
//				}
//				
//				
//			}
			
		} catch (Exception ex){
			
		}
		return lc;
	}




	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewMasterRepeatTreeTest frame = new NewMasterRepeatTreeTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


}
