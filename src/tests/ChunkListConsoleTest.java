package tests;

import java.io.BufferedReader;
import java.io.FileReader;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import acm.program.ConsoleProgram;
import repetition_analysis.note_chunk.ChunkList;

public class ChunkListConsoleTest extends ConsoleProgram {

	private String path = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ShortTest.liveclip";
	
	public void run(){
		setSize(700, 700);
		LiveClip lc = getLiveClip();
		println(lc.toString());
		ChunkList cl = new ChunkList();
		for (LiveMidiNote lmn: lc.noteList){
			cl.addNote(lmn, lc);
		}
		cl.sortList();
		println(cl.toString());
		
	}
	
	private LiveClip getLiveClip() {
		LiveClip lc = new LiveClip(0, 0);
		try {
			lc.instantiateClipFromBufferedReader(new BufferedReader(new FileReader(path)));
		} catch (Exception ex){
			
		}
		return lc;
	}
}
