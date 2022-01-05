package repetition_analysis.note_chunk;
import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;

public interface NoteChunkListObject {

	public void input(LiveMidiNote lmn, LiveClip lc);
	public void setNextNoteChunk(NoteChunk nc);			// this is sequential around position and includes end of form and beginning of form items
	public void setPreviousNoteChunk(NoteChunk nc);
	public void setNextListObject(NoteChunkListObject nclo); // this will be in the order created and is only for populating the list
	public String toString();
	public String chunkNameToString();
	public double position();
	public double positionInForm();
	public double length();
//	public String absoluteNoteString();
	int averageVelocity();
	public String name();
	public NoteChunk beginningOfFormClone();
//	public NoteChunkListObject next();
//	public NoteChunkListObject previous();
}
