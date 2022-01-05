package repetition_analysis.feature_chunk;
import java.util.ArrayList;
import java.util.HashMap;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.combo_variables.IntAndDouble;
import ResourceUtils.ChordForm;
import repetition_analysis.feature_handler.FeatureHandler;
import repetition_analysis.feature_handler.FeatureInfo;
import repetition_analysis.feature_handler.Handler_AbsoluteDuration;
import repetition_analysis.feature_handler.Handler_AbsoluteTopNoteInterval;
import repetition_analysis.feature_handler.Handler_AbsoluteTopNotePitch;
import repetition_analysis.feature_handler.Handler_GapValue;
import repetition_analysis.feature_handler.Handler_HalfSimpleMelodyContour;
import repetition_analysis.feature_handler.Handler_LoudSoftDynamics;
import repetition_analysis.feature_handler.Handler_ModTopNotePitch;
import repetition_analysis.feature_handler.Handler_PolyAbsolutePitch;
import repetition_analysis.feature_handler.Handler_PolyModPitch;
import repetition_analysis.feature_handler.Handler_SimpleDynamics;
import repetition_analysis.feature_handler.Handler_SimpleMelodyContour;
import repetition_analysis.feature_handler.Handler_StaPorNuto;
import repetition_analysis.feature_handler.Handler_TopNoteChordToneScaleToneNonTone;
import repetition_analysis.feature_handler.Handler_TopNoteChromaticChordTone;
import repetition_analysis.feature_handler.Handler_TopNoteChromaticScaleDegree;
import repetition_analysis.feature_handler.Handler_TopNoteDiatonicChordFunctionTone;
import repetition_analysis.feature_handler.Handler_TopNoteDiatonicKeyScaleDegree;
import repetition_analysis.note_chunk.NoteChunk;

/*
 * contains a list of NoteChunks that occur sequentially in the chunkList of a project
 * 
 * these will be tested for repetition
 */
public class FeatureChunk {

	public ArrayList<NoteChunk> chunkList = new ArrayList<NoteChunk>();
	private double position = 0.0;
	private static final String separator ="_";
	private int keyCentre = 5;		// default for testing
	private static final FeatureInfo nullInfo = new FeatureInfo();
	private ChordForm cf;
	private boolean hasChordForm = false;
	private LiveClip lc = null;
	private int barNumberStart;
	private int barNumberEnd;
	private double barStartOffset;
	private double barEndOffset;
	private IntAndDouble barStartParam;
	private IntAndDouble barEndParam;
	

	public FeatureChunk(NoteChunk nc){
		chunkList.add(nc);
		lc = nc.lc;
	}
	public FeatureChunk(){
		
	}
	public FeatureChunk(double position, LiveClip lc) {
		this.position = position;
		this.lc = lc;
		setBarPositions();
	}
	private void setBarPositions() {
		double barLength = lc.signatureNumerator / lc.signatureDenominator * 4.0;		// 4.0 - to convert time signature to timeline lenth of bar
		//System.out.println("barLength=" + barLength);
		barNumberStart = (int)(position / barLength);
		barNumberEnd = barNumberStart + 1;
		barStartOffset = position % barLength;
		barEndOffset = barStartOffset - barLength;
		barStartParam = new IntAndDouble(barNumberStart, barStartOffset);
		barEndParam = new IntAndDouble(barNumberEnd, barEndOffset);
	}
	public void add(NoteChunk nc){
		chunkList.add(nc);
		if (lc == null) lc = nc.lc;
	}
	public int size(){
		return chunkList.size();
	}
	public void setKeyCentre(int keyCentre){
		this.keyCentre = keyCentre;
	}
	public int averageVelocity(){
		int total = 0;
		for (NoteChunk nc: chunkList){
			total += nc.averageVelocity();
		}
		return total / chunkList.size();
	}
	public void setChordForm(ChordForm cf){
		this.cf = cf;
		hasChordForm = true;
	}
	public ChordForm cf(){
		return cf;
	}
	public boolean hasChordForm(){
		return hasChordForm;
	}
	
// feature module thing -------------------------------------------------------
	
