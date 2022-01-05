package tests;
import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import ResourceUtils.ChordForm;
import acm.program.ConsoleProgram;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.note_chunk.ChunkList;
import repetition_analysis.note_chunk.NoteChunk;
import repetition_analysis.note_chunk.NoteChunkListObject;

public class FeatureChunkConsoleTest extends ConsoleProgram {

	
	public void run(){
		setSize(1200, 900);
		LiveClip lc = getLiveClipMajorScale();
		FeatureChunk fc = makeFeatureChunk(lc);
		ChordForm cf = new ChordForm(getChordClip());
		double d = 4.0;
		println(d + ": " + cf.getPrevailingChordSymbol(d) + ", " + cf.getPrevailingChord(d, lc).toString());
//		for (double d = 0.0; d < 12.0; d++){
//			println(d + ": " + cf.getPrevailingChordSymbol(d) + ", " + cf.getPrevailingChord(d, lc).toString());
//		}
		println(cf.chunkListToString());
		fc.setChordForm(cf);
		println(fc.toStringLarge());
//		FeatureInfo fi = fc.getFeatureInfo(FeatureChunk.ABSOLUTE_PITCH);
//		println(fi.toString());
//		FeatureInfo fi2 = fc.getFeatureInfo(FeatureChunk.MOD_PITCH);
//		println(fi2.toString());
	}

	private FeatureChunk makeFeatureChunk(LiveClip lc){
		FeatureChunk fc = new FeatureChunk();
		for (NoteChunkListObject nclo: getChunkList(lc).chunkList){
			if (nclo instanceof NoteChunk){
				fc.add((NoteChunk)nclo);
			}
		}
		return fc;
	}
	private ChunkList getChunkList(LiveClip lc){
		//LiveClip lc = getLiveClip();
		ChunkList cl = new ChunkList();
		cl.addClip(lc);
		return cl;
	}
	private NoteChunk[] getNoteChunks() {
		LiveClip lc = getLiveClip();
		NoteChunk[] arr = new NoteChunk[lc.noteList.size()];
		int index = 0;
		for (LiveMidiNote lmn: lc.noteList){
			arr[index] = new NoteChunk(lmn, lc);
			
			index++;
		}
		return arr;
	}
	private LiveClip getChordClip(){
		LiveClip lc = new LiveClip(0, 0);
		lc.addNote(65, 0.0, 4.0, 65, 0);	// F major
		lc.addNote(68, 0.0, 4.0, 65, 0);
		lc.addNote(72, 0.0, 4.0, 100, 0);
		lc.addNote(67, 4.0, 4.0, 80, 0);	// C major
		lc.addNote(72, 4.0, 4.0, 80, 0);
		lc.addNote(76, 4.0, 4.0, 80, 0);
		lc.startMarker = 0.0;
		lc.endMarker = 8.0;
		lc.loopStart = 0.0;
		lc.loopEnd = 8.0;
		lc.length = 8.0;
		return lc;
	}
	private LiveClip getLiveClipMajorScale() {
		LiveClip lc = new LiveClip(0, 0);
		lc.addNote(77, 0.0, 0.5, 65, 0);
		lc.addNote(76, 0.5, 0.5, 65, 0);
		lc.addNote(74, 1.0, 0.5, 100, 0);
		lc.addNote(70, 3.0, 1.0, 80, 0);
		lc.addNote(71, 4.5, 0.5, 80, 0);
		lc.addNote(74, 5.0, 1.0, 80, 0);
		lc.addNote(76, 6.0, 1.0, 80, 0);
		lc.addNote(77, 7.0, 1.0, 80, 0);
		lc.startMarker = 0.0;
		lc.endMarker = 8.0;
		lc.loopStart = 0.0;
		lc.loopEnd = 8.0;
		lc.length = 8.0;
		return lc;
	}

	private LiveClip getLiveClip() {
		LiveClip lc = new LiveClip(0, 0);
		lc.addNote(65, 1.0, 1.0, 65, 0);
		lc.addNote(69, 1.0, 1.0, 65, 0);
		lc.addNote(65, 4.0, 0.5, 100, 0);
		lc.addNote(65, 7.5, 0.5, 80, 0);
		lc.startMarker = 0.0;
		lc.endMarker = 8.0;
		lc.loopStart = 0.0;
		lc.loopEnd = 8.0;
		lc.length = 8.0;
		return lc;
	}
}
