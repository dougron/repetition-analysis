package tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import DataObjects.combo_variables.DoubleAndString;
import DataObjects.incomplete_note_utils.FinalListNote;
import ResourceUtils.ChordForm;
import XMLMaker.MXM;
import XMLMaker.MusicXMLMaker;
import XMLMaker.XMLTimeSignatureZone;
import XMLMaker.XMLKeyZone;
import acm.program.ConsoleProgram;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_handler.FeatureInfo;
import repetition_analysis.note_chunk.NoteChunk;
import repetition_analysis.repeat_schema_list.RepeatSchemaList;
import repetition_analysis.repeat_schema_list.SchemaListObject;

public class AnnotateCorpusItem extends ConsoleProgram {
	
//	private String testContentPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/CorpusMelodyFiles/BilliesBounceF.liveclip";
//	private String testChordPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ShortTest.chords.liveclip";
//	private String testChordPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ChordProgressionTestFiles/FJazzBlues.chords.liveclip";

//	private String testContentPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/CorpusMelodyFiles/OpaSupaInstrumental.liveclip";
//	private String testChordPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ChordProgressionTestFiles/OpaSupaInstrumental.chords.liveclip";
//	private String testContentPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/CorpusMelodyFiles/StraightNoChaser.liveclip";
//	private String testChordPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ChordProgressionTestFiles/FJazzBlues.chords.liveclip";
//	private String testContentPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/CorpusMelodyFiles/BlueBossa.liveclip";
//	private String testChordPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ChordProgressionTestFiles/BlueBossa.chords.liveclip";
//	private String testContentPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/CorpusMelodyFiles/BlackOrpheus.liveclip";
//	private String testChordPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ChordProgressionTestFiles/BlackOrpheus.chords.liveclip";
	private String testContentPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/CorpusMelodyFiles/Anthropology.liveclip";
	private String testChordPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ChordProgressionTestFiles/BbRhythmChanges.chords.liveclip";
//	private String testContentPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/CorpusMelodyFiles/Confirmation.liveclip";
//	private String testChordPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ChordProgressionTestFiles/Confirmation.chords.liveclip";

	
	private String simpleFeatureListPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/simpleFeatureList.txt";
	private String simpleFeatureRepeatListPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/simpleFeatureRepeatList.txt";

	
//	private String testContentPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/SevenFlatNine.liveclip";
//	private String testChordPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/SevenFlatNine.chords.liveclip";

	
	private String musicXMLFolderPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/AnnotateCorpusMusicXML/";
	private int xOffset = -50;
	private int cellLength = 3;
	private CharSequence separatorReplacement = "/";


	public void run(){
		setSize(700, 700);
		LiveClip lc = getLiveClipFromFile(testContentPath);
		println("melody clip ------------------------------------\n" + lc.toString());
		LiveClip chords = getLiveClipFromFile(testChordPath);
		println("\n\n\nchords clip ------------------------------------\n" + chords.toString());
//		prepChordsClip(chords, -1);
		
//		makeBasicAnnotatedScore(lc);
		makeLeadSheet(lc, chords);		
//		makeMediumAnnotatedScore(lc, chords);
//		makeAbbrevationKey();
		
		
		println("done.");
	}
	private void makeLeadSheet(LiveClip lc, LiveClip chords) {
		println("makeLeadSheet() called.....");
		RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLength , chords);
		makeSimpleFeatureList(ra);
		makeSimpleFeatureRepeatList(ra);
		ArrayList<FinalListNote> flnList = new ArrayList<FinalListNote>();
		
		boolean b = true;
		for (FeatureChunk fc: ra.getFeatureChunkList()){
			println(fc.toString());
//			if (b){
//				FinalListNote header = new FinalListNote(0.0);
//				for (int key: FeatureChunk.descriptionIndexArr){
//					String str = FeatureChunk.getFeatureShortName(key);
//					header.addAnnotation(str + ":", xOffset * 2);
//				}
//				b = false;
//				flnList.add(header);
//			}
			FinalListNote fln = new FinalListNote(fc.position());
			
			boolean bb = true;
			for (NoteChunk nc: fc.chunkList){
				if (bb){
					for (LiveMidiNote lmn: nc.noteList){
						fln.addNote(lmn.note - 22);			// -22 is to compensate for the Confirmation botch up
					}
					fln.setDuration(nc.length);
					fln.setVelocity(nc.averageVelocity());
					
//					for (int key: FeatureChunk.descriptionIndexArr){
//						FeatureInfo fi = fc.getFeatureInfo(key);
//						fln.addAnnotation(fi.getFeatureString());
//					}
					bb = false; 		// only takes the first NoteChunk, if it exists at all
				}
			}
			flnList.add(fln);
			
			
		}
		println("\n\n\n\nmedium finalNoteList created..................");
		for (FinalListNote fln: flnList){
			println(fln.toString());
		}

//		makeMusicXML(flnList, lc.signatureNumerator, lc.signatureDenominator, lc.name + "_ra_info", lc.barCount(), lc.name, null);
		MusicXMLMaker mxm = new MusicXMLMaker(MXM.KEY_OF_C);
		//mxm.setLandscapePageOrientation();	// does not work. need to tweak import settings in Sibelius to get landscape orientation

