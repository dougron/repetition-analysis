package repetition_analysis.feature_handler;

import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.note_chunk.NoteChunk;

public class Handler_PolyModPitch extends Handler implements FeatureHandler {

	
	public Handler_PolyModPitch(){
		super("POLY_MOD_PITCH", "mod_p", false, false, Handler.MELODY_TYPE, true);
	}
	@Override
	public FeatureInfo getInfo(FeatureChunk fc) {
		return new FeatureInfo(getIntArr(fc), FeatureChunk.POLY_MOD_PITCH);

	}
	private int[][] getIntArr(FeatureChunk fc) {
		int[][] arr = new int[fc.chunkList.size()][];
		int index = 0;
		for (NoteChunk nc: fc.chunkList){			
			arr[index] = nc.modNoteArray();
			index++;
		}
		return arr;
	}


	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();

	}
	@Override
	public boolean isPolyphonic(){
		return true;
	}

}
