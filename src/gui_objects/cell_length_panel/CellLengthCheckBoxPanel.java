package gui_objects.cell_length_panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CellLengthCheckBoxPanel extends JPanel {

	private JCheckBox[] cbArray;
	private ArrayList<Integer> lengthSelections = new ArrayList<Integer>();
	private CellLengthPanelParent parent;
	
	public CellLengthCheckBoxPanel(int[] lengthOptions, CellLengthPanelParent parent){
		this.parent = parent;
		setLayout(new GridBagLayout());
		setOpaque(false);
		
		JLabel cellLengthLabel = new JLabel("cell length");
		GridBagConstraints gbc_cll = getGridBagConstraints(0, 0);
		gbc_cll.gridwidth = 5;
		add(cellLengthLabel, gbc_cll);
		
		int gridxcount = 6;
//		int[] lengthOptions = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
		int xpos = 0;
		int ypos = 1;
		cbArray = new JCheckBox[lengthOptions.length];
		int index = 0;
		for (int i: lengthOptions){
			JCheckBox box = new JCheckBox();
			box.setText("" + i);
			box.setOpaque(false);
			box.addItemListener(lengthCheckBoxItemListener());
			cbArray[index] = box;
			GridBagConstraints gbc_box = new GridBagConstraints();
			gbc_box.gridx = xpos;
			gbc_box.gridy = ypos;
			gbc_box.anchor = GridBagConstraints.WEST;
			add(box, gbc_box);
			xpos++;
			if (xpos == gridxcount){
				xpos = 0;
				ypos++;
			}
			index++;
		}
		
		
	}
	public void setEnabled(boolean b){
		for (JCheckBox box: cbArray){
			box.setEnabled(b);
		}
	}
	public void addSelectedIndex(int i){
		lengthSelections.add(i);
		setLengthCheckBoxes(lengthSelections);
	}
	public void setLengthCheckBoxes(ArrayList<Integer> list) {
		for (JCheckBox box: cbArray){
			for (ItemListener il: box.getItemListeners()){
				box.removeItemListener(il);
			}
			int index = Integer.parseInt(box.getText());
			if (list.contains(index)){				
				box.setSelected(true);
			} else {
				box.setSelected(false);
			}
			box.addItemListener(lengthCheckBoxItemListener());
		}
		
	}
	private ItemListener lengthCheckBoxItemListener() {
		
		return new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				System.out.println("Checking length option buttons");
				lengthSelections.clear();
				for (JCheckBox box: cbArray){
					if (box.isSelected()){
						//System.out.println(box.getText());
						lengthSelections.add(Integer.parseInt(box.getText()));
					}
				}
				String str = "lengthSelections=";
				for (Integer i: lengthSelections){
					str += i + ",";
				}
				System.out.println(str);
				parent.fileBrowserIsUpdated();
			}
			
		};
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
	public int selectedCount() {
		return lengthSelections.size();
	}
	public ArrayList<Integer> selectedIndexList() {
		return lengthSelections;
	}
	public String cellLengthsToString() {
		String str = "";
		if (lengthSelections.size() == 0){
			str += "no cell length options";
		} else {
			for (Integer i: lengthSelections){
				str += i + ",";
			}
		}
		return null;
	}

}
