package repetition_analysis.note_chunk;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import StaticChordScaleDictionary.CSD;
import StaticChordScaleDictionary.ChordAnalysis;
import StaticChordScaleDictionary.NotePatternAnalysis;

public class NoteChunk implements NoteChunkListObject{

	public double length;
	public double position;
	public ArrayList<LiveMidiNote> noteList = new ArrayList<LiveMidiNote>();
	private NoteChunk nextNoteChunk = null;
	private NoteChunk previousNoteChunk = null;
	//private NoteChunk nextNoteChunk = 
	private static int staticCount = 0;
	private int count;
	public LiveClip lc;		// this is just there for the metadata
	private String name;
	private NoteChunkListObject nextListObject;
	
	
	public NoteChunk(LiveMidiNote lmn, LiveClip lc){
		noteList.add(lmn);
		length = lmn.length;
		position = lmn.position;
		this.lc = lc;
		count = staticCount;
		staticCount++;
		name = "NoteChunk-" + count;
	}
	@Override
	public NoteChunk clone(){
		// deepcopy
		NoteChunk nc = new NoteChunk(noteList.get(0), lc);
		for (int i = 1; i < noteList.size(); i++){
			input(noteList.get(i), lc);
		}
		return nc;
	}

	@Override
	public void input(LiveMidiNote lmn, LiveClip lc) {				// in this case lc is not actually used, cos we assume that the lc passed to the instantiator is the clip to use for metadata
		if (lmn.position == position && lmn.length == length){
			noteList.add(lmn);
			Collections.sort(noteList, LiveMidiNote.posAndNoteComparator);
		} else {
			nextListObject.input(lmn, lc);
		}		
	}
	@Override
	public void setNextNoteChunk(NoteChunk nc) {
		this.nextNoteChunk = nc;		
	}
	@Override
	public void setPreviousNoteChunk(NoteChunk nc) {
		this.previousNoteChunk = nc;
		
	}
	@Override
	public void setNextListObject(NoteChunkListObject nclo) {
		this.nextListObject = nclo;
		
	}
	
	@Override
	public double position() {
		return position;
	}
	@Override
	public double length() {
		return length;
	}
	@Override
	public int averageVelocity(){
		int total = 0;
		for (LiveMidiNote lmn: noteList){
			total += lmn.velocity;
		}
		return total / noteList.size();
	}
// toString methods --------------------------------------------------------------------------
	public String toString(){
		String str = "NoteChunk #" + count + ": position=" + position + " length=" + length;
		for (LiveMidiNote lmn: noteList){
			str += "\n    " + lmn.toString();
		}
		str += "\n   absoluteNoteString=" + absoluteNoteString();
		str += "\n" + chordAnalysisToString();
		if (nextNoteChunk != null) str += "nextNoteChunk(): " + nextNoteChunk.name() + "\n";
		if (previousNoteChunk != null) str += "previousNoteChunk(): " + previousNoteChunk.name() + "\n";
		if (nextListObject != null){
			str += "nextListObject: " + nextListObject.name();
		} else {
			str += "no nextListObject";
		}
		
		return str;
	}
	private String absoluteNoteString() {
		String str = "";
		for (LiveMidiNote lmn: noteList){
			str += lmn.absoluteNoteName();
		}
		return str;
	}
	public String chunkNameToString(){
		String str = "NoteChunk #" + count + ": position=" + position + " length=" + length;
		str += " " + absoluteNoteString();
		return str;
	}
	public String bracketedNoteData(){
		// for debugging
		String str = "(";
		for (LiveMidiNote lmn: noteList){
			str += lmn.toLOMString();
		}
		return str + ")";
	}
// comparators -------------------------------------------------------------------------------
	public static Comparator<NoteChunkListObject> posAndLenComparator = new Comparator<NoteChunkListObject>(){
		public int compare(NoteChunkListObject chunk1, NoteChunkListObject chunk2){
			if (chunk1.position() < chunk2.position()) return -1;
			if (chunk1.position() > chunk2.position()) return 1;
			if (chunk1.length() < chunk2.length()) return -1;
			if (chunk1.length() > chunk2.length()) return 1;
			return 0;
		}
	};
//------------------------------------------------------------------------------------------------


