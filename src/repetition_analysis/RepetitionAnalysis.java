package repetition_analysis;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import DataObjects.ableton_live_clip.LiveClip;
import ResourceUtils.ChordForm;
import gui_objects.main_ui.RepetitionAnalysisGameFrame;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_handler.FeatureHandler;
import repetition_analysis.note_chunk.ChunkList;
import repetition_analysis.repeat_schema_list.RepeatSchemaList;

public class RepetitionAnalysis {

	private LiveClip lc;
	private int cellLength;
	private ChunkList chunkList;
	private ArrayList<FeatureChunk> featureChunkList;
	private String chunkFileName; 
	private String featureChunkFileName;
	private String featureRepeatFileName;
	private String oneLinerFileName;
	private RepeatSchemaList rsl;
	private ChordForm cf = null;		// ChordForm for analysis
	private HashMap<Integer, FeatureHandler> handlerMap = new HashMap<Integer, FeatureHandler>();
	private int[] cellLenArr;


	public RepetitionAnalysis(LiveClip lc, int cellLength) {
		init(lc, cellLength);
	}
	public RepetitionAnalysis(LiveClip lc, int cellLength, LiveClip chords){
		cf = new ChordForm(chords);
		init(lc, cellLength);
	}
	public RepetitionAnalysis(LiveClip lc, int[] cellLenArr) {
		init(lc, cellLenArr);
	}
	public RepetitionAnalysis(LiveClip lc, int[] cellLenArr, LiveClip chords){
		cf = new ChordForm(chords);
		init(lc, cellLenArr);
	}
	public RepetitionAnalysis(LiveClip lc, ArrayList<Integer> selectedIndexList) {
		int[] arr = integerListToIntArray(selectedIndexList);
		init(lc, arr);
	}
	
	public RepetitionAnalysis(LiveClip lc, ArrayList<Integer> selectedIndexList, LiveClip chords) {
		int[] arr = integerListToIntArray(selectedIndexList);
		cf = new ChordForm(chords);
		init(lc, arr);
	}
	
	private void init(LiveClip lc, int cellLength){
		this.lc = lc;
		this.cellLength = cellLength;
		setPathsFromPropertiesFile();
		makeHandlerMap();
		makeAnalysis(lc, cellLength);
	}
	private void init(LiveClip lc, int[] cellLenArr){
		this.lc = lc;
		this.cellLenArr = cellLenArr;
		setPathsFromPropertiesFile();
		makeHandlerMap();
		makeAnalysis(lc, cellLenArr);
	}
	private void setPathsFromPropertiesFile (){
		try (
				InputStream input 
//				= new FileInputStream("src/resources/repetition_analysis.properties")
				= new FileInputStream(RepetitionAnalysisGameFrame.propertiesPath)
					){
			Properties props = new Properties();
			props.load(input);
			String path = props.getProperty("chunks_and_features_path");
			chunkFileName = path + "chunkList.txt";
			featureChunkFileName = path + "featureList.txt";
			featureRepeatFileName = path + "featureRepeatList.txt";
			oneLinerFileName = path + "oneLinerList.txt";
		} catch (IOException ex){
			
		}	
	}
	private int[] integerListToIntArray(ArrayList<Integer> list) {
		int[] arr = new int[list.size()];
		int index = 0;
		Iterator<Integer> it = list.iterator();
		while (it.hasNext()){
			arr[index] = it.next();
			index++;
		}
		return arr;
	}
	private void makeHandlerMap() {
		for (Integer i: FeatureChunk.getHandlerMap().keySet()){
			FeatureHandler fh = FeatureChunk.getHandlerMap().get(i);
			try {
				FeatureHandler newfh = fh.getClass().newInstance();
				if (cf == null){
					if (!fh.needsAnalysisChordForm()){
						handlerMap.put(i, newfh);
					}
				} else {
					handlerMap.put(i, newfh);
				}
			} catch (Exception ex){
				
			}			
		}
		
	}
	public int[] getContentTimeSignature(){
		return new int[]{lc.signatureNumerator, lc.signatureDenominator};
	}
	public String contentTimeSignatureToString(){
		int[] ts = getContentTimeSignature();
		return "RepetitionAnalysis content clip timesignature: " + ts[0] + "/" + ts[1];
	}
	public String toString(){
		String str = "RepetitionAnalysis content name:" + lc.name;
		if (cf != null) str += "\nRepetitionAnalysis chords name:" + cf.name();
		str += "\n" + contentTimeSignatureToString();
		return str;
	}
	public HashMap<Integer, FeatureHandler> getHandlerMap(){
		return handlerMap;
	}
	public FeatureHandler getFeatureHandler(int key){	
		if (handlerMap.containsKey(key)){
			return handlerMap.get(key);
		} else {
			System.out.println("RepetitionAnalysis.getFeatureHandler attempting to get null FeatureHandler from handlerMap");
			return null;
		}
	}
	public void makeFinalNoteList(){
//		chooseNewRepetitionSchemaCombos();
//		generateFinalNoteList();
	}
	public RepeatSchemaList getSchemaList(){
		return rsl;
	}
	public ChunkList getChunkList(){
		return chunkList;
	}
	public FeatureChunk getFeatureChunk(int index){
		if (index < featureChunkList.size()){
			return featureChunkList.get(index);
		} else {
			return null;
		}
	}
	public ArrayList<FeatureChunk> getFeatureChunkList(){
		return featureChunkList;
	}
	public String chordFormToString(){
		if (cf == null){
			return "no chordForm for analysis";
		} else {
			return cf.toString();
		}	
	}
	public FeatureChunkRepeatSchema getRepeatSchema(int index){
		return rsl.get(index);
	}

