package tests;
import java.io.BufferedReader;
import java.io.FileReader;

import ChordScaleDictionary.ChordScaleDictionary;
import DataObjects.ableton_live_clip.LiveClip;
import acm.program.ConsoleProgram;
import repetition_analysis.repetition_generator.RepetitionMachine;

public class RepetioneerConsoleTest extends ConsoleProgram {

	private String path = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/liveClipData.txt";
	
	public void run(){
		ChordScaleDictionary csd = new ChordScaleDictionary();
		setSize(700, 900);
		LiveClip lc = getLiveClip();
		println(lc.toString());
		RepetitionMachine rm = new RepetitionMachine();
		int sliceLength = 4;
		rm.setLiveClipForAnalysis(lc, sliceLength);
		println(rm.toString());
	}

	private LiveClip getLiveClip() {
		LiveClip lc = new LiveClip(0, 0);
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			while(true){
				String str = in.readLine();
				if (str == null) break;
				println(str);
				String[] strArr = str.split(",");
				if (strArr[0].equals("LiveMidiNote")){
					int note = Integer.valueOf(strArr[1]);
					double pos = Double.valueOf(strArr[4]);
					double len = Double.valueOf(strArr[5]);
					int vel = Integer.valueOf(strArr[6]);
					lc.addNote(note, pos, len, vel, 0);
				} else if (strArr[0].equals("length")){
					lc.length = Double.valueOf(strArr[1]);
				} else if (strArr[0].equals("loopStart")){
					lc.loopStart = Double.valueOf(strArr[1]);
				} else if (strArr[0].equals("loopEnd")){
					lc.loopEnd = Double.valueOf(strArr[1]);
				} else if (strArr[0].equals("startMarker")){
					lc.startMarker = Double.valueOf(strArr[1]);
				} else if (strArr[0].equals("endMarker")){
					lc.endMarker = Double.valueOf(strArr[1]);
				} else if (strArr[0].equals("signatureNumerator")){
					lc.signatureNumerator = Integer.valueOf(strArr[1]);
				} else if (strArr[0].equals("signatureDenominator")){
					lc.signatureDenominator = Integer.valueOf(strArr[1]);
				}
				
			}
			
		} catch (Exception ex){
			
		}
		return lc;
	}
}
