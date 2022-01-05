package repetition_analysis.feature_handler;

import repetition_analysis.feature_chunk.FeatureChunk;

/*
 * as of 26 June 2017, this class delivers a value for each note in a FeatureChunk as opposed to 
 * each gap in a FeatureChunk, and the number now represents the interval from the previous note
 * If the note is the first in the piece, it calculates the interval off of the topNote of the last 
 * NoteChunk, cos we are currently assuming jazz and forms that cycle
 */
public class Handler_AbsoluteTopNotePitch extends Handler implements FeatureHandler {

	public Handler_AbsoluteTopNotePitch(){
		super("ABSOLUTE_TOP_NOTE_PITCH", "pit_abs", false, false, Handler.MELODY_TYPE);
		setDescription("Absolute midi pitch of the top note");
	}
	public FeatureInfo getInfo(FeatureChunk fc){
		return new FeatureInfo(getIntArr(fc), FeatureChunk.ABSOLUTE_TOP_NOTE_PITCH);
	}
	private int[] getIntArr(FeatureChunk fc) {
		int[] arr = new int[fc.chunkList.size()];
		for (int i = 0; i < fc.chunkList.size(); i++){
			arr[i] = fc.chunkList.get(i).topNote();
		}
		return arr;
	}

	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();

	}

}
