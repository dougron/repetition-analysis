package repetition_analysis.feature_handler;
import DataObjects.combo_variables.IntAndString;
import StaticChordScaleDictionary.CSD;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.note_chunk.NoteChunk;

public class Handler_TopNoteDiatonicChordFunctionTone extends Handler implements FeatureHandler {
	
	

	public Handler_TopNoteDiatonicChordFunctionTone(){
		super("DIATONIC_CHORD_FUNCTION_TONE", "pit_dct", true, true, Handler.MELODY_TYPE, true);
		setDescription("Top note expressed as diatonic interval (1-7) and a chromatic offset 0=none +1/-1 up or down a semitone");
	}
	
	public FeatureInfo getInfo(FeatureChunk fc){
		//System.out.println("Handler_TopNoteChromaticChordTone----------------------------");
		return new FeatureInfo(getIntArr(fc), FeatureChunk.DIATONIC_CHORD_FUNCTION_TONE);
	}
	
	private int[][] getIntArr(FeatureChunk fc) {
		int[][] arr = new int[fc.chunkList.size()][2];
		if (fc.hasChordForm()){
			for (int i = 0; i < fc.chunkList.size(); i++){
				NoteChunk nc = fc.chunkList.get(i);
//				System.out.println("-----------\n" + nc.toString());
				int[][] ccs = fc.cf().getPrevailingCIKO(nc.position(), nc.lc).chromaticChordScale();
				IntAndString chord = fc.cf().getPrevailingChord(nc.position, nc.lc);
//				System.out.println(chord.toString());
//				System.out.println(fc.cf().chunkListToString());
				//System.out.println("chordRoot=" + chord.i + " " + CSD.noteName(chord.i) + " " + chord.str);
				int note = nc.topNote() - chord.i;
				while (note < 0) note += 12;
				while (note > 11) note -= 12;
				//System.out.println("note index=" + note + " ccs[note]=" + ccs[note][0] + "," + ccs[note][1]);
				arr[i] = ccs[note];
			}
		} else {
			isUsable = false;
			for (int i = 0; i < fc.chunkList.size(); i++){
				arr[i][0] = -1;
				arr[i][1] = -1;
			}
		}	
		return arr;
	}

	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();
	}
}