	public String chordAnalysisToString(){
		
		ArrayList<Integer> nList = getNoteIntegerList();
 		ArrayList<NotePatternAnalysis> npaList = CSD.getChordOptions(nList);
 		String str = "NotePatternAnalysis list:-\n";
 		for (NotePatternAnalysis npa: npaList){
 			str += npa.chordSymbolToString() + "\n";
 		}
		return str;
	}


	private ArrayList<Integer> getNoteIntegerList() {
		ArrayList<Integer> nList = new ArrayList<Integer>();
		for (LiveMidiNote lmn: noteList){
			nList.add(lmn.note);
		}
		return nList;
	}

// return info methods -----------------------------------------------------------
	public int topNote() {		
		return noteList.get(noteList.size() - 1).note;
	}
	
	public int[] noteArray(){
		int[] arr = new int[noteList.size()];
		int index = 0;
		for (LiveMidiNote lmn: noteList){
			arr[index] = lmn.note;
			index++;
		}
		return arr;
	}
	public int[] modNoteArray() {
		int[] arr = new int[noteList.size()];
		int index = 0;
		for (LiveMidiNote lmn: noteList){
			arr[index] = lmn.note % 12;
			index++;
		}
		return arr;

	}
	public NoteChunk next(){
		return nextNoteChunk;
//		if (nextNoteChunk instanceof ListEnd){
//			ListEnd le = (ListEnd)nextNoteChunk;
//			return le.getBeginningOfRepeatFormChunk();
//		} else {
//			return nextNoteChunk;
//		}
		
	}
	public NoteChunk previous(){
		return previousNoteChunk;
	}
	
	


	@Override
	public double positionInForm() {
		return position - lc.loopStart;
	}
	public NoteChunk endOfFormClone(){
		// makes a version of this but one form length later on the timeline
		// expressly for returning the first NoteCHunk of a piece by the 
		// ListEnd instance for Handler_StaPorNuto measurement....
		ArrayList<LiveMidiNote> lmnList = new ArrayList<LiveMidiNote>();
		for (LiveMidiNote lmn: noteList){
			LiveMidiNote newLMN = lmn.clone();
			newLMN.position += lc.loopEnd - lc.loopStart;
			lmnList.add(newLMN);
		}
		
		NoteChunk nc = new NoteChunk(lmnList.get(0), lc);
		for (int i = 1; i < lmnList.size(); i++){
			nc.input(lmnList.get(i), lc);
		}
		nc.name += " endClone of NoteChunk-" + count;
		return nc;
	}
	public NoteChunk beginningOfFormClone(){
		// makes a version of this but one form length earlier on the timeline
		// expressly for AbsoluteTopNoteInterval

		ArrayList<LiveMidiNote> lmnList = new ArrayList<LiveMidiNote>();
		for (LiveMidiNote lmn: noteList){
			LiveMidiNote newLMN = lmn.clone();
			newLMN.position -= lc.loopEnd - lc.loopStart;
			lmnList.add(newLMN);
		}
		
		NoteChunk nc = new NoteChunk(lmnList.get(0), lc);
		for (int i = 1; i < lmnList.size(); i++){
			nc.input(lmnList.get(i), lc);
		}
		nc.name += " beginningClone of NoteChunk-" + count;
		return nc;
	}

	@Override
	public String name() {
		return name;
	}

	public String[] toStringArray() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(name());
		for (LiveMidiNote lmn: noteList){
			list.add(lmn.toLOMString());
		}
		return list.toArray(new String[list.size()]);
	}

	

	



}
