package gui_objects.feature_chooser_panel;

import javax.swing.JCheckBox;

import repetition_analysis.feature_chunk.FeatureChunk;

public class FeatureTypeCheckBox extends JCheckBox {

	private int featureGroupIndex;

	public FeatureTypeCheckBox(int featureGroupIndex){
		setText(FeatureChunk.featureTypeName[featureGroupIndex]);
		setOpaque(false);
		this.featureGroupIndex = featureGroupIndex;
	}
	public int featureGroupIndex(){
		return featureGroupIndex;
	}
}