	public FeatureInfo getFeatureInfo(int featureID){
		if (handlerMap.containsKey(featureID)){
			return handlerMap.get(featureID).getInfo(this);
		} else {
			return nullInfo;
		}
	}

// feature string methods =====================================================
//	public String absoluteNoteNameString(){
//		String str = "";
//		for (NoteChunk nc: chunkList){
//			str += nc.absoluteNoteString() + separator;
//		}
//		return str;
//	}
//	public String modNoteNameString(){
//		String str = "";
//		for (NoteChunk nc: chunkList){
//			str += nc.modNoteString() + separator;
//		}
//		return str;
//	}
//	public String modNoteValueString(){
//		String str = "";
//		for (NoteChunk nc: chunkList){
//			str += nc.modNoteValueString() + separator;
//		}
//		return str;
//	}
//	public String absoluteTopNoteValueString(){
//		String str = "";
//		for (NoteChunk nc: chunkList){
//			str += nc.topNote() + separator;
//		}
//		return str;
//	}
//	public int[] absoluteTopNoteValueArr(){
//		int[] arr = new int[chunkList.size()];
	//	int index = 0;
//		for (NoteChunk nc: chunkList){
//			arr[index] += nc.topNote();
//			index++;
//		}
//		return arr;
//	}
//	public String gapValueString(){
//		String str = "";
//		for (double d: gapValueArray()){
//			str += d + separator;
//		}
//		return str;
//	}
//	public String durationValueString(){
//		String str = "";
//		for (double d: durationArray()){
//			str += d + separator;
//		}
//		return str;
//	}
//	public String smallLargePitchContourString(){
//		String str = "";
//		for (int d: smallLargePitchContourArray()){
//			str += d + separator;
//		}
//		return str;
//	}
//	public String noteRelativeToSingleKeyCentreToString(int keyCentre){
//		String str = "";
//		for (Integer[] d: noteRelativeToSingleKeyCentre(keyCentre)){
//			for (Integer i: d){
//				str += i + ",";
//			}
//			str += separator;
//		}
//		return str;
//	}

// feature value methods ----------------------------------------------------------
//	public double[] gapValueArray(){
//		double[] darr = new double[chunkList.size() - 1];
//		for (int i = 0; i < chunkList.size() - 1; i++){
//			darr[i] = chunkList.get(i + 1).position - chunkList.get(i).position;
//		}
//		return darr;
//	}
//	public double[] durationArray(){
//		double[] darr = new double[chunkList.size()];
//		for (int i = 0; i < chunkList.size(); i++){
//			darr[i] = chunkList.get(i).length;
//		}
//		return darr;
//	}
//	public int[] smallLargePitchContourArray(){
//		int[] darr = new int[chunkList.size() - 1];
//		for (int i = 0; i < chunkList.size() - 1; i++){
//			int d = chunkList.get(i + 1).topNote() - chunkList.get(i).topNote();
//			if (d == 0){
//				darr[i] = 0;
//			} else if (d > 0 && d < 6){
//				darr[i] = 1;				// small up interval
//			} else if (d > 5){
//				darr[i] = 2;				// big up interval
//			} else if (d < 0 && d > -6){
//				darr[i] = -1;				// small down interval
//			} else if (d < -5){
//				darr[i] = -2;				// big down interval
//			}
//		}
//		return darr;
//	}
//	public ArrayList<Integer[]> noteRelativeToSingleKeyCentre(int keyCentre){
//		//this is for a single key centre
//		ArrayList<Integer[]> kList = new ArrayList<Integer[]>();
//		keyCentre = keyCentre % 12;				// just in case
//		for (NoteChunk nc: chunkList){
//			kList.add(nc.singleKeyArray(keyCentre));
//		}
//		return kList;
//	}

// other info methods ===========================================================
	public double position(){
		return position;
	}
	public IntAndDouble getXOverBarPosParam(double xover){
		double d = xover * lc.getBarLengthInQuarters();
		if (barStartOffset <= d){
			return barStartParam;	// closer to start of bar
		} else {
			return barEndParam;
		}
	}
	public IntAndDouble getBarStartParam(){
		return barStartParam;
	}
	public IntAndDouble getBarEndParam(){
		return barEndParam;
	}
	public LiveClip lc() {
		return lc;
	}
	public String toString(){
		String str = "feature position=" + position + " barStart=" + barNumberStart + "/" + barStartOffset + " barEnd=" + barNumberEnd + "/" + barEndOffset + ": ";
		for (FeatureHandler fh: handlerMap.values()){
			str += "\t" + fh.shortToString(this);
		}
		return str;
	}
//	public String toString(){	// temp for debugging the diatonic chord tone
//		String str = "feature position=" + position + " barStart=" + barNumberStart + "/" + barStartOffset + " barEnd=" + barNumberEnd + "/" + barEndOffset + ": ";
//		str += "\n" + handlerMap.get(DIATONIC_CHORD_FUNCTION_TONE).shortToString(this);
//		return str;
//	}

