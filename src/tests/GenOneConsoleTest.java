package tests;
import java.io.BufferedReader;
import java.io.FileReader;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.incomplete_note_utils.FinalListNote;
import acm.program.ConsoleProgram;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.repeat_schema_list.RepeatSchemaList;
import repetition_analysis.repeat_schema_list.SchemaListObject;
import repetition_analysis.repetition_generator.GenOne;
import repetition_analysis.repetition_generator.RepetitionGenerator;

public class GenOneConsoleTest extends ConsoleProgram {

	private String path = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/liveClipData.txt";

	
	public void run(){
		setSize(700, 900);
		LiveClip lc = getLiveClip();
		int cellLength = 3;
		RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLength);
		RepeatSchemaList rsl = ra.getSchemaList();
//		println(rsl.toString());
		RepetitionGenerator generator = new GenOne();
		
		SchemaListObject slo = rsl.schemaList.get(5);	// 5 - arb for testing
		println(slo.toString());
		
		SchemaListObject slo2 = rsl.schemaList.get(5);	// 5 - arb for testing. in this case chosen because of GAP_VALUE

		generator.addRepetitionSchema(slo);
		println(generator.toString());
		generator.addFeatureChunk(slo2);
		println(generator.toString());
		
		println("finalNoteList:------");
		for (FinalListNote fn: generator.getFinalNoteList()){
			println(fn.toString());
		}
	}

	private FeatureChunkRepeatSchema getRepSchema() {
//		FeatureChunkRepreatSchema fcrs = new FeatureChunkRepeatSchema();
		return null;
	}
	private LiveClip getLiveClip() {
		LiveClip lc = new LiveClip(0, 0);
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			while(true){
				String str = in.readLine();
				if (str == null) break;
				//println(str);
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
				} else if (strArr[0].equals("name")){
					lc.name = strArr[1];
				}
				
				
			}
			
		} catch (Exception ex){
			
		}
		return lc;
	}
}
