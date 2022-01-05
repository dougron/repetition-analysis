package repetition_analysis.feature_handler;
import java.util.HashMap;

import DataObjects.combo_variables.IntAndString;
import StaticChordScaleDictionary.CSD;
import chord_progression_analyzer.ChordInKeyObject;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.note_chunk.NoteChunk;

public class Handler_TopNoteDiatonicKeyScaleDegree extends Handler implements FeatureHandler {

	
	public Handler_TopNoteDiatonicKeyScaleDegree(){
		super("DIATONIC_KEY_SCALE_TONE", "pit_dsd", true, true, Handler.MELODY_TYPE, true);
		setDescription("Top note expressed as diatonic interval (range 1-7) from root of prevailing key centre, with a diatonic/chromatic signifier(0/1)");
	}
	
	public FeatureInfo getInfo(FeatureChunk fc){
		//System.out.println("Handler_TopNoteChromaticChordTone----------------------------");
		return new FeatureInfo(getIntArr(fc), FeatureChunk.DIATONIC_KEY_SCALE_TONE);
	}
	
	private int[][] getIntArr(FeatureChunk fc) {
		int[][] arr = new int[fc.chunkList.size()][2];	// index 0 is the interval, index 1 is -1,0,+1 chromatic adjustment 
		if (fc.hasChordForm()){
			for (int i = 0; i < fc.chunkList.size(); i++){
				NoteChunk nc = fc.chunkList.get(i);
				IntAndString key = fc.cf().getPrevailingKey(nc.position());
				//System.out.println("key=" + key.i + ", " + key.str);
				int[][] keyStructure = new int[][]{};
				boolean hasKeyStructure = false;
				if (key.str == CSD.MAJOR){
					keyStructure = CSD.IONIAN_MODE.getChromaticModeModel();
					hasKeyStructure = true;
				} else if (key.str == CSD.MINOR){
					keyStructure = CSD.AOELIAN_MODE.getChromaticModeModel();
					hasKeyStructure = true;
				}
				if (hasKeyStructure){
					int keyRoot = fc.cf().getPrevailingKey(nc.position()).i;
//					System.out.println(fc.cf().chunkListToString());
//					System.out.println("keyRoot=" + keyRoot + " " + CSD.noteName(keyRoot));
					int note = nc.topNote() - key.i;
					while (note < 0) note += 12;
					while (note > 11) note -= 12;					
					arr[i] = getKeyMap(note, keyStructure);
				} else {
					isUsable = false;
					arr = nonViableAnalysisArray(fc.chunkList.size());
				}

			}
		} else {
			isUsable = false;
			arr = nonViableAnalysisArray(fc.chunkList.size());
		}
		
		
		return arr;
	}

	private int[] getKeyMap(int note, int[][] keyStructure) {
		return keyStructure[note];
	}
	
	private int[][] nonViableAnalysisArray(int size) {
		int arr[][] = new int[size][2];
		for (int i = 0; i < size; i++){
			arr[i][0] = -1;
			arr[i][1] = 0;
		}
		return arr;
	}
	
	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();
	}
}
