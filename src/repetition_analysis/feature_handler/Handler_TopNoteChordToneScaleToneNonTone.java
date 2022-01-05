package repetition_analysis.feature_handler;
import DataObjects.combo_variables.IntAndString;
import chord_progression_analyzer.ChordInKeyObject;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.note_chunk.NoteChunk;

public class Handler_TopNoteChordToneScaleToneNonTone extends Handler implements FeatureHandler {
	
//	private FeatureHandler fh = new Handler_TopNoteDiatonicChordFunctionTone();

	public Handler_TopNoteChordToneScaleToneNonTone(){
		super("CHORD_SCALE_NONSCALE_TONE", "pit_csn", true, true, Handler.MELODY_TYPE, true);
		setDescription("Top note described as a 0)chord tone, 1)scale tone or 2)non scale tone");
	}
	
	public FeatureInfo getInfo(FeatureChunk fc){
		//System.out.println("Handler_TopNoteChromaticChordTone----------------------------");
		return new FeatureInfo(getIntArr(fc), FeatureChunk.CHORD_SCALE_NONSCALE_TONE);
	}
	
	private int[] getIntArr(FeatureChunk fc) {
		// 0 - chord tone 1 - scale tone 2 - non scale tone
		int[] arr = new int[fc.chunkList.size()];
		if (fc.hasChordForm()){
			for (int i = 0; i < fc.chunkList.size(); i++){
				NoteChunk nc = fc.chunkList.get(i);
				ChordInKeyObject ciko = fc.cf().getPrevailingCIKO(nc.position(), nc.lc);
				int[][] ccs = ciko.chromaticChordScale();
				int[] chordTones = ciko.cc.getNotePatternAnalysis().intervals();
				IntAndString chord = fc.cf().getPrevailingChord(nc.position, nc.lc);
				int note = nc.topNote();
				note -= chord.i;
				while (note > 11) note -= 12;
				while (note < 0) note += 12;
				if (arrayContainsModNote(chordTones, note)){
					arr[i] = 0;		// chord tone
				} else {
					int[] crar = ccs[note];
					if (crar[1] == 0){
						arr[i] = 1;		// scale tone
					} else {
						arr[i] = 2;		// non scale tone
					}
				}
			}
			
			
		} else {
			isUsable = false;
			for (int i = 0; i < fc.chunkList.size(); i++){
				arr[i] = -1;
			}
		}	
		return arr;
	}

	private boolean arrayContains(int[] array, int val) {
		for (int i: array){
			if (i == val){
				return true;
			}
		}
		return false;
	}
	private boolean arrayContainsModNote(int[] array, int val) {
		// same as arrayContains except taking into account that 9th degrees in the interval() array
		// come out above 12 and need to be compatred to a note which is %12
		for (int i: array){
			if (i % 12 == val){
				return true;
			}
		}
		return false;
	}

	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();
	}
}