	//	str += "\t" + absoluteNoteNameString();
//	str += "\t" + modNoteNameString();
//	str += "\t" + absoluteTopNoteValueString();
//	str += "\t" + modNoteValueString();
//	str += "\t" + gapValueString();
//	str += "\t" + durationValueString();
//	str += "\t" + smallLargePitchContourString();
//	str += "\t" + noteRelativeToSingleKeyCentreToString(keyCentre);

	public String toStringLarge(){
		String str = "feature position=" + position + ": ";
		str += "\nChunkList contents -------------------------------";
		for (NoteChunk nc: chunkList){
			str += "\n" + (nc.toString());
		}
		for (Integer i: handlerMap.keySet()){
			str += "\nfeatureHandler index=" + i + "\n" + handlerMap.get(i).getInfo(this).toString();
		}
		return str;
	}
//	public String toString(int keyCentre){
//		this.keyCentre = keyCentre;
//		return toString();
//	}
	public String getFeatureString(int featureOption) {
		if (handlerMap.containsKey(featureOption)){
			return handlerMap.get(featureOption).getInfo(this).getFeatureString();
		}
		
		return null;
	}
	public static String getFeatureName(int featureID){
		if (handlerMap.containsKey(featureID)){
			return handlerMap.get(featureID).name();
		}
		return null;
	}
	public static String getFeatureShortName(int featureID){
		if (handlerMap.containsKey(featureID)){
			return handlerMap.get(featureID).shortName();
		}
		return null;
	}
	public static HashMap<Integer, FeatureHandler> getHandlerMap(){
		return handlerMap;
	}
	
	
	
	
	public static final int ABSOLUTE_PITCH = 0;
	public static final int MOD_PITCH = 1;
	public static final int GAP_VALUE = 2;
	public static final int ABSOLUTE_DURATION = 3;
	public static final int ABSOLUTE_TOP_NOTE_PITCH = 4;
	public static final int LOUD_SOFT_DYNAMICS = 5;
	public static final int MOD_TOP_NOTE_PITCH = 6;
	public static final int ABSOLUTE_TOP_NOTE_INTERVAL = 7;
	public static final int CHROMATIC_SCALE_DEGREE = 8;
	public static final int CHROMATIC_CHORD_TONE = 9;
	public static final int DIATONIC_KEY_SCALE_TONE = 10;
	public static final int DIATONIC_CHORD_FUNCTION_TONE = 11;
	public static final int SIMPLE_MELODY_CONTOUR = 12;
	public static final int HALF_SIMPLE_MELODY_CONTOUR = 13;
	public static final int SIMPLE_DYNAMICS = 14;
	public static final int STACCATO_PORTATO_TENUTO = 15;
	public static final int CHORD_SCALE_NONSCALE_TONE = 16;
	public static final int POLY_ABSOLUTE_PITCH = 17;
	public static final int POLY_MOD_PITCH = 18;
	