		mxm.measureMap.addNewTimeSignatureZone(new XMLTimeSignatureZone(lc.signatureNumerator, lc.signatureDenominator, lc.barCount())); 
		mxm.keyMap.addNewKeyZone(new XMLKeyZone(MXM.KEY_OF_C, lc.barCount()));
		
//		String chordPartName = "chords";
//		mxm.addPart(chordPartName, chords);

		String partName = lc.name;
		mxm.addPart(partName, flnList); //, int signatureNumerator, int signatureDenominator, String fileName, int barCount);
		
		if (ra.hasChordsClip()){
			for (DoubleAndString das: ra.cf().getListOfChords()){
				println("placing " + das.str + " at " + das.d);
				//mxm.addTextDirection(chordPartName, das.str, das.d, MXM.PLACEMENT_ABOVE);
				mxm.addTextDirection(partName, ra.cf().getPrevailingCIKO(das.d, lc).toStringKeyChordAndSimpleFunction(), das.d, MXM.PLACEMENT_ABOVE);
				
			}
			
		}
		
//		if (cf != null){
//			addChordSymbols(partName, mxm, cf);
//		}
		String fileName = lc.name + "_ra_info";
		String outputPath = musicXMLFolderPath + fileName + ".xml";
		println(outputPath + " - file created" );
		mxm.makeXML(outputPath);
		
	}
	private void prepChordsClip(LiveClip chords, int octaveShift) {
		double start = chords.loopStart;
		for (LiveMidiNote lmn: chords.noteList){
			lmn.position -= start;
			lmn.note += octaveShift * 12;
		}
		chords.loopStart = 0;
		chords.loopEnd -= start;
		
	}
	private LiveClip getLiveClipFromFile(String path) {
		LiveClip lc = new LiveClip(0, 0);
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			lc.instantiateClipFromBufferedReader(br);
		} catch (Exception ex){
			
		}
		return lc;
	}
	private void makeAbbrevationKey() {
		String filename = musicXMLFolderPath + "/abbreviationKey.txt";
		try {
			File f = new File(filename);
			if (!f.exists()) {
			     f.createNewFile();
			  }
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			String str = "Key to Abbreviations:\n";
			for (int key: FeatureChunk.descriptionIndexArr){
				str += FeatureChunk.getFeatureShortName(key) + ":\t" + FeatureChunk.getHandlerMap().get(key).description() + "\n";
				
			}
			bw.write(str);
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void makeMediumAnnotatedScore(LiveClip lc, LiveClip chords) {
		println("makeMediumAnnotatedScore() called.....");
		RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLength , chords);
		makeSimpleFeatureList(ra);
		makeSimpleFeatureRepeatList(ra);
		ArrayList<FinalListNote> flnList = new ArrayList<FinalListNote>();
		
		boolean b = true;
		for (FeatureChunk fc: ra.getFeatureChunkList()){
			println(fc.toString());
			if (b){
				FinalListNote header = new FinalListNote(0.0);
				for (int key: FeatureChunk.descriptionIndexArr){
					String str = FeatureChunk.getFeatureShortName(key);
					header.addAnnotation(str + ":", xOffset * 2);
				}
				b = false;
				flnList.add(header);
			}
			FinalListNote fln = new FinalListNote(fc.position());
			
			boolean bb = true;
			for (NoteChunk nc: fc.chunkList){
				if (bb){
					for (LiveMidiNote lmn: nc.noteList){
						fln.addNote(lmn.note);
					}
					fln.setDuration(nc.length);
					fln.setVelocity(nc.averageVelocity());
					
					for (int key: FeatureChunk.descriptionIndexArr){
						FeatureInfo fi = fc.getFeatureInfo(key);
						fln.addAnnotation(fi.getFeatureString());
					}
					bb = false; 		// only takes the first NoteChunk, if it exists at all
				}
			}
			flnList.add(fln);
			
			
		}
		println("\n\n\n\nmedium finalNoteList created..................");
		for (FinalListNote fln: flnList){
			println(fln.toString());
		}

//		makeMusicXML(flnList, lc.signatureNumerator, lc.signatureDenominator, lc.name + "_ra_info", lc.barCount(), lc.name, null);
		MusicXMLMaker mxm = new MusicXMLMaker(MXM.KEY_OF_C);
		//mxm.setLandscapePageOrientation();	// does not work. need to tweak import settings in Sibelius to get landscape orientation

		mxm.measureMap.addNewTimeSignatureZone(new XMLTimeSignatureZone(lc.signatureNumerator, lc.signatureDenominator, lc.barCount())); 
		mxm.keyMap.addNewKeyZone(new XMLKeyZone(MXM.KEY_OF_C, lc.barCount()));
		
		String chordPartName = "chords";
		mxm.addPart(chordPartName, chords);
		if (ra.hasChordsClip()){
			ArrayList<DoubleAndString> dslist = ra.cf().getListOfChords();
			for (DoubleAndString das: ra.cf().getListOfChords()){
				//mxm.addTextDirection(chordPartName, das.str, das.d, MXM.PLACEMENT_ABOVE);
				mxm.addTextDirection(chordPartName, ra.cf().getPrevailingCIKO(das.d, lc).toStringKeyChordAndSimpleFunction(), das.d, MXM.PLACEMENT_ABOVE);
			}
			
		}
		
		
		String partName = lc.name;
		mxm.addPart(partName, flnList); //, int signatureNumerator, int signatureDenominator, String fileName, int barCount);
//		if (cf != null){
//			addChordSymbols(partName, mxm, cf);
//		}
		String fileName = lc.name + "_ra_info";
		String outputPath = musicXMLFolderPath + fileName + ".xml";
		println(outputPath + " - file created" );
		mxm.makeXML(outputPath);
		
	}
	private void makeSimpleFeatureRepeatList(RepetitionAnalysis ra) {
		try {
			File f = new File(simpleFeatureRepeatListPath);
			if (!f.exists()) {
			     f.createNewFile();
			  }
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			String str = "SimpleFeatureRepeatList:\n";
			ArrayList<FeatureChunkRepeatSchema> flist = new ArrayList<FeatureChunkRepeatSchema>();
			for (SchemaListObject slo: ra.getSchemaList().schemaList){
				if (slo instanceof FeatureChunkRepeatSchema){
					flist.add((FeatureChunkRepeatSchema)slo);
				}
			}
			Collections.sort(flist, RepeatSchemaList.featureAndPosComparator);
			for (FeatureChunkRepeatSchema fcrs: flist){
				str += fcrs.featureShortName() + "\t" + fcrs.featureString() + "\t" + fcrs.positionsToString() + "\n";
			}
			
			bw.write(str);
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	private void makeSimpleFeatureList(RepetitionAnalysis ra) {
		try {
			File f = new File(simpleFeatureListPath);
			if (!f.exists()) {
			     f.createNewFile();
			  }
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			String str = "SimpleFeatureList:\n";
			for (int key: FeatureChunk.descriptionIndexArr){
				for (FeatureChunk fc: ra.getFeatureChunkList()){
					str += fc.getFeatureShortName(key) + ":\tposition=" + fc.position() + "\t" + fc.getFeatureString(key) + "\n";
					
				}
			}
			
			bw.write(str);
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void makeBasicAnnotatedScore(LiveClip lc){
		// this makes a basic annotation of each note from what is contained 
				// within the relevant LiveMidiNote
		ArrayList<FinalListNote> flnList = new ArrayList<FinalListNote>();

		boolean b = true;
		for (LiveMidiNote lmn: lc.noteList){
			if (b){
				FinalListNote header = new FinalListNote(0.0);
				header.addAnnotation("pos:", xOffset);
				header.addAnnotation("note:", xOffset);
				header.addAnnotation("len:", xOffset);
				header.addAnnotation("vel:", xOffset);
				b = false;
				flnList.add(header);
			}
			FinalListNote fln = new FinalListNote(lmn.position);
			fln.addNote(lmn.note);
			fln.setDuration(lmn.length);
			fln.setVelocity(lmn.velocity);
			
			fln.addAnnotation("" + lmn.position);
			fln.addAnnotation("" + lmn.note);
			fln.addAnnotation("" + lmn.length);
			fln.addAnnotation("" + lmn.velocity);
			
			flnList.add(fln);
		}
		println("\n\n\n\nbasic finalNoteList created..................");
		for (FinalListNote fln: flnList){
			println(fln.toString());
		}
		makeMusicXML(flnList, lc.signatureNumerator, lc.signatureDenominator, lc.name + "_basic_midi_info", lc.barCount(), lc.name, null);

	}
	private void makeMusicXML(ArrayList<FinalListNote> flnList, int signatureNumerator, int signatureDenominator, String fileName, int barCount, String partName, ChordForm cf){
		MusicXMLMaker mxm = new MusicXMLMaker(MXM.KEY_OF_C);
		//System.out.println("lc.barCount()=" + lc.barCount());
		mxm.measureMap.addNewTimeSignatureZone(new XMLTimeSignatureZone(signatureNumerator, signatureDenominator, barCount)); 
		mxm.keyMap.addNewKeyZone(new XMLKeyZone(MXM.KEY_OF_C, barCount));
		//String partName = lc.name;
		mxm.addPart(partName, flnList); //, int signatureNumerator, int signatureDenominator, String fileName, int barCount);
		if (cf != null){
			addChordSymbols(partName, mxm, cf);
		}
		String outputPath = musicXMLFolderPath + fileName + ".xml";
		mxm.makeXML(outputPath);
	}
	private void addChordSymbols(String partName, MusicXMLMaker mxm, ChordForm cf) {
		for (DoubleAndString das: cf.getListOfChords()){
			mxm.addTextDirection(partName, das.str, das.d, MXM.PLACEMENT_ABOVE);
		}
		
	}

}
