package repetition_analysis.note_chunk;
import java.util.ArrayList;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;

public class ListEnd implements NoteChunkListObject {

	
	private ArrayList<NoteChunkListObject> chunkList;
	public LiveClip lc;
	private ChunkList parent;
	private NoteChunk repeatedFirstItem;

	public ListEnd(ArrayList<NoteChunkListObject> chunkList, ChunkList parent){
		this.chunkList = chunkList;
		this.parent = parent;
	}
	@Override
	public void input(LiveMidiNote lmn, LiveClip lc) {
		this.lc = lc;
		NoteChunk chunk = new NoteChunk(lmn, lc);
		chunk.setNextListObject(parent.firstListObject());
		parent.setfirstListObjectFromChunkList(chunk);
		chunkList.add(0, chunk);
		
	}

	@Override
	public void setNextNoteChunk(NoteChunk nclo) {
		// not relevant for ListEnd

	}
	@Override
	public void setPreviousNoteChunk(NoteChunk nclo) {
		// not relevant for ListEnd
		
	}
	@Override
	public void setNextListObject(NoteChunkListObject nclo) {
		// not relevant for ListEnd
		
	}
	public String toString(){
		return "ListEnd object";
	}
	@Override
	public double position() {
		return 10000000;		// arbitrarily large position for sorting purposes
	}
	@Override
	public double length() {
		// TODO Auto-generated method stub
		return 0;
	}
//	@Override
//	public String absoluteNoteString() {
//		
//		return "END_OBJECT";
//	}
	@Override
	public double positionInForm() {
		
		return position();
	}
	@Override
	public int averageVelocity() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void setBeginningOfRepeatFormChunk(NoteChunk nclo){
		this.repeatedFirstItem = nclo;
	}
	public NoteChunk getBeginningOfRepeatFormChunk(){
		return repeatedFirstItem;
	}
	public String name(){
		return "ListEnd object";
		
	}
	@Override
	public String chunkNameToString() {
		return toString();
	}
	@Override
	public NoteChunk beginningOfFormClone() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
