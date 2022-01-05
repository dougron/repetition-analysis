package repetition_analysis.feature_handler;
import DataObjects.combo_variables.IntAndString;
import StaticChordScaleDictionary.CSD;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.note_chunk.NoteChunk;


	/*
	 * TopNote() of NoteChunk as an interval from the prevailing chord root. range 0 - 11, but
	 * returns -1 if there is no ChordForm present and the prevailing chord cannot be ascertained
	 */
public class Handler_TopNoteChromaticChordTone extends Handler implements FeatureHandler {
	
	

	public Handler_TopNoteChromaticChordTone(){
		super("CHROMATIC_CHORD_TONE", "pit_cct", true, true, Handler.MELODY_TYPE, true);
		setDescription("Top note chromatic chord tone expressed as semitone distance (range 0-11) from root of prevailing chord");
	}
	
	public FeatureInfo getInfo(FeatureChunk fc){
		//System.out.println("Handler_TopNoteChromaticChordTone----------------------------");
		return new FeatureInfo(getIntArr(fc), FeatureChunk.CHROMATIC_CHORD_TONE);
	}
	
	private int[] getIntArr(FeatureChunk fc) {
		int[] arr = new int[fc.chunkList.size()];
		if (fc.hasChordForm()){
			for (int i = 0; i < fc.chunkList.size(); i++){
				NoteChunk nc = fc.chunkList.get(i);
//				System.out.println("-----------\n" + nc.toString());
				IntAndString chord = fc.cf().getPrevailingChord(nc.position(), nc.lc);
//				System.out.println(chord.toString());
//				System.out.println(fc.cf().chunkListToString());
//				System.out.println("chordRoot=" + chord.i + " " + CSD.noteName(chord.i) + " " + chord.str);
				int note = nc.topNote() - chord.i;
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