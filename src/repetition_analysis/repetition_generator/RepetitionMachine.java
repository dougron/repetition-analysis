package repetition_analysis.repetition_generator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import DataObjects.ableton_live_clip.LiveClip;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.note_chunk.ChunkList;

public class RepetitionMachine {
	
	LiveClip lc;
	HashMap<String, FeatureChunkRepeatSchema> rlMap = new HashMap<String, FeatureChunkRepeatSchema>();
	private ChunkList chunkList;
	private Random rnd = new Random();

	public RepetitionMachine(){
		
	}
	public void setLiveClipForAnalysis(LiveClip lc, int sliceLength){
		this.lc = lc;
		makeAnalyses(lc);
	}
	public ArrayList<LiveClip> makeNewPieces(int count, int formCountPerPiece){
		ArrayList<LiveClip> clipList = new ArrayList<LiveClip>();
		
		return clipList;
	}
	private void makeAnalyses(LiveClip lc2) {
		rlMap.clear();
		makeChunkList();
		
	}
	private void makeChunkList() {
		chunkList = new ChunkList();
		chunkList.addClip(lc);
//		System.out.println(chunkList.toString());
//		makeChunkListTestFile();
	}
	
// toString methods --------------------------------------------------------------------
	public String toString(){
		String str = "";
		str += chunkList.toString();
		return str;
	}

}
