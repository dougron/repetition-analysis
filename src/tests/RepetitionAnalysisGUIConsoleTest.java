package tests;

import java.io.BufferedReader;
import java.io.FileReader;

import DataObjects.ableton_live_clip.LiveClip;
import acm.program.ConsoleProgram;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.repeat_schema_list.RepeatSchemaList;

public class RepetitionAnalysisGUIConsoleTest extends ConsoleProgram {

	private String path = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ShortTest.liveclip";
	private String chordspath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ShortTest.chords.liveclip";

	
	public void run(){
		LiveClip lc = getLiveClip();
		LiveClip chords = getChordsClip();
		int cellLength = 3;
		RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLength, chords);
		RepeatSchemaList rsl = ra.getSchemaList();
		
	}
	
	private LiveClip getClip(String p) {
		LiveClip lc = new LiveClip(0, 0);
		try {
			lc.instantiateClipFromBufferedReader(new BufferedReader(new FileReader(p)));			
		} catch (Exception ex){
			
		}
		return lc;
	}
	public LiveClip getChordsClip(){
		return getClip(chordspath);
	}
	public LiveClip getLiveClip(){
		return getClip(path);
	}
}
