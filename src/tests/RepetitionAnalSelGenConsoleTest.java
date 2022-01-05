package tests;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import ChordScaleDictionary.ChordScaleDictionary;
import DataObjects.ableton_live_clip.LiveClip;
import acm.program.ConsoleProgram;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchemaSortWrapper;
import repetition_analysis.repeat_schema_list.FCRSSortWrapperList;
import repetition_analysis.repeat_schema_list.SchemaListObject;

public class RepetitionAnalSelGenConsoleTest extends ConsoleProgram {

//	private String path = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ShortTest.liveclip";
//	private String chordspath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ShortTest.chords.liveclip";

	private String path = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/CorpusMelodyFiles/StraightNoChaser.liveclip";
	private String chordspath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ChordProgressionTestFiles/FJazzBlues.chords.liveclip";

//	private ChordScaleDictionary csd = new ChordScaleDictionary();
	
	public void run(){
		setSize(1500, 900);
		LiveClip lc = getLiveClip();
		LiveClip chords = getChordsClip();
		println(lc.toString());
//		int cellLength = 3;
//		RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLength, chords);
		
		int[] cellLenArr = new int[]{3, 4, 5};
		RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLenArr, chords);
		
		println(ra.toString());
		
//		sortingSchemaListTests(ra);			/
		
		
		//		int[] selectionArray =  new int[]{
//				RepetitionAnalysis.GAP_FEATURE, 
//				RepetitionAnalysis.GAP_FEATURE,
//				RepetitionAnalysis.NOTE_FEATURE,
//				RepetitionAnalysis.NOTE_FEATURE,
//				};
//		RepetitionSelection rs = new RepetitionSelection(ra, selectionArray);
//		RepetitionGenerator rg = new RepetitionGenerator(rs);
//		int outputClipCount = 5;
//		ArrayList<LiveClip> clipList = rg.getOutputClips(outputClipCount);
	}
	
	



	private void sortingSchemaListTests(RepetitionAnalysis ra) {
		// these relate to the implementation of the JTree to choose master repeat schemas 
		println("schemaListObjectToShortStringTest=========================================");
		schemaListObjectToShortStringTest(ra);
		println("FeatureChunkRFeapeatSchemaSortWrapperTest==================================");
		featureChunkRepeatSchemaSortWrapperTest(ra, 0.0, 16.0);
	}





	private void featureChunkRepeatSchemaSortWrapperTest(RepetitionAnalysis ra, double start, double end) {
		ArrayList<FeatureChunkRepeatSchemaSortWrapper> sList = ra.getSchemaList().makePositionConstrainedSchemaList(start, end);
		sizeAndFirstPosListTest(sList);
//		posMapTest(sList, ra);
		gapMapTest(sList, ra);
	}





	private void gapMapTest(ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList, RepetitionAnalysis ra) {
		ArrayList<FCRSSortWrapperList> swlList = ra.getSchemaList().getGapSortWrapperList(swList);
		
		String keyList = "";
		for (FCRSSortWrapperList swl: swlList){
			
			println(swl.keyArrayToString() + " ------------------------------------------");
			Iterator<FeatureChunkRepeatSchemaSortWrapper> swit = swl.iterator();
			while (swit.hasNext()){
				println("   " + swit.next().toString());
			}
			keyList += swl.keyArrayToString() + "\n";
		}
		println(keyList);
		println(swlList.size() + " unique gap items");
		
	}





	private void posMapTest(ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList, RepetitionAnalysis ra) {
//		TreeMap<String, FCRSSortWrapperList> posMap = ra.getSchemaList().getPosMap(swList);
		ArrayList<FCRSSortWrapperList> swlList = ra.getSchemaList().getPosSortWrapperList(swList, 2, 5);
		Collections.sort(swlList, FCRSSortWrapperList.featureLengthAndPosComparator);
		String keyList = "";
		for (FCRSSortWrapperList swl: swlList){
			
			println(swl.keyArrayToString() + " ------------------------------------------");
			Iterator<FeatureChunkRepeatSchemaSortWrapper> swit = swl.iterator();
			while (swit.hasNext()){
				println("   " + swit.next().toString());
			}
			keyList += swl.keyArrayToString() + "\n";
		}
		println(keyList);
		println(swlList.size() + " unique pos items");
		
	}





	private ArrayList<FeatureChunkRepeatSchemaSortWrapper> sizeAndFirstPosListTest(ArrayList<FeatureChunkRepeatSchemaSortWrapper> sList) {		
		Collections.sort(sList, FeatureChunkRepeatSchemaSortWrapper.sizeAndFirstPosComparator);	
		for (FeatureChunkRepeatSchemaSortWrapper sw: sList){
			println(sw.toString());
		}
		return sList;
	}
	private TreeMap<String, ArrayList<FeatureChunkRepeatSchemaSortWrapper>> makeGapMap(
			ArrayList<FeatureChunkRepeatSchemaSortWrapper> sList) {
		TreeMap<String, ArrayList<FeatureChunkRepeatSchemaSortWrapper>> map = new TreeMap<String, ArrayList<FeatureChunkRepeatSchemaSortWrapper>>();
		for (FeatureChunkRepeatSchemaSortWrapper sw: sList){
			if (!map.containsKey(sw.gapArrayToString())){
				map.put(sw.gapArrayToString(), new ArrayList<FeatureChunkRepeatSchemaSortWrapper>());
			}
			map.get(sw.gapArrayToString()).add(sw);
		}
		
		return map;
	}





	private TreeMap<String, ArrayList<FeatureChunkRepeatSchemaSortWrapper>> makePosMap(
			ArrayList<FeatureChunkRepeatSchemaSortWrapper> sList) {
		TreeMap<String, ArrayList<FeatureChunkRepeatSchemaSortWrapper>> map = new TreeMap<String, ArrayList<FeatureChunkRepeatSchemaSortWrapper>>();
		for (FeatureChunkRepeatSchemaSortWrapper sw: sList){
			if (!map.containsKey(sw.posArrayToString())){
				map.put(sw.posArrayToString(), new ArrayList<FeatureChunkRepeatSchemaSortWrapper>());
			}
			map.get(sw.posArrayToString()).add(sw);
		}
		
		return map;
	}





	private void schemaListObjectToShortStringTest(RepetitionAnalysis ra) {
		for (SchemaListObject slo: ra.getSchemaList().schemaList){
			println(slo.toShortString(0.0, 16.0));
//			if (slo instanceof FeatureChunkRepeatSchema){
//				println(((FeatureChunkRepeatSchema) slo).shortToString());
//			}
			
		}
		
	}





	// privates ----------------------------------------------------------------------------	
	private LiveClip getChordsClip() {
		LiveClip lc = new LiveClip(0, 0);
		try {
			lc.instantiateClipFromBufferedReader(new BufferedReader(new FileReader(chordspath)));			
		} catch (Exception ex){
			
		}
		return lc;
	}
	private LiveClip getLiveClip() {
		LiveClip lc = new LiveClip(0, 0);
		try {
			lc.instantiateClipFromBufferedReader(new BufferedReader(new FileReader(path)));
//			BufferedReader in = new BufferedReader(new FileReader(path));
//			while(true){
//				String str = in.readLine();
//				if (str == null) break;
//				println(str);
//				String[] strArr = str.split(",");
//				if (strArr[0].equals("LiveMidiNote")){
//					int note = Integer.valueOf(strArr[1]);
//					double pos = Double.valueOf(strArr[4]);
//					double len = Double.valueOf(strArr[5]);
//					int vel = Integer.valueOf(strArr[6]);
//					lc.addNote(note, pos, len, vel, 0);
//				} else if (strArr[0].equals("length")){
//					lc.length = Double.valueOf(strArr[1]);
//				} else if (strArr[0].equals("loopStart")){
//					lc.loopStart = Double.valueOf(strArr[1]);
//				} else if (strArr[0].equals("loopEnd")){
//					lc.loopEnd = Double.valueOf(strArr[1]);
//				} else if (strArr[0].equals("startMarker")){
//					lc.startMarker = Double.valueOf(strArr[1]);
//				} else if (strArr[0].equals("endMarker")){
//					lc.endMarker = Double.valueOf(strArr[1]);
//				} else if (strArr[0].equals("signatureNumerator")){
//					lc.signatureNumerator = Integer.valueOf(strArr[1]);
//				} else if (strArr[0].equals("signatureDenominator")){
//					lc.signatureDenominator = Integer.valueOf(strArr[1]);
//				} else if (strArr[0].equals("name")){
//					lc.name = strArr[1];
//				}
//				
//				
//			}
			
		} catch (Exception ex){
			
		}
		return lc;
	}
}
