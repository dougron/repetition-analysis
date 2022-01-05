package repetition_analysis.feature_handler;

import repetition_analysis.feature_chunk.FeatureChunk;

public class Handler_SimpleMelodyContour extends Handler implements FeatureHandler {

	public Handler_SimpleMelodyContour(){
		super("SIMPLE_MELODY_CONTOUR", "cnt_sim", false, true, Handler.CONTOUR_TYPE);
		setDescription("Simple melody contour: range (-1, 1)");
	}
	public FeatureInfo getInfo(FeatureChunk fc){
		return new FeatureInfo(getIntArr(fc), FeatureChunk.SIMPLE_MELODY_CONTOUR);
	}
	private int[] getIntArr(FeatureChunk fc) {
		int[] arr = new int[fc.chunkList.size()];
		for (int i = 0; i < fc.chunkList.size(); i++){
			arr[i] = (int)Math.signum(fc.chunkList.get(i).topNote() - fc.chunkList.get(i).previous().topNote());
		}
		return arr;
	}

	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();

	}
	
}
