package repetition_analysis.feature_handler;
import StaticChordScaleDictionary.CSD;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.note_chunk.NoteChunk;

/*
 * TopNote() of NoteChunk as an interval from the prevailing key. range 0 - 11, but
 * returns -1 if there is no ChordForm present and the key cannot be ascertained
 */
public class Handler_TopNoteChromaticScaleDegree extends Handler implements FeatureHandler {
	
	

	public Handler_TopNoteChromaticScaleDegree(){
		super("CHROMATIC_SCALE_DEGREE", "pit_csd", true, true, Handler.MELODY_TYPE, true);
		setDescription("Top note expressed as semitone distance (range 0-11) from root of prevailing key centre");
	}
	public FeatureInfo getInfo(FeatureChunk fc){
		return new FeatureInfo(getIntArr(fc), FeatureChunk.CHROMATIC_SCALE_DEGREE);
	}
	private int[] getIntArr(FeatureChunk fc) {
		int[] arr = new int[fc.chunkList.size()];
		if (fc.hasChordForm()){
			for (int i = 0; i < fc.chunkList.size(); i++){
				NoteChunk nc = fc.chunkList.get(i);
				int keyRoot = fc.cf().getPrevailingKey(nc.position()).i;
//				System.out.println(fc.cf().chunkListToString());
//				System.out.println("keyRoot=" + keyRoot + " " + CSD.noteName(keyRoot));
				int note = nc.topNote() - keyRoot;
				while (note < 0) note += 12;
				while (note > 11) note -= 12;
				arr[i] = note;
			}
		} else {
			isUsable = false;
			for (int i = 0; i < fc.chunkList.size(); i++){
				arr[i] = -1;
			}
		}
		
		
		return arr;
	}

	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();

	}
}
