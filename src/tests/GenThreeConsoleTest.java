package tests;

import java.io.BufferedReader;
import java.io.FileReader;

import DataObjects.ableton_live_clip.LiveClip;
import acm.program.ConsoleProgram;
import repetition_analysis.RepetitionAnalysis;

/*
 * duplicate of the FeaturePickerConsoleTest but not requiring any selection. for speed of testing,
 * when that is required....
 */

public class GenThreeConsoleTest extends ConsoleProgram {
	
	private String path = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ShortTest.liveclip";
	private String chordspath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ShortTest.chords.liveclip";
	private String outputChordPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ChordProgressionTestFiles";
	
	private String outputPath = "D:/_DALooperTXT/XMLMakerFiles/genTwoOutput.xml";


	public void run(){
		setSize(1300, 900);
		
		LiveClip lc = getLiveClip();
		LiveClip chords = getChordsClip();
		int cellLength = 3;
		RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLength, chords);
		//RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLength);
		println(ra.toString() + "\n");
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
