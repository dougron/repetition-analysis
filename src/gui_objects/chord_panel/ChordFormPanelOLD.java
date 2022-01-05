package gui_objects.chord_panel;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ResourceUtils.ChordForm;

public class ChordFormPanelOLD extends JPanel {
	
	private ChordForm cf = null;
	private JTextField nameField;
	private JButton makeNullCF;

	public ChordFormPanelOLD(){
		init();
		setNameField();
		setButtons();
	}
	private void setButtons() {

		
	}
	public void setChordForm(ChordForm cf){
		this.cf = cf;
		setNameField();
		
	}

	private void setNameField() {
		if (cf == null){
			nameField.setText("no output form selected");
		} else {
			nameField.setText(cf.name());
		}
	}
	private void init() {
		setLayout(new FlowLayout());	// this needs to change when the layout gets more complicated
		createNameField();
		add(nameField);
	}
	

	private void createNameField() {
		nameField = new JTextField();		
	}
}
