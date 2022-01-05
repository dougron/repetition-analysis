package repetition_analysis.feature_picker;

import java.util.ArrayList;
import java.util.HashMap;

import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;

public class FeatureGamePicker {
	
	HashMap<Integer, ArrayList<FeatureChunkRepeatSchema>> map = new HashMap<Integer, ArrayList<FeatureChunkRepeatSchema>>();

	public FeatureGamePicker(){
		
	}

	public void setData(int elementType, ArrayList<FeatureChunkRepeatSchema> outputList) {
		map.put(elementType, outputList);
		
	}
	public boolean hasContour(){
		if (map.containsKey(FeatureChunk.CONTOUR_TYPE)){
			return true;
		} else {
			return false;
		}
	}
	public boolean canGenerateOutput(){
		// minimum requirements to work in the generator
		for (int i: requiredElements){
			if (!map.containsKey(i)){
				return false;
			}
		}
		return true;
	}
	private static final int[] requiredElements = new int[]{
			FeatureChunk.RHYTHM_TYPE,
			FeatureChunk.DURATION_TYPE,
			FeatureChunk.DYNAMICS_TYPE,
			FeatureChunk.PITCH_TYPE
	};
	
}
