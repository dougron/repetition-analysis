package repetition_analysis.repetition_generator;
import java.util.ArrayList;
import java.util.TreeMap;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import DataObjects.incomplete_note_utils.FinalListNote;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;

/*
 * quick and nasty generator using secondary repetition schemas. meant for monophonic melody
 */
public class GenTwo{
	
	private TreeMap<Double, FinalListNote> finalNoteMap = new TreeMap<Double, FinalListNote>();
	private double outputPosOffset = 0.0;
	private LiveClip lc = null;
	
	public GenTwo(){
		
	}
	public LiveClip makeLiveClip(
			FeatureChunkRepeatSchema primaryRepeatSchema,		//
			FeatureChunkRepeatSchema[] rhythmFeatureArr,
			int featureID_Rhythm,
			FeatureChunkRepeatSchema[] noteFeatureArr, 				// array of items indicating note features
			int featureID_Note,										// featureID of secondary index list in primary repeatScheme to indicate use of previous material
			FeatureChunkRepeatSchema[] dynamicsFeatureArr, 			// // must be a primary dynamics feature
			int featureID_Dynamics, 
			FeatureChunkRepeatSchema[] durationFeatureArr, 
			int featureID_Duration, 
			double formLength
			){
		// will always regenerate LiveClip
		LiveClip lc = new LiveClip(0, 0);
		lc.length = formLength;
		lc.loopEnd = formLength;
		double[] posList = primaryRepeatSchema.posArray();
//		double[] gapArr = rhythmGapFeature.getPrimaryFeatureChunk().featureChunk().gapValueArray();
		double[][] gapArrArr = makeGapArrArr(rhythmFeatureArr);
		int[] gapIndexArr = primaryRepeatSchema.getIndexList(featureID_Rhythm).getiList();
		int[][] noteArrArr = makeNoteArrArr(noteFeatureArr);
		int[] noteIndexArr = primaryRepeatSchema.getIndexList(featureID_Note).getiList();
		int[][] dynArrArr = makeDynArrArr(dynamicsFeatureArr);
		int[] dynamicsIndexArr = primaryRepeatSchema.getIndexList(featureID_Dynamics).getiList();
		double[][] durationArrArr = makeDurationArrArr(durationFeatureArr);
		int[] durationIndexArr = primaryRepeatSchema.getIndexList(featureID_Duration).getiList();
		
		int index = 0;
		for (double pos: posList){
			double[] gapArr = gapArrArr[gapIndexArr[index % gapIndexArr.length] % gapArrArr.length];
			int[] noteArr = noteArrArr[noteIndexArr[index % noteIndexArr.length] % noteArrArr.length];
			int[] velocityArr = dynArrArr[dynamicsIndexArr[index % dynamicsIndexArr.length] % dynArrArr.length];
			double[] durationArr = durationArrArr[durationIndexArr[index % durationIndexArr.length] % durationArrArr.length];
			makeFinalNotes(pos, gapArr, noteArr, velocityArr, durationArr);
			index++;
		}
		for (FinalListNote fln: finalNoteMap.values()){
			lc.addNoteList(fln.getNoteList());
		}
		dealWithOverlaps(lc);
		dealWithPosOffset(lc);
		return lc;
	}
	public TreeMap<Double, FinalListNote> getFinalNoteList(){
		return finalNoteMap;
	}
	private void dealWithPosOffset(LiveClip lc) {
		for (LiveMidiNote lmn: lc.noteList){
			lmn.position += outputPosOffset;
		}
		lc.loopEnd += outputPosOffset;
		lc.loopStart += outputPosOffset;
	}
	private void dealWithOverlaps(LiveClip lc) {
		lc.sortNoteList();
		for (int i = 0; i < lc.noteList.size() - 1; i++){
			LiveMidiNote lmn1 = lc.noteList.get(i);
			LiveMidiNote lmn2 = lc.noteList.get(i + 1);
			if (lmn1.position < lmn2.position){
				if (lmn1.position + lmn1.length > lmn2.position){
					lmn1.length = lmn2.position - lmn1.position;
				}
			}
		}
		
	}
	private double[][] makeGapArrArr(FeatureChunkRepeatSchema[] noteFeatureArr) {
		double[][] arrarr = new double[noteFeatureArr.length][];
		for (int i = 0; i < noteFeatureArr.length; i++){
			FeatureChunkRepeatSchema fcrs = noteFeatureArr[i];
			double[] arr = fcrs.getPrimaryFeatureChunk().featureChunk().getFeatureInfo(FeatureChunk.GAP_VALUE).doubleArrOne;
			arrarr[i] = arr;
		}
		return arrarr;
	}
	private double[][] makeDurationArrArr(FeatureChunkRepeatSchema[] noteFeatureArr) {
		double[][] arrarr = new double[noteFeatureArr.length][];
		for (int i = 0; i < noteFeatureArr.length; i++){
			FeatureChunkRepeatSchema fcrs = noteFeatureArr[i];
			double[] arr = fcrs.getPrimaryFeatureChunk().featureChunk().getFeatureInfo(FeatureChunk.ABSOLUTE_DURATION).doubleArrOne;
			arrarr[i] = arr;
		}
		return arrarr;
	}
	private int[][] makeDynArrArr(FeatureChunkRepeatSchema[] noteFeatureArr) {
		int[][] arrarr = new int[noteFeatureArr.length][];
		for (int i = 0; i < noteFeatureArr.length; i++){
			//FeatureChunkRepeatSchema fcrs = noteFeatureArr[i];
			int[] arr = new int[]{60, 90, 60, 90, 60, 90};			// preset for testing. currently there are no dynamics featuresd implemented
			arrarr[i] = arr;
		}
		return arrarr;
	}
	private int[][] makeNoteArrArr(FeatureChunkRepeatSchema[] noteFeatureArr) {
		int[][] arrarr = new int[noteFeatureArr.length][];
		for (int i = 0; i < noteFeatureArr.length; i++){
			FeatureChunkRepeatSchema fcrs = noteFeatureArr[i];
			int[] arr = fcrs.getPrimaryFeatureChunk().featureChunk().getFeatureInfo(FeatureChunk.ABSOLUTE_TOP_NOTE_PITCH).intArrOne;
			arrarr[i] = arr;
		}
		return arrarr;
	}
	public void makeFinalNotes(double pos, double[] gapArr, int[] noteArr, int[] velocityArr, double[] durationArr){
		int noteCount = gapArr.length + 1;
		double newPos = pos;
		for (int i = 0; i < noteCount; i++){
			FinalListNote fln = new FinalListNote(newPos);
			fln.addNote(noteArr[i % noteArr.length]);
			fln.setVelocity(velocityArr[i % velocityArr.length]);
			double dur = durationArr[i % durationArr.length];
			if (i < noteCount - 1){				
				if (dur > gapArr[i]){
					dur = gapArr[i];
				} 
			}
			fln.setDuration(dur);
			addNewNote(fln);
			if (i < noteCount - 1){
				newPos += gapArr[i % gapArr.length];
			}
		}
	}
	public void clearMap(){
		finalNoteMap.clear();
	}
	public String toString(){
		String str = "GenTwo---------------";
		str += "\nfinalNoteMap:-\n";
		for (double d: finalNoteMap.keySet()){
			str += d + ": - " + finalNoteMap.get(d).toString() + "\n";
		}
		return str;
	}
	public void setOutputPosOffset(double offset) {
		this.outputPosOffset = offset;
		
	}

	
// private ------------------------------------------------------------------------
	private void addNewNote(FinalListNote fln) {
		if (!finalNoteMap.containsKey(fln.position())){
			finalNoteMap.put(fln.position(), fln);
		}
		
	}
	
	private void addNewNote(double d) {
		if (!finalNoteMap.containsKey(d)){
			finalNoteMap.put(d, new FinalListNote(d));
		}
		
	}

}