	public static final int[] descriptionIndexArr = new int[]{
			GAP_VALUE,
			
//			ABSOLUTE_PITCH,
//			MOD_PITCH,
			ABSOLUTE_TOP_NOTE_PITCH,
			MOD_TOP_NOTE_PITCH,
			ABSOLUTE_TOP_NOTE_INTERVAL,
			CHROMATIC_SCALE_DEGREE,
			CHROMATIC_CHORD_TONE,
			DIATONIC_KEY_SCALE_TONE,
			DIATONIC_CHORD_FUNCTION_TONE,
			CHORD_SCALE_NONSCALE_TONE,
			
			ABSOLUTE_DURATION,
			STACCATO_PORTATO_TENUTO,
			
			LOUD_SOFT_DYNAMICS,
			SIMPLE_DYNAMICS,
			
			SIMPLE_MELODY_CONTOUR,
			HALF_SIMPLE_MELODY_CONTOUR
	};
	
	private static final HashMap<Integer, FeatureHandler> handlerMap = new HashMap<Integer,FeatureHandler>()
			{{
				put(ABSOLUTE_PITCH, new Handler_PolyAbsolutePitch());
				put(MOD_PITCH, new Handler_PolyModPitch());
				put(MOD_TOP_NOTE_PITCH, new Handler_ModTopNotePitch());
				put(ABSOLUTE_TOP_NOTE_INTERVAL, new Handler_AbsoluteTopNoteInterval());
				put(CHROMATIC_SCALE_DEGREE, new Handler_TopNoteChromaticScaleDegree());
				put(CHROMATIC_CHORD_TONE, new Handler_TopNoteChromaticChordTone());
				put(DIATONIC_KEY_SCALE_TONE, new Handler_TopNoteDiatonicKeyScaleDegree());
				put(DIATONIC_CHORD_FUNCTION_TONE, new Handler_TopNoteDiatonicChordFunctionTone());
				put(CHORD_SCALE_NONSCALE_TONE, new Handler_TopNoteChordToneScaleToneNonTone());
				put(SIMPLE_MELODY_CONTOUR, new Handler_SimpleMelodyContour());
				put(HALF_SIMPLE_MELODY_CONTOUR, new Handler_HalfSimpleMelodyContour());
				put(GAP_VALUE, new Handler_GapValue());
				put(ABSOLUTE_DURATION, new Handler_AbsoluteDuration());
				put(STACCATO_PORTATO_TENUTO, new Handler_StaPorNuto());
				put(ABSOLUTE_TOP_NOTE_PITCH, new Handler_AbsoluteTopNotePitch());
				put(LOUD_SOFT_DYNAMICS, new Handler_LoudSoftDynamics());
				put(SIMPLE_DYNAMICS, new Handler_SimpleDynamics());
				
			}};

	public static final int RHYTHM_TYPE = 0;
	public static final int PITCH_TYPE = 1;
	public static final int DURATION_TYPE = 2;
	public static final int DYNAMICS_TYPE = 3;
	public static final int CONTOUR_TYPE = 4;
	
	public static final String[] featureTypeName = new String[]{
			"rhythm", "pitch", "duration", "dynamics", "contour"
	};
	
	public static final int[][] featureTypeIndexGroup = new int[][]{
			new int[]{			// rhythm index = RHYTHM_TYPE
					GAP_VALUE
			},
			new int[]{			// pitch
//					ABSOLUTE_TOP_NOTE_PITCH,
//					MOD_TOP_NOTE_PITCH,
//					ABSOLUTE_TOP_NOTE_INTERVAL,
//					CHROMATIC_SCALE_DEGREE,
//					CHROMATIC_CHORD_TONE,
//					DIATONIC_KEY_SCALE_TONE,
//					DIATONIC_CHORD_FUNCTION_TONE,
					CHORD_SCALE_NONSCALE_TONE
			},
			new int[]{			// duration
					ABSOLUTE_DURATION,
					STACCATO_PORTATO_TENUTO,
			},
			new int[]{			// dynamics
					LOUD_SOFT_DYNAMICS,
					SIMPLE_DYNAMICS,
			},
			new int[]{			// contour index = CONTOUR_TYPE
					SIMPLE_MELODY_CONTOUR,
					HALF_SIMPLE_MELODY_CONTOUR
			}
	};
	
	
	
	



	
	
	
	
	
	

	
	
	
	

	

}
