package gui_objects.render_panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import repetition_analysis.gen_output.GenOutput;

public class RenderGamePanel extends JPanel {

	private Color bg = Color.gray;
	private JCheckBox injectIntoLive;
	private JCheckBox renderMusicXML;
	private JCheckBox saveAsLiveClip;
	private JTextPane clipMapPane;
	private HashMap<String, ArrayList<GenOutput>> goMap = new HashMap<String, ArrayList<GenOutput>>();
	private boolean autoRender;
	private Color autoRenderFalseColor = new Color(100, 200, 200);
	private Color autoRenderTrueColor = new Color(200, 100, 100);
	private String autoRenderOnText = "autoRenderON";
	private String autoRenderOffText = "autoRenderOFF";
	private boolean renderWhenAutoRenderIsActivated = false;
	HashMap<String, Integer> goMapOldSize = new HashMap<String, Integer>();
	private JLabel renderName;

	
	public RenderGamePanel(){
		setLayout();
		makeGUIElements();
	}
	
	public void manageXMLGroupMap(GenOutput go) {
		String key = go.lc().signatureNumerator + "/" + go.lc().signatureDenominator + "-" + go.lc().barCount();
		if (!goMap.containsKey(key)){
			goMap.put(key, new ArrayList<GenOutput>());
			goMapOldSize.put(key, 0);
		}
		goMap.get(key).add(go);
		updateClipMapPane();
	}
	
	public void updateGoMapOldSize() {
		for (String key: goMap.keySet()){
			goMapOldSize.put(key, goMap.get(key).size());
		}	
	}
	
	public void setRenderName(String str){
		renderName.setText(str);
	}
	
	public HashMap<String, ArrayList<GenOutput>> getGoMap (){
		return goMap;
	}
	
	public Integer getOldSizeOfGoMapValue (String key){
		return goMapOldSize.get(key);
	}
	
// privates -----------------------------------------------------------------
	
	private void makeGUIElements() {
		GridBagConstraints gbc_injectIntoLive = new GridBagConstraints();
		gbc_injectIntoLive.gridx = 0;
		gbc_injectIntoLive.gridy = 0;
		gbc_injectIntoLive.anchor = GridBagConstraints.WEST;
		injectIntoLive = new JCheckBox("injectIntoLive");
		injectIntoLive.setOpaque(false);
		injectIntoLive.setSelected(true);
		add(injectIntoLive, gbc_injectIntoLive);
				
		GridBagConstraints gbc_renderMusicXML = new GridBagConstraints();
		gbc_renderMusicXML.gridx = 0;
		gbc_renderMusicXML.gridy = 1;
		gbc_renderMusicXML.anchor = GridBagConstraints.WEST;
		renderMusicXML = new JCheckBox("renderMusicXML");
		renderMusicXML.setOpaque(false);
		renderMusicXML.setSelected(true);
		add(renderMusicXML, gbc_renderMusicXML);
				
		GridBagConstraints gbc_saveAsLiveClip = new GridBagConstraints();
		gbc_saveAsLiveClip.gridx = 0;
		gbc_saveAsLiveClip.gridy = 2;
		gbc_saveAsLiveClip.anchor = GridBagConstraints.WEST;
		saveAsLiveClip = new JCheckBox("saveAsLiveClip");
		saveAsLiveClip.setOpaque(false);
		saveAsLiveClip.setSelected(true);
		add(saveAsLiveClip, gbc_saveAsLiveClip);
		
		clipMapPane = new JTextPane();
		clipMapPane.setPreferredSize(new Dimension(100, 100));
		GridBagConstraints gbc_clipMapPane = new GridBagConstraints();
		gbc_clipMapPane.gridx = 0;
		gbc_clipMapPane.gridy = 3;
		gbc_clipMapPane.fill = GridBagConstraints.BOTH;
		add(clipMapPane, gbc_clipMapPane);
		
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
		add(clearButton, gbc_clearButton);
		
		setupRenderToggleButton();
//		setupOutputRuleToggle(panel);	
		setupRenderName();
		
	}

	private void setupRenderName() {
		renderName = new JLabel();
		renderName.setOpaque(true);
		renderName.setBackground(Color.white);
//		renderName.setPreferredSize(new Dimension(200, 35));
		GridBagConstraints gbc_renderName = new GridBagConstraints();
		gbc_renderName.gridx = 0;
		gbc_renderName.gridy = 6;
		gbc_renderName.ipady = 15;
		//gbc_renderName.ipadx = 25;
		
		gbc_renderName.insets = new Insets(20, 20, 20, 20);
		gbc_renderName.fill = GridBagConstraints.HORIZONTAL;
		gbc_renderName.anchor = GridBagConstraints.CENTER;
		
		add(renderName, gbc_renderName);
		
	}
	private void setupRenderToggleButton() {
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
		
		add(autoRenderButton, gbc_autoRender);
	}
	private void setLayout(){
		setBackground(bg );
		GridBagLayout layout = new GridBagLayout();
		layout.rowHeights = new int[]{20, 20, 20, 100, 20, 20};
		layout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE, Double.MIN_VALUE};
		setLayout(layout);
	}

	private void updateClipMapPane() {
		String str = "";
		for (String key: goMap.keySet()){
			str += key + " " + goMap.get(key).size() + " items\n";
		}
		clipMapPane.setText(str);
	}

	public boolean injectIntoLive(){
		return injectIntoLive.isSelected();
	}
	public boolean saveAsLiveClip() {
		return saveAsLiveClip.isSelected();
	}
	public boolean renderMusicXML() {
		return renderMusicXML.isSelected();
	}
}
