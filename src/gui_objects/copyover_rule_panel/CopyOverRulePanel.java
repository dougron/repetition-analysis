package gui_objects.copyover_rule_panel;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import gui_objects.feature_chooser_panel.FeatureChooserGamePanel;

public class CopyOverRulePanel extends JPanel {
	
	private double xover = 0.5;
	private Color onColor = Color.GREEN;
	private Color offColor = Color.GRAY;
	private JButton copyRuleButton;
	public static final String copyOverRuleTrueText = "copyOver=TRUE";
	public static final String copyOverRuleFalseText = "copyOver=FALSE";
	private boolean copyOverRule = true;
	private FeatureChooserGamePanel chooserPanel;

	public CopyOverRulePanel(){
		init();
	}	
	public CopyOverRulePanel(FeatureChooserGamePanel chooserPanel){
		this.chooserPanel = chooserPanel;
		init();
	}
	private void init(){
		setLayout(new GridBagLayout());
		setBackground(Color.pink);
		makeCopyOverToggle();
		makeClearFeatureChooserPanelButton();
	}
	private void makeClearFeatureChooserPanelButton() {
		JButton clearButton = new JButton("Clear Chooser Panel");
		clearButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
			    chooserPanel.clearAll();
			  } 
			});
		GridBagConstraints gbc_clearButton = new GridBagConstraints();
		gbc_clearButton.gridx = 0;
		gbc_clearButton.gridy = 8;
//		gbc_clearButton.ipady = 15;
//		gbc_clearButton.insets = new Insets(20, 20, 20, 20);
//		gbc_clearButton.anchor = GridBagConstraints.CENTER;
		
		add(clearButton, gbc_clearButton);
	}
	public boolean getCopyOverRule(){
		return copyOverRule;
	}

	private void makeCopyOverToggle() {
		copyRuleButton = new JButton();
		if (copyOverRule){
			copyRuleButton.setText(copyOverRuleTrueText);
		} else {
			copyRuleButton.setText(copyOverRuleFalseText);
		}
		copyRuleButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
			    if (copyOverRule == true){
			    	copyOverRule = false;
			    	copyRuleButton.setText(copyOverRuleFalseText);			    	
			    } else {
			    	copyOverRule = true;
			    	copyRuleButton.setText(copyOverRuleTrueText);
			    }
			  } 
			});
		GridBagConstraints gbc_copyRule = new GridBagConstraints();
		gbc_copyRule.gridx = 0;
		gbc_copyRule.gridy = 7;
		gbc_copyRule.ipady = 15;
		gbc_copyRule.insets = new Insets(20, 20, 20, 20);
		gbc_copyRule.anchor = GridBagConstraints.CENTER;
		
		add(copyRuleButton, gbc_copyRule);
		
	}
}
