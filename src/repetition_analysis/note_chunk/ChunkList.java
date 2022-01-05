package repetition_analysis.note_chunk;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.ListModel;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import ResourceUtils.ChordForm;
import repetition_analysis.feature_chunk.FeatureChunk;

/*
 * accepts new LiveMidiNotes which get assigned to a NoteChunk(implements NoteChunkListObject) 
 * with all the same position
 */

public class ChunkList {

	public ArrayList<NoteChunkListObject> chunkList = new ArrayList<NoteChunkListObject>();
	public ArrayList<NoteChunkListObject> repeatChunkList = new ArrayList<NoteChunkListObject>();
	private LiveClip lc;
	private NoteChunkListObject firstListObject = null;
	
	public ChunkList(){
		ListEnd le = new ListEnd(chunkList, this);
		chunkList.add(le);
		firstListObject = le;
	}
	public void addNote(LiveMidiNote lmn, LiveClip lc){
		firstListObject.input(lmn, lc);
		this.lc = lc;
	}
	public void addClip(LiveClip lc){
		for (int i = lc.noteList.size() - 1; i > -1; i--){
			addNote(lc.noteList.get(i), lc);
		}
		sortList();
	}
	public void sortList() {
		Collections.sort(chunkList, NoteChunk.posAndLenComparator);
		makeRepeatChunkList();
		
//		setListEndRepeatFirstItem();

		
		makeSequentialNextItems();
	}
	private void makeRepeatChunkList() {
		for (NoteChunkListObject nclo: chunkList){
			if (nclo instanceof NoteChunk){
				NoteChunk nc = ((NoteChunk) nclo).clone();
				nc.position += lc.loopEnd;
				repeatChunkList.add(nc);
			}
		}
		if (repeatChunkList.size() > 0){
			repeatChunkList.get(0).setPreviousNoteChunk((NoteChunk)chunkList.get(chunkList.size() - 2));
			for (int i = 1; i < repeatChunkList.size(); i++){
				repeatChunkList.get(i).setPreviousNoteChunk((NoteChunk)repeatChunkList.get(i - 1));
			}
			for (int i = 0; i < repeatChunkList.size() - 1; i++){
				repeatChunkList.get(i).setNextNoteChunk((NoteChunk)repeatChunkList.get(i + 1));
			}
		}
		
		
	}
	public void setfirstListObjectFromChunkList(NoteChunkListObject nclo){
		firstListObject = nclo;
	}
	public NoteChunkListObject firstListObject(){
		return firstListObject;
	}
	private void setListEndRepeatFirstItem() {
		ListEnd le = (ListEnd)chunkList.get(chunkList.size() - 1);
		if (repeatChunkList.size() > 0){
			NoteChunkListObject nclo = repeatChunkList.get(0);
			NoteChunk nc = (NoteChunk)nclo;
			le.setBeginningOfRepeatFormChunk(nc.endOfFormClone());
		}
		
		
	}
	private void makeSequentialNextItems() {
		if (chunkList.size() > 1){
			for (int i = 0; i < chunkList.size() - 1; i++){
				NoteChunkListObject nclo = chunkList.get(i + 1);
				if (nclo instanceof ListEnd){
					//ListEnd le = (ListEnd)nclo;
					chunkList.get(i).setNextNoteChunk((NoteChunk) repeatChunkList.get(0));
				} else {
					NoteChunk nc = (NoteChunk)nclo;
					chunkList.get(i).setNextNoteChunk(nc);
				}
				
				if (i == 0){
					chunkList.get(i).setPreviousNoteChunk(chunkList.get(chunkList.size() - 2).beginningOfFormClone());
				} else {
					chunkList.get(i).setPreviousNoteChunk((NoteChunk)chunkList.get(i - 1));
				}
			}
		}
		
		
	}
	public String toString(){
		String str = "ChunkList:-\n";
		str += " firstListObject:- " + firstListObject.name() + "\n";
		str += "========= chunkList ==================\n";
		for (NoteChunkListObject nclo: chunkList){
			str += "--------------------------\n";
			str += nclo.toString() + "\n";
		}
		str += "========= repeatChunkList ==================\n";
		for (NoteChunkListObject nclo: repeatChunkList){
			str += "--------------------------\n";
			str += nclo.toString() + "\n";
		}
		return str;
	}
	public ArrayList<FeatureChunk> getFeatureChunkList(int chunkLength) {
		ArrayList<FeatureChunk> fcList = new ArrayList<FeatureChunk>();
		for (int i = 0; i < chunkList.size() - 1; i++){		// problem with this -1 dont need end item
			FeatureChunk fc = new FeatureChunk(chunkList.get(i).positionInForm(), lc);
			NoteChunk nc = (NoteChunk)chunkList.get(i);
			//System.out.println("==== start FeatureChunk at " + nc.position);
			for (int j = 0; j < chunkLength; j++){
				fc.add(nc);
				//System.out.println(nc.name() + " at pos=" + nc.position);
				nc = nc.next();
			}
			fcList.add(fc);
		}
		return fcList;
	}
	public ArrayList<FeatureChunk> getFeatureChunkList(int chunkLength, ChordForm cf){
		ArrayList<FeatureChunk> fcList = getFeatureChunkList(chunkLength);
		for (FeatureChunk fc: fcList){
			fc.setChordForm(cf);
		}
		return fcList;
	}
	public ArrayList<FeatureChunk> getFeatureChunkList(int[] is, ChordForm cf) {
		ArrayList<FeatureChunk> finalFCList = new ArrayList<FeatureChunk>();
		for (int i: is){
			finalFCList.addAll(getFeatureChunkList(i, cf));
		}
		return finalFCList;
	}
	public ArrayList<FeatureChunk> getFeatureChunkList(int[] is) {
		ArrayList<FeatureChunk> finalFCList = new ArrayList<FeatureChunk>();
		for (int i: is){
			finalFCList.addAll(getFeatureChunkList(i));
		}
		return finalFCList;
	}
//	public HashMap<String, FeatureChunkRepeatSchema> getRepeatListMap(int chunkLength){
//		HashMap<String, FeatureChunkRepeatSchema> fcMap = new HashMap<String, FeatureChunkRepeatSchema>();
//		for (FeatureChunk fc: getFeatureChunkList(chunkLength)){
//			String fcStr = fc.absoluteNoteNameString();
//			if (fcMap.containsKey(fcStr)){
//				fcMap.get(fcStr).add(fc);
//			} else {
//				FeatureChunkRepeatSchema fcrl = new FeatureChunkRepeatSchema(fcStr, "absoluteNoteName");
//				fcrl.add(fc);
//				fcMap.put(fcStr, fcrl);
//			}
//		}
//		return fcMap;
	public String[] nameArray() {
		ArrayList<String> list = new ArrayList<String>();
		for (NoteChunkListObject nclo: chunkList){
			if (nclo instanceof NoteChunk){
				list.add(nclo.chunkNameToString());
			}
		}
		return list.toArray(new String[list.size()]);
	}
	public NoteChunk getNoteChunk(int index){
		if (index > chunkList.size() - 2){
			return (NoteChunk)chunkList.get(0);		// default behaviour
		} else {
			return (NoteChunk)chunkList.get(index);
		}
	}
	

		
//	}
}
