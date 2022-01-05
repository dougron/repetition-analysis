package repetition_analysis.feature_chunk;
/*
 * this class contains a list of FeatureChunkWrapper instances all of the same 
 * FeatureChunkWrapper.featureOption, which here is named featureID
 * 
 * new feature is the ability to return a series of indices which describe the repeat (or not)
 * of non primary features
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import DataObjects.combo_variables.IntAndDouble;
import repetition_analysis.index_list.IndexList;
import repetition_analysis.note_chunk.NoteChunk;
import repetition_analysis.repeat_schema_list.RepeatSchemaList;
import repetition_analysis.repeat_schema_list.SchemaListObject;

public class FeatureChunkRepeatSchema implements SchemaListObject{

	private ArrayList<FeatureChunkWrapper> featureChunkList = new ArrayList<FeatureChunkWrapper>();
	private String featureString;
	private String name;
	private int featureID;
	private SchemaListObject nextSchema;
	private TreeMap<Integer, IndexList> secondaryIndexMap = new TreeMap<Integer, IndexList>();
	private ArrayList<IndexList> secondaryIndexList = new ArrayList<IndexList>();
	private RepeatSchemaList rsl;
	private LiveClip lc = null;	//this is the original content clip from the RepetitionAnalysis
	private String shortName;
	private static int idCounter = 0;
	private int uniqueID;
	
	
	public FeatureChunkRepeatSchema(FeatureChunkWrapper fcr, SchemaListObject nextSchema, RepeatSchemaList rsl){
		this.rsl = rsl;
		this.nextSchema = nextSchema;
		featureChunkList.add(fcr);
		featureString = fcr.featureString();
		featureID = fcr.featureID();
		name = fcr.name();
		shortName = fcr.shortName();
		uniqueID = idCounter;
		idCounter++;
	}
	public FeatureChunkRepeatSchema(){
		featureID = -1;
		featureString = "null FeatureChunkRepeatSchema";
		name = featureString;
		uniqueID = idCounter;
		idCounter++;
	}
//	public FeatureChunkRepeatSchema(){
//		// for extending SchemaListEnd
//	}
	public int featureID(){
		return featureID;
	}
	public int uniqueID(){
		return uniqueID;
	}
	public void add(FeatureChunkWrapper fcw){
		//System.out.println(fcw.toString() + " arrived at " + this.toString());
		if (fcw.featureID() == featureID && fcw.featureString().equals(featureString)){
			//System.out.println("... and added");
			featureChunkList.add(fcw);
			makeSecondaryIndexMap();
			if (lc == null) lc = fcw.lc();
		} else {
			//System.out.println("... and passed on");
			nextSchema.add(fcw);
		}
		
	}
	
	public double[] gapArray(){
		// the gaps between each repetition
		double[] arr = new double[featureChunkList.size() - 1];
		for (int i = 0; i < arr.length; i++){
			arr[i] = featureChunkList.get(i + 1).featureChunk().position() - featureChunkList.get(i).featureChunk().position();
		}
		return arr;
	}
	public double[] gapArray(double start, double end){
		// the gaps between each repetition for a limited position range
		
		double[] arr = new double[featureChunkList.size() - 1];
		for (int i = 0; i < arr.length; i++){
			arr[i] = featureChunkList.get(i + 1).featureChunk().position() - featureChunkList.get(i).featureChunk().position();
		}
		return arr;
	}
////	public double[] gapValueArray(){
//		// the gaps between notes if this is a GAP_VALUE feature......
//		if (featureID == GAP_VALUE && featureChunkList.size() > 0){
//			return featureChunkList.get(0).featureChunk().gapValueArray();		// 0 - if featureID == GAP_VALUE then all FeatureChunks should have the same gapValueArray, so choose the first item
//		} else {
//			return new double[0];
//		}
//	}
	
	public double[] posArray(){
		// array of positions
		double[] arr = new double[featureChunkList.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = featureChunkList.get(i).position();
		}
		
		return arr;
	}
	public IntAndDouble[] barXOverPosArray(double xover){
		IntAndDouble[] arr = new IntAndDouble[featureChunkList.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = featureChunkList.get(i).getXOverBarPosParam(xover);
		}
		
		return arr;
	}
	public IntAndDouble[] barStartPosArray(){
		IntAndDouble[] arr = new IntAndDouble[featureChunkList.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = featureChunkList.get(i).getBarStartParam();
		}
		
		return arr;
	}
	public IntAndDouble[] barEndPosArray(){
		IntAndDouble[] arr = new IntAndDouble[featureChunkList.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = featureChunkList.get(i).getBarEndParam();
		}
		
		return arr;
	}
	public String toString(){
		String str = shortWithNotesToString() + "\n" + secondaryIndexListToString();
		return str;
	}
	@Override
	public String toShortString() {
		String str = oneLineToString();
		return str;
	}
	public String toShortString(double start, double end) {
		String str = oneLineToString(start, end);
		return str;
	}
	
	public String indexListToString(int index){
		return secondaryIndexList.get(index).listToString();
	}
	public String barXOverPosArrayToString(double xover){
		String str = "";
		for (IntAndDouble iad: barXOverPosArray(xover)){
			str += "(" + iad.i + "," + iad.d + ")";
		}
		return str;
	}
	private String oneLineToString() {
		//String str = shortName + ": " + featureString + " " + " repeats(" + count() + "): ";
		String str = shortName + ": ";
		str += repeatsToString();
		str += " gaps: ";
		for (double d: gapArray()){
			str += d + ",";
		}
		return str;
	}
	private String oneLineToString(double start, double end) {
		// for constraining the repeat points to a range
		//String str = shortName + ": " + featureString + " " + " repeats(" + count() + "): ";
		ConstrainedFeatureChunkList cfcl = new ConstrainedFeatureChunkList(featureChunkList, start, end);
		String str = shortName + ": ";
		str += cfcl.repeatsToString();
		str += " gaps: ";
		str += cfcl.gapsToString();
		
		return str;
	}
	public String nameAndRepeatCountToString(){
		String str = name + ": " + featureString + " (" + count() + ")";
		return str;
	}
	public String nameAndFeatureStringToString(){
		String str = featureShortName() + ": " + featureString;
		return str;
	}
	public String nameFeatureStringAndRepeatCountToString(){
		String str = nameAndFeatureStringToString() + " (" + count() + ")";
		return str;
	}
	public String repeatsToString(){
		return positionsToString();
	}
	public String repeatsToString(double start, double end){
		return positionsToString(start, end);
	}
	public String shortToString(){
		String str = name + ": " + featureString + " " + "\nrepeats(" + count() + "): ";
		str += positionsToString();
		str += "\ngaps: ";
		for (double d: gapArray()){
			str += d + ",";
		}
		return str;
	}

	public String shortWithNotesToString(){
		String str = name + ": " + featureString + " " + "\nrepeats(" + count() + "): ";
		str += positionsToString();
		str += "\ngaps: ";
		for (double d: gapArray()){
			str += d + ",";
		}
		
		for (FeatureChunkWrapper fc: featureChunkList){
			str += "\n   featureChunk at " + fc.position() + " ";
			for (NoteChunk nc: fc.featureChunk().chunkList){
				str += nc.bracketedNoteData() + ",";
			}
		}
		return str;
	}
	public String featureShortName(){
		return FeatureChunk.getFeatureShortName(featureID);
	}
	public String featureString(){
		return featureString;
	}
	public String positionsToString(){
		String str = "";
		for (FeatureChunkWrapper fcw: featureChunkList){
			str += fcw.featureChunk().position() + ",";
		}
		return str;
	}
	public String positionsToString(double start, double end){
		String str = "";
		for (FeatureChunkWrapper fcw: featureChunkList){
			if (fcw.position() >= start && fcw.position() < end){
				str += fcw.featureChunk().position() + ",";
			}
			
		}
		return str;
	}
	

	public int size(){
		return featureChunkList.size();
	}
	@Override
	public int count() {
		return featureChunkList.size();
	}
	public void makeSecondaryIndexMap(){
		secondaryIndexMap.clear();
		secondaryIndexList.clear();
		HashMap<String, Integer> strMap = new HashMap<String, Integer>();
		for (int i: rsl.parent.getHandlerMap().keySet()){
			strMap.clear();
			IndexList iList = new IndexList(i);
			int index = 0;
			for (FeatureChunkWrapper fcw: featureChunkList){
				String str = fcw.featureString(i);
				if (strMap.containsKey(str)){
					iList.add(strMap.get(str));
				} else {
					strMap.put(str, index);
					iList.add(index);
					index++;
				}
			}
			secondaryIndexMap.put(i, iList);
			secondaryIndexList.add(iList);
			Collections.sort(secondaryIndexList, IndexList.varietyComparator);			
		}
	}
	public FeatureChunkWrapper getPrimaryFeatureChunk(){
		if (featureChunkList.size() > 0){
			return featureChunkList.get(0);
		} else {
			return null;
		}
	}
	public IndexList getIndexList(int featID){
		if (secondaryIndexMap.containsKey(featID)){
			return secondaryIndexMap.get(featID);
		} else {
			return null;
		}
	}
	public ArrayList<FeatureChunkWrapper> featureChunkList() {
		
		return featureChunkList;
	}
	public ArrayList<String> secondaryIndexListAsListOfStrings(){
		ArrayList<String> list = new ArrayList<String>();
		for (IndexList il: secondaryIndexList){
			list.add(il.listToString());
		}
		return list;
	}
// privates ------------------------------------------------------------------------------------------
//	private ConstrainedFeatureChunkList constrainedFeatureChunkList(double start, double end){
//		
//	}
	private String secondaryIndexMapToString() {
		String str = "";
		for (int i: secondaryIndexMap.keySet()){
			IndexList iList = secondaryIndexMap.get(i);
			str += "  " + rsl.parent.getHandlerMap().get(i) + ": " + iList.listToString() + "\n";

		}
		return str;
	}
	private String secondaryIndexListToString() {
		String str = "";
		// for list sorted by index - or just use secondaryIndexMapToString
//		for (int index: secondaryIndexMap.keySet()){
//			IndexList iList = secondaryIndexMap.get(index);
//			str += "  index=" + iList.featureID() + " " + FeatureChunk.handlerMap.get(iList.featureID()) + "(variety=" + iList.variety()+ ",setSize=" + iList.setSize() + ",size=" + iList.size();
//			str += "):" + iList.listToString() + "\n";
//		}
		// list sorted by variety
		for (IndexList iList: secondaryIndexList){
			str += "  index=" + iList.featureID() + " " + rsl.parent.getHandlerMap().get(iList.featureID()) + "(variety=" + iList.variety()+ ",setSize=" + iList.setSize() + ",size=" + iList.size();
			str += "):" + iList.listToString() + "\n";
		}
		return str;
	}
// comparators ---------------------------------------------------------------------------------------
	public static Comparator<FeatureChunkRepeatSchema> repCountComparator = new Comparator<FeatureChunkRepeatSchema>(){
		public int compare(FeatureChunkRepeatSchema note1, FeatureChunkRepeatSchema note2){
			if (note1.size() < note2.size()) return 1;
			if (note1.size() > note2.size()) return -1;
			return 0;
		}
	};
	
	public static Comparator<FeatureChunkRepeatSchema> featureIDComparator = new Comparator<FeatureChunkRepeatSchema>(){
		public int compare(FeatureChunkRepeatSchema note1, FeatureChunkRepeatSchema note2){
			if (note1.featureID() < note2.featureID()) return 1;
			if (note1.featureID() > note2.featureID()) return -1;
			return 0;
		}
	};
	
	public static Comparator<FeatureChunkRepeatSchema> featureIDAndArrayComparator = new Comparator<FeatureChunkRepeatSchema>(){
		public int compare(FeatureChunkRepeatSchema note1, FeatureChunkRepeatSchema note2){
			if (note1.featureID() < note2.featureID()) return 1;
			if (note1.featureID() > note2.featureID()) return -1;
			FeatureChunkWrapper fcw1 = note1.getPrimaryFeatureChunk();
			FeatureChunkWrapper fcw2 = note2.getPrimaryFeatureChunk();
			
			return fcw1.getFeatureInfo().beforeOrAfter(fcw2.getFeatureInfo());
		}
	};
	public static Comparator<FeatureChunkRepeatSchema> featureIDAndLastToFirstArrayComparator = new Comparator<FeatureChunkRepeatSchema>(){
		public int compare(FeatureChunkRepeatSchema note1, FeatureChunkRepeatSchema note2){
			if (note1.featureID() < note2.featureID()) return 1;
			if (note1.featureID() > note2.featureID()) return -1;
			FeatureChunkWrapper fcw1 = note1.getPrimaryFeatureChunk();
			FeatureChunkWrapper fcw2 = note2.getPrimaryFeatureChunk();
			
			return fcw1.getFeatureInfo().beforeOrAfterLastToFirst(fcw2.getFeatureInfo());
		}
	};


	public double length() {
		if (lc == null){
			return 0.0;
		} else {
			return lc.length;
		}
	}
	public LiveClip lc() {
		return lc;
	}
	public int barCount(){
		return lc.barCount();
	}
	public ArrayList<IndexList> secondaryIndexList(){
		return secondaryIndexList;
	}
	public Object nameAndPosArrayToString() {
		String str = shortName + ": ";
		str += repeatsToString();
		
		return str;
	}



	
// statics ---------------------------------------------------------------------------------------------	
//	public static final int ABSOLUTE_NOTE_NAME = 0;
//	public static final int MOD_NOTE_NAME = 1;
//	public static final int ABSOLUTE_TOP_NOTE_VALUE = 2;
//	public static final int MOD_NOTE_VALUE = 3;
//	public static final int GAP_VALUE = 4;
//	public static final int DURATION_VALUE = 5;
//	public static final int SMALL_LARGE_PITCH_CONTOUR = 6;
//	public static final int SINGLE_KEY_CENTRE_NOTE_VALUE = 7;
//	
//	public static final int[] featureOptionArr = new int[]{
//			 ABSOLUTE_NOTE_NAME,
//			 MOD_NOTE_NAME,
//			 ABSOLUTE_TOP_NOTE_VALUE,
//			 MOD_NOTE_VALUE,
//			 GAP_VALUE,
//			 DURATION_VALUE,
//			 SMALL_LARGE_PITCH_CONTOUR,
//			 SINGLE_KEY_CENTRE_NOTE_VALUE,
//	};
//	public static final String[] featureOptionNameArr = new String[]{
//			 "ABSOLUTE_NOTE_NAME",
	//		 "MOD_NOTE_NAME",
//			 "ABSOLUTE_TOP_NOTE_VALUE",
//			 "MOD_NOTE_VALUE",
//			 "GAP_VALUE",
//			 "DURATION_VALUE",
//			 "SMALL_LARGE_PITCH_CONTOUR",
//			 "SINGLE_KEY_CENTRE_NOTE_VALUE",
//	};






}
