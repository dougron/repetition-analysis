package repetition_analysis.feature_handler;

import repetition_analysis.feature_chunk.FeatureChunk;

public class Handler_GapValue extends Handler implements FeatureHandler {

	public Handler_GapValue(){
		super("GAP_VALUE", "rhy_gap", false, false, Handler.RHYTHM_TYPE);
		setDescription("Inter onset distance between this note and the next");
	}
	public FeatureInfo getInfo(FeatureChunk fc){
		return new FeatureInfo(getDoubleArr(fc), FeatureChunk.GAP_VALUE);
		
	}
	private double[] getDoubleArr(FeatureChunk fc) {
		double[] arr = new double[fc.chunkList.size()];
		for (int i = 0; i < fc.chunkList.size(); i++){
			double pos1 = fc.chunkList.get(i).next().position();
			double pos2 = fc.chunkList.get(i).position();
			arr[i] = fc.chunkList.get(i).next().position() - fc.chunkList.get(i).position();
		}
		return arr;
	}

	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();

	}

}
