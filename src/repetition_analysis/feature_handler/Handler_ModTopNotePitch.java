package repetition_analysis.feature_handler;

import repetition_analysis.feature_chunk.FeatureChunk;

public class Handler_ModTopNotePitch extends Handler implements FeatureHandler {

	public Handler_ModTopNotePitch() {
		super("MOD_TOP_NOTE_PITCH", "pit_mod", false, false, Handler.MELODY_TYPE, true);
		setDescription("Modulo value of the top note (pitch without octave)");
		// TODO Auto-generated constructor stub
	}

	public FeatureInfo getInfo(FeatureChunk fc){
		return new FeatureInfo(getIntArr(fc), FeatureChunk.MOD_TOP_NOTE_PITCH);
	}
	private int[] getIntArr(FeatureChunk fc) {
		int[] arr = new int[fc.chunkList.size()];
		for (int i = 0; i < fc.chunkList.size(); i++){
			arr[i] = fc.chunkList.get(i).topNote() % 12;
		}
		return arr;
	}

	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();

	}
}
