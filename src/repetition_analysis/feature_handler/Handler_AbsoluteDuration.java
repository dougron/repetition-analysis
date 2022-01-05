package repetition_analysis.feature_handler;

import repetition_analysis.feature_chunk.FeatureChunk;

public class Handler_AbsoluteDuration extends Handler implements FeatureHandler {

	public Handler_AbsoluteDuration(){
		super("ABSOLUTE_DURATION", "dur_abs", false, false, Handler.DURATION_TYPE);
		setDescription("Absolute duration on the timeline where a quarter note = 1.0");
	}
	public FeatureInfo getInfo(FeatureChunk fc){
		return new FeatureInfo(getDoubleArr(fc), FeatureChunk.ABSOLUTE_DURATION);
	}
	private double[] getDoubleArr(FeatureChunk fc) {
		double[] arr = new double[fc.chunkList.size()];
		for (int i = 0; i < fc.chunkList.size(); i++){
			arr[i] = fc.chunkList.get(i).length();
		}
		return arr;
	}

	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();

	}

}
