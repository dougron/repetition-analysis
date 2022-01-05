package repetition_analysis.feature_handler;

import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.note_chunk.NoteChunk;

public class Handler_PolyAbsolutePitch extends Handler implements FeatureHandler {

	
	public Handler_PolyAbsolutePitch(){
		super("POLY_ABOSULTE_PITCH", "abs_p", false, false, Handler.MELODY_TYPE);
	}
	public FeatureInfo getInfo(FeatureChunk fc){
		return new FeatureInfo(getIntArr(fc), FeatureChunk.POLY_ABSOLUTE_PITCH);
	}
	private int[][] getIntArr(FeatureChunk fc) {
		int[][] arr = new int[fc.chunkList.size()][];
		int index = 0;
		for (NoteChunk nc: fc.chunkList){			
			arr[index] = nc.noteArray();
			index++;
		}
		return arr;
	}
//	private String getInfoString(FeatureChunk fc) {
//		String str = "";
//		for (NoteChunk nc: fc.chunkList){
//			str += nc.absoluteNoteString();
//		}
//		return str;
//	}
	@Override
	public boolean isPolyphonic(){
		return true;
	}


	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();

	}
}
