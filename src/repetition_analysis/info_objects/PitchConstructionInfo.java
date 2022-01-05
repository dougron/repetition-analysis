package repetition_analysis.info_objects;

import repetition_analysis.feature_chunk.FeatureChunkWrapper;

public class PitchConstructionInfo {

	
	public int featureID;
	public FeatureChunkWrapper pitchData;
	public int pitchIndex;
	public FeatureChunkWrapper contourData;
	public int contourIndex;
	
	
	public PitchConstructionInfo(int featureID){
		this.featureID = featureID;
	}
	
	public PitchConstructionInfo(int featureID, FeatureChunkWrapper pitchData, int pitchIndex){
		this.featureID = featureID;
		this.pitchData = pitchData;
		this.pitchIndex = pitchIndex;
	}
}
