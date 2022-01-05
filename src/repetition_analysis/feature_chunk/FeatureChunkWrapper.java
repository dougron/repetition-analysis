package repetition_analysis.feature_chunk;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.combo_variables.IntAndDouble;
import repetition_analysis.feature_handler.FeatureInfo;

/*
 * this class wraps a FeatureChunk and defines its primary attribute under consideration 
 * this is defined as featureOption
 */
public class FeatureChunkWrapper {

	private FeatureChunk fc;
	private int featureID;

	public FeatureChunkWrapper(FeatureChunk fc, int featureID) {
		this.fc = fc;
		this.featureID = featureID;
	}
	public int featureID(){
		return featureID;
	}
	public FeatureChunk featureChunk(){
		return fc;
	}
	public FeatureInfo getFeatureInfo(){
		return fc.getFeatureInfo(featureID);
	}
	public double position(){
		return fc.position();
	}
	public String featureString(){
		return fc.getFeatureString(featureID);
	}
	public String featureString(int featureID){		// for returning non primary featureStrings
		return fc.getFeatureString(featureID);	
	}
	public String name(){
		return FeatureChunk.getFeatureName(featureID);
	}
	public String shortName(){
		return FeatureChunk.getFeatureShortName(featureID);
	}
	public IntAndDouble getBarStartParam(){
		return fc.getBarStartParam();
	}
	public IntAndDouble getBarEndParam(){
		return fc.getBarEndParam();
	}
	public IntAndDouble getXOverBarPosParam(double xover){
		return fc.getXOverBarPosParam(xover);
	}
	public String toString(){
		String str = name() + ":- ";
		str += fc.getFeatureString(featureID);
		return str;
	}
	public LiveClip lc(){
		return fc.lc();
	}
	public FeatureChunkWrapper originalSimpleContour(){
		return new FeatureChunkWrapper(fc, FeatureChunk.SIMPLE_MELODY_CONTOUR);
	}


}
