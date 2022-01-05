package gui_objects.feature_chooser_panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JPanel;

import DataObjects.combo_variables.IntAndString;
import ResourceUtils.ChordForm;
import gui_objects.ColourUpdateParent;
import gui_objects.main_ui.RepetitionAnalysisParent;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_picker.FeatureGamePicker;
import repetition_analysis.feature_picker.FeaturePicker;
import repetition_analysis.index_list.IndexList;
import repetition_analysis.repeat_schema_list.SchemaListObject;

public class FeatureChooserGamePanel extends JPanel implements RepetitionAnalysisParent {
	
	//FeaturePicker fp = new FeaturePicker();

	public static final IntAndString[] nameArr = new IntAndString[]{		// these two static arrays must be the same length
			new IntAndString(FeatureChunk.RHYTHM_TYPE, "RHYTHM"), 
			new IntAndString(FeatureChunk.DURATION_TYPE, "DURATION"), 			
			new IntAndString(FeatureChunk.DYNAMICS_TYPE, "DYNAMICS"), 
			new IntAndString(FeatureChunk.PITCH_TYPE, "PITCH"), 
			new IntAndString(FeatureChunk.CONTOUR_TYPE, "CONTOUR")
			};
	public static final Comparator[] compArr = new Comparator[]{
			FeatureChunkRepeatSchema.featureIDAndArrayComparator,
			FeatureChunkRepeatSchema.featureIDAndArrayComparator,
			FeatureChunkRepeatSchema.featureIDAndArrayComparator,
			FeatureChunkRepeatSchema.featureIDAndLastToFirstArrayComparator,
			FeatureChunkRepeatSchema.featureIDAndArrayComparator
	};
	public FeatureGameChooser[] fcArr;

	private RepetitionAnalysisParent raParent;
	private ColourUpdateParent colourParent;
	
	public FeatureChooserGamePanel(RepetitionAnalysisParent raParent){
		this.raParent = raParent;
		this.colourParent = (ColourUpdateParent) raParent;
		makeLayout();
		makeChooserArray();
		testAndSetBackground();
	}
	public void setAttachListContent(ArrayList<IndexList> list){
		for (FeatureGameChooser fc: fcArr){
			fc.setAttachList(list);
		}
		setParameterHasChanged(true);
	}
	public void setOptionListContent(RepetitionAnalysis ra){
		if (ra == null){
			ArrayList<FeatureChunkRepeatSchema> emptyList = new ArrayList<FeatureChunkRepeatSchema>();
			for (FeatureGameChooser fc: fcArr){
				fc.setOptionList(emptyList);
			}
		} else {
			for (FeatureGameChooser fc: fcArr){
				fc.setOptionList(makeOptionList(fc.elementType, ra));
			}
		}
		
	}
	public boolean hasEnoughToGenerateOutput() {
		for (FeatureGameChooser fc: fcArr){
			if (fc.elementType != FeatureChunk.CONTOUR_TYPE){
				if (!fc.hasOutputList()){
					//System.out.println(fc.getName() + " does not have output. Not enough to generate output");
					return false;
				}
			}
		}
		return true;
	}

// private -------------------------------------------------------------------------------------
	
