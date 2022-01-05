package repetition_analysis.feature_handler;

import repetition_analysis.feature_chunk.FeatureChunk;

public class Handler_AbsoluteTopNoteInterval extends Handler implements FeatureHandler {

	public Handler_AbsoluteTopNoteInterval(){
		super("ABSOLUTE_TOP_NOTE_INTERVAL", "pit_int", false, false, Handler.MELODY_TYPE);
		setDescription("Absolute interval in semitones between this note and the previous note");
	}
	public FeatureInfo getInfo(FeatureChunk fc){
		return new FeatureInfo(getIntArr(fc), FeatureChunk.ABSOLUTE_TOP_NOTE_INTERVAL);
	}
	private int[] getIntArr(FeatureChunk fc) {
		int[] arr = new int[fc.chunkList.size()];
		for (int i = 0; i < fc.chunkList.size(); i++){
			arr[i] = fc.chunkList.get(i).topNote() - fc.chunkList.get(i).previous().topNote();
		}
		return arr;
	}

	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();

	}
}
