package tests;
import java.io.BufferedReader;
import java.io.FileReader;

import DataObjects.ableton_live_clip.LiveClip;
import XMLMaker.MXM;
import XMLMaker.MusicXMLMaker;
import XMLMaker.XMLTimeSignatureZone;
import XMLMaker.XMLKeyZone;
import acm.program.ConsoleProgram;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.repetition_generator.GenTwo;

public class GenTwoConsoleTest extends ConsoleProgram {

	private String path = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/liveClipData.txt";
	private String outputPath = "D:/_DALooperTXT/XMLMakerFiles/genTwoOutput.xml";
	
	public void run(){
		setSize(700, 900);
		GenTwo gen = new GenTwo();

//		double pos = 3.0;
//		double[] gapArr = new double[]{0.5, 1.0};
//		int[] noteArr = new int[]{60, 63, 67};
//		int[] velocityArr = new int[]{48, 48, 80};
//		double[] durationArr = new double[]{0.5, 1.0, 0.5};
//		gen.makeFinalNotes(pos, gapArr, noteArr, velocityArr, durationArr);
		
		LiveClip lc = getLiveClip();
		int cellLength = 3;
		RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLength);
		
		FeatureChunkRepeatSchema primaryRepeatSchema = (FeatureChunkRepeatSchema)ra.getSchemaList().schemaList.get(3);
		FeatureChunkRepeatSchema[] rhythmGapFeatureArr = new FeatureChunkRepeatSchema[]{
				(FeatureChunkRepeatSchema)ra.getSchemaList().schemaList.get(16),
		};
		int featureID_Rhythm = FeatureChunk.GAP_VALUE;
		FeatureChunkRepeatSchema[] noteFeatureArr = new FeatureChunkRepeatSchema[]{
				(FeatureChunkRepeatSchema)ra.getSchemaList().schemaList.get(7),
				(FeatureChunkRepeatSchema)ra.getSchemaList().schemaList.get(36),	// both ABSOLUTE_TOP_NOTE_VALUE for now				
		};
		int featureID_Note = FeatureChunk.ABSOLUTE_PITCH;
		FeatureChunkRepeatSchema[] dynamicsFeatureArr = new FeatureChunkRepeatSchema[]{
				(FeatureChunkRepeatSchema)ra.getSchemaList().schemaList.get(11),
				(FeatureChunkRepeatSchema)ra.getSchemaList().schemaList.get(29),	// dynamics stuff does not work yet				
		};
		int featureID_Dynamics = FeatureChunk.ABSOLUTE_PITCH;
		FeatureChunkRepeatSchema[] durationFeatureArr = new FeatureChunkRepeatSchema[]{
				(FeatureChunkRepeatSchema)ra.getSchemaList().schemaList.get(12),
				(FeatureChunkRepeatSchema)ra.getSchemaList().schemaList.get(13),		// both DURATION
		};
		int featureID_Duration = FeatureChunk.GAP_VALUE;
		
		LiveClip outputClip = gen.makeLiveClip(
				primaryRepeatSchema, 
				rhythmGapFeatureArr,
				featureID_Rhythm,
				noteFeatureArr, 
				featureID_Note, 
				dynamicsFeatureArr, 
				featureID_Dynamics, 
				durationFeatureArr, 
				featureID_Duration,
				lc.length
		);
		
		println(gen.toString());
		MusicXMLMaker mxm = new MusicXMLMaker(MXM.KEY_OF_C);
		mxm.measureMap.addNewTimeSignatureZone(new XMLTimeSignatureZone(4, 4, 24)); 
		mxm.keyMap.addNewKeyZone(new XMLKeyZone(MXM.KEY_OF_C, 24));
		mxm.addPart("BilliesBounce", lc);
		mxm.addPart("variation", outputClip);
		mxm.makeXML(outputPath);
	}
	
	private LiveClip getLiveClip() {
		// this can be done with the LiveClip(BufferedReader) instantiator
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