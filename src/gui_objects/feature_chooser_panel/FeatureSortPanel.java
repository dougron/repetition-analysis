package gui_objects.feature_chooser_panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import repetition_analysis.feature_chunk.FeatureChunk;

public class FeatureSortPanel extends JPanel {
	
	private FeatureTypeCheckBox[] checkBoxArr;
	private FeatureSortParent fsparent;

	public FeatureSortPanel(FeatureSortParent parent){
		this.fsparent = parent;
		setLayout();
		checkBoxArr = addGUIObjects();
		setOpaque(false);
	}
	public ArrayList<Integer> getfeatureIDList(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		boolean isAllUnchecked = isAllUnchhecked();
		for (FeatureTypeCheckBox cb: checkBoxArr){
			if (isAllUnchecked){
				addArrToList(list, FeatureChunk.featureTypeIndexGroup[cb.featureGroupIndex()]);
			} else if (cb.isSelected()){
				addArrToList(list, FeatureChunk.featureTypeIndexGroup[cb.featureGroupIndex()]);
			}
		}
		
		return list;
	}

	private void addArrToList(ArrayList<Integer> list, int[] is) {
		for (int i: is){
			list.add(i);
		}
		
	}
	private boolean isAllUnchhecked() {
		for (FeatureTypeCheckBox cb: checkBoxArr){
			if (cb.isSelected()){
				return false;
			}
		}
		return true;
	}
	private FeatureTypeCheckBox[] addGUIObjects() {
		int[] arr = new int[]{FeatureChunk.RHYTHM_TYPE, FeatureChunk.PITCH_TYPE, FeatureChunk.DURATION_TYPE, FeatureChunk.DYNAMICS_TYPE, FeatureChunk.CONTOUR_TYPE};
		FeatureTypeCheckBox[] cbArr = new FeatureTypeCheckBox[arr.length];
		for (int i = 0; i < arr.length; i++){
			FeatureTypeCheckBox cb = new FeatureTypeCheckBox(arr[i]);
			cb.addActionListener(actionListener());
			cbArr[i] = cb;
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridx = 0;
			gbc.gridy = i;
			add(cb, gbc);
		}
		return cbArr;
	}
	private ActionListener actionListener(){
		return new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				fsparent.updateSortedPanel(getfeatureIDList());
				
			}
			
		};
	}

	private void setLayout() {
		setLayout(new GridBagLayout());
		
	}
}
