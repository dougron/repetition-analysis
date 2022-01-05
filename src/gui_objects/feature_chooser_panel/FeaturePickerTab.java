package gui_objects.feature_chooser_panel;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Dimension;

public class FeaturePickerTab extends JPanel {
	
	JLabel label;
	private Insets globalPadding = new Insets(1,1,1,1);
	private FeaturePickerGamePanel parent;
	private JButton btnOnOff; 
	/**
	 * Create the panel.
	 * @param parent 
	 */
	public FeaturePickerTab(FeaturePickerGamePanel parent) {
		this.parent = parent;
		
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		this.setOpaque(true);
		
		label = new JLabel();
		label.setPreferredSize(new Dimension(20, 30));
		add(label);
		
		JButton btnMoveLeft = new JButton("<");
		btnMoveLeft.setForeground(new Color(0, 0, 0));
		btnMoveLeft.setMargin(globalPadding);
		btnMoveLeft.addActionListener(moveLeftListener());
		add(btnMoveLeft);
		
		JButton btnMoveRight = new JButton(">");
		btnMoveRight.setMargin(globalPadding);
		btnMoveRight.addActionListener(moveRightListener());
		add(btnMoveRight);
		
		btnOnOff = new JButton("off");
		btnOnOff.setMargin(globalPadding);
		btnOnOff.addActionListener(onOffListener());
		add(btnOnOff);
		
		JButton btnNew = new JButton("+");
		btnNew.setMargin(globalPadding);
		btnNew.addActionListener(addListener());
		add(btnNew);
		
		JButton tnDelete = new JButton("x");
		tnDelete.setMargin(globalPadding);
		tnDelete.addActionListener(deleteListener());
		add(tnDelete);

	}
	private ActionListener onOffListener() {
		return new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				parent.toggleOnOff();
				
			}
			
		};
	}
	private ActionListener moveRightListener() {
		return new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				parent.moveRight();
				
			}
			
		};
	}
	private ActionListener moveLeftListener() {
		return new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				parent.moveLeft();
				
			}
			
		};
	}
	private ActionListener addListener() {
		return new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				parent.addNewToRightOfSelf();
				
			}
			
		};
	}
	private ActionListener deleteListener() {
		
		return new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				parent.removeSelfFromTabList();
				
			}
			
		};
	}
	public void setLabelText(String str){
		label.setText(str);
	}
	public void setOn(boolean isOn) {
		if (isOn){
			btnOnOff.setText("on");
		} else {
			btnOnOff.setText("off");
		}
		//updateBackgroundColours();
	}
	public void updateBackgroundColours() {
		if (parent.isOn()){
			if (parent.hasCompleteData()) {
				setBackground(Color.green);
			} else {
				setBackground(Color.pink);
			}
		} else {
			setBackground(Color.gray);
		}
		
	}

}