	private ArrayList<FeatureChunkRepeatSchema> makeOptionList(int elementType, RepetitionAnalysis ra) {
		ArrayList<FeatureChunkRepeatSchema> list = new ArrayList<FeatureChunkRepeatSchema>();
		for (SchemaListObject slo: ra.getSchemaList().schemaList){
			if (slo instanceof FeatureChunkRepeatSchema){
				FeatureChunkRepeatSchema fcrs = (FeatureChunkRepeatSchema)slo;
				if (arrayContains(FeatureChunk.featureTypeIndexGroup[elementType], fcrs.featureID())){
					list.add(fcrs);
				}
			}
		}
		
		return list;
	}
	private boolean arrayContains(int[] is, int featureID) {
		for (int i: is){
			if (i == featureID){
				return true;
			}
		}
		return false;
	}
	private void makeChooserArray() {
		fcArr = new FeatureGameChooser[nameArr.length];
		GridBagConstraints gbc = new GridBagConstraints();
		for (int i = 0; i < nameArr.length; i++){
			IntAndString ias = nameArr[i];
			fcArr[i] = new FeatureGameChooser(ias.i, ias.str, this, compArr[i]);
			gbc.gridx = 0;
			gbc.gridy = i;
			add(fcArr[i], gbc);
		}
		
	}
	public void testAndSetBackground() {
		if (hasEnoughToGenerateOutput()){
			if (colourParent.isOn()) {
				setBackground(Color.green);
			} else {
				setBackground(Color.gray);
			}
		} else {
			if (colourParent.isOn()){
				setBackground(Color.pink);
			} else {
				setBackground(Color.lightGray);
			}
		}
		
	}

	
	private void makeLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0};
		setLayout(layout);		
	}
	@Override
	public ChordForm cf() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean parameterHasChanged() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setParameterHasChanged(boolean b) {
//		System.out.println("FeatureChooserGamePanel.setParameterHasChanged() called");
//		testAndSetBackground();
		colourParent.updateSubPanelColours();
		if (hasEnoughToGenerateOutput()){
			raParent.setParameterHasChanged(true);
		}
		
	}

	@Override
	public boolean isCalculating() {
		// TODO Auto-generated method stub
		return false;
	}

	public FeatureGamePicker fp() {				// this would be for when and if GenFour was working
		FeatureGamePicker fgp = new FeatureGamePicker();
		for (FeatureGameChooser fgc: fcArr){
			if (fgc.hasOutputList()){
				fgp.setData(fgc.elementType, fgc.getOutputList());
			}
			
		}
		
		return fgp;
	}
	public FeaturePicker getFeaturePicker() {
		FeaturePicker fp = new FeaturePicker();
		for (FeatureGameChooser fc: fcArr){
			if (fc.hasOutputList()){
				switch (fc.elementType){
				case FeatureChunk.RHYTHM_TYPE:
					fp.setArray(fc.fcwSelectedList, FeatureChooser.RHYTHM);
					fp.setMasterFeatureID(fc.getMasterRepeatSchemaAttachIndex(), FeatureChooser.RHYTHM);
					break;
				case FeatureChunk.DURATION_TYPE:
					fp.setArray(fc.fcwSelectedList, FeatureChooser.DURATION);
					fp.setMasterFeatureID(fc.getMasterRepeatSchemaAttachIndex(), FeatureChooser.DURATION);
					break;
				case FeatureChunk.DYNAMICS_TYPE:
					fp.setArray(fc.fcwSelectedList, FeatureChooser.DYNAMICS);
					fp.setMasterFeatureID(fc.getMasterRepeatSchemaAttachIndex(), FeatureChooser.DYNAMICS);
					break;
				case FeatureChunk.PITCH_TYPE:
					fp.setArray(fc.fcwSelectedList, FeatureChooser.PITCH);
					fp.setMasterFeatureID(fc.getMasterRepeatSchemaAttachIndex(), FeatureChooser.PITCH);
					break;
				case FeatureChunk.CONTOUR_TYPE:
					fp.setArray(fc.fcwSelectedList, FeatureChooser.CONTOUR);
					fp.setMasterFeatureID(fc.getMasterRepeatSchemaAttachIndex(), FeatureChooser.CONTOUR);
					break;
				}
			}
		}
		return fp;
	}
	public void clearAll() {
		for (FeatureGameChooser fgc: fcArr){
			fgc.clearSelectedList();
		}
		
	}
	@Override
	public void setMasterRepeatSchemaPanelBarCount(int barCount) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateRSGraphic() {
		// TODO Auto-generated method stub
		
	}


}
