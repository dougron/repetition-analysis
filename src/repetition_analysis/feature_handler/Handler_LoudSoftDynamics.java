package repetition_analysis.feature_handler;

import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.note_chunk.NoteChunk;

public class Handler_LoudSoftDynamics extends Handler implements FeatureHandler {
	
	public static int loudDynamic = 96;
	public static int softDynamic = 64;

	public Handler_LoudSoftDynamics(){
		super("LOUD_SOFT_DYNAMICS", "dyn_lsd", false, false, Handler.DYNAMICS_TYPE);
		setDescription("Loud or soft dynamic. loud = 1, soft = 0");
	}
	public FeatureInfo getInfo(FeatureChunk fc){
		return new FeatureInfo(getIntArr(fc), FeatureChunk.LOUD_SOFT_DYNAMICS);
	}

	private int[] getIntArr(FeatureChunk fc) {
		// this is a relative loud soft calculation based on the FeatureChunk
		// not interesting in the case of cell length = 1 but going to leave it as such
		int average = fc.averageVelocity();
		int[] arr = new int[fc.chunkList.size()];
		int index = 0;
		for (NoteChunk nc: fc.chunkList){
			if (nc.averageVelocity() < average){
				arr[index] = softDynamic;
			} else {
				arr[index] = loudDynamic;
			}
			index ++;
		}	
		return arr;
	}

	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();

	}
	

}