	private void makeAnalysis(LiveClip lc2, int cellLength2) {
		makeChunkList();
		makeFeatureChunkList(cellLength2);
		makeRepeatSchemaList(featureChunkList);
	}
	private void makeAnalysis(LiveClip lc2, int[] cellLenArr) {
		makeChunkList();
		makeFeatureChunkList(cellLenArr);
		makeRepeatSchemaList(featureChunkList);
	}
	
	private void makeRepeatSchemaList(ArrayList<FeatureChunk> featureChunkList2) {
		this.rsl = new RepeatSchemaList(featureChunkList2, this);
		
		makeRepeatSchemaListTextFile();
	}

	private void makeRepeatSchemaListTextFile() {
		try {
			FileWriter fw = new FileWriter(featureRepeatFileName);
			BufferedWriter bw = new BufferedWriter(fw);
//			String str = "";
			rsl.sortList();
//			for (SchemaListObject slo: rsl.schemaList){
//				str += slo.toString() + "\n";
//			}
			//System.out.println(rsl.toString());
			bw.write(rsl.toString());
			bw.close();
			fw.close();
		} catch (IOException ex){
			
		}
	}

	private void makeFeatureChunkList(int chunkLength) {
		if (cf == null){
			featureChunkList = chunkList.getFeatureChunkList(chunkLength);
		} else {
			featureChunkList = chunkList.getFeatureChunkList(chunkLength, cf);
		}
		
		makeFeatureChunkListTextFile();
	}
	private void makeFeatureChunkList(int[] chunkLenArr) {
		if (cf == null){
			featureChunkList = chunkList.getFeatureChunkList(chunkLenArr);
		} else {
			featureChunkList = chunkList.getFeatureChunkList(chunkLenArr, cf);
		}
		
		makeFeatureChunkListTextFile();
	}
	private void makeFeatureChunkListTextFile() {
		try {
			FileWriter fw = new FileWriter(featureChunkFileName);
			BufferedWriter bw = new BufferedWriter(fw);
			String str = "";
			// the short version
//			for (FeatureChunk fc: featureChunkList){
//				str += fc.toString() + "\n";
//			}
			// the long version
			for (FeatureChunk fc: featureChunkList){
				str += fc.toStringLarge() + "\n";
			}
			//System.out.println(str);
			bw.write(str);
			bw.close();
			fw.close();
		} catch (IOException ex){
			
		}
		
	}
	private void makeChunkList() {
		chunkList = new ChunkList();
		chunkList.addClip(lc);
		//System.out.println(chunkList.toString());
		makeChunkListTextFile();
	}
	private void makeChunkListTextFile() {
		try {
			FileWriter fw = new FileWriter(chunkFileName);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(chunkList.toString());
			bw.close();
			fw.close();
		} catch (IOException ex){
			
		}
		
	}
	public boolean isSameTimeSignature(int[] ts) {
		if (lc.signatureNumerator == ts[0] && lc.signatureDenominator == ts[1]){
			return true;
		} else {
			return false;
		}
		
	}
	public boolean hasChordsClip(){		// ###########################
		if (cf == null){
			return false;
		} else {
			return true;
		}
	}
	public String contentClipName(){
		return lc.name;
	}
	public String chordsClipName(){
		if (cf == null){
			return "no chords clip";
		} else {
			return cf.name();
		}
	}
	public ChordForm cf(){
		// this is the chords to go along with the content, as might not be clear....
		return cf;
	}
	
	
}
