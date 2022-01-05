package tests;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import DataObjects.combo_variables.DoubleAndString;
import MelodySegmenter.BatchSegmenter;
import MelodySegmenter.SegmentationModel;
import MelodySegmenter.InterOnsetPeakBoundary;
import MelodySegmenter.OffsetToOnsetBoundary;
import MelodySegmenter.PhantomLiveClip;
import ResourceUtils.ChordForm;
import XMLMaker.MXM;
import XMLMaker.MusicXMLMaker;
import XMLMaker.XMLTimeSignatureZone;
import XMLMaker.XMLKeyZone;
/*
 * proof of concept test for the proposed phrase end rectifier, which segments a melody into phrases and
 * makes the last note a chord tone. begun 17 April 2018
 */
import acm.program.ConsoleProgram;
import chord_progression_analyzer.ChordInKeyObject;


public class PhraseEndRecifierTest extends ConsoleProgram {

	String chordFormPath = "./resources/chord_progression_files/JazzBluesF.chords.liveclip";
	String xmlPath = "D:/Documents/miscForBackup/Repetition text files/SegmentetXMLs/phraseEndRectifierTest.xml";
	String liveclipPath = "D:/Documents/miscForBackup/Repetition text files/SegmentetXMLs";
	
	double[][] posOptions = new double[][]{
		new double[]{},
		new double[]{},
		new double[]{},
		new double[]{},
		new double[]{},

		new double[]{0.0},
		new double[]{0.5},
		new double[]{1.0},
		new double[]{1.5},
//		new double[]{0.0, 0.5},
//		new double[]{0.0, 1.0},
//		new double[]{0.0, 1.5},
		new double[]{0.0, 0.5, 1.0},
		new double[]{0.0, 0.5, 1.0, 1.5},
		new double[]{0.0, 0.5, 1.0, 1.5},
		new double[]{0.0, 0.5, 1.0, 1.5},
		new double[]{0.0, 0.5, 1.0, 1.5},
		new double[]{0.0, 0.5, 1.5},
		new double[]{0.0, 1.0, 1.5},
//		new double[]{0.5, 1.0},
		new double[]{0.5, 1.5},
		new double[]{0.5, 1.0, 1.5},
//		new double[]{1.0, 1.5},
	};
	Random rnd = new Random();
	int low = 44;
	int hi = 72;
	int hicut = 64;
	int lowcut = 48;
	int registerCentreOfGravity = 57;
	
	int[] intervalOptions = new int[]{1, -1, 2, -2, 3, -3, 1, -1, 2, -2, 3, -3, 1, -1, 2, -2, 3, -3, 4, -4, 5, -5, 6, -6, 7, -7};//, 8, -8, 9, -9, 10, -10};
	int[] scaleDegrees = new int[]{0, 2, 4, 5, 7, 9, 10};
	private int xmlBoundaryTextSize = 6;
	
	public void run(){
		setSize(700, 700);
		ChordForm cf = getOutputChordForm();
		println(cf.toString());
		
		ArrayList<Double> posList = makePosList(cf);
		println("posList---------------------------");
		for (Double d: posList){
			println(d);
		}
		LiveClip lc = makeLiveClip(posList);
		lc.loopStart = cf.lc().loopStart;
		lc.loopEnd = cf.lc().loopEnd;
		lc.length = cf.lc().length;
		lc.name ="randomMelody";
		BatchSegmenter bs = new BatchSegmenter(
				new SegmentationModel[]{new InterOnsetPeakBoundary(), new OffsetToOnsetBoundary()}, 
				new double[]{1.0}, 
				0.5, 
				new PhantomLiveClip(lc));
		LiveClip newlc = makeRectifiedLiveClip(bs, cf);
		MusicXMLMaker mxm = makeMusicXML(new LiveClip[]{lc, newlc}, cf, bs);
		
		mxm.makeXML(xmlPath);
		renderLiveClipToTextFile(lc);
		renderLiveClipToTextFile(newlc);
		println("done....");
	}
	private void renderLiveClipToTextFile(LiveClip lc) {
		String filepath = liveclipPath + "/" + lc.name + ".liveclip";
		//System.out.println(lc.getClipAsTextFile());
		try {
			FileWriter fw = new FileWriter(filepath);
			BufferedWriter bw = new BufferedWriter(fw);
//			System.out.println("writeLiveClipToFile called:");
//			System.out.println(lc.toString());
			String str = lc.getClipAsTextFile();
//			System.out.println(str);
			bw.write(str);
			bw.close();
			fw.close();
		} catch (IOException ex){
			System.out.println("PhraseEndRecifierTest.renderLiveClipToTextFile() failed to work");
			System.out.println(ex.getMessage());
		}
		
	}

	private MusicXMLMaker makeMusicXML(LiveClip[] lcArr, ChordForm cf, BatchSegmenter bs) {
		MusicXMLMaker mxm = new MusicXMLMaker(MXM.KEY_OF_C);
		//mxm.setLandscapePageOrientation();	// does not work. need to tweak import settings in Sibelius to get landscape orientation

		mxm.measureMap.addNewTimeSignatureZone(new XMLTimeSignatureZone(lcArr[0].signatureNumerator, lcArr[0].signatureDenominator, lcArr[0].loopEndBarCount())); 
		mxm.keyMap.addNewKeyZone(new XMLKeyZone(MXM.KEY_OF_C, lcArr[0].barCount()));
		
		boolean b = true;
		for (LiveClip lc: lcArr){
			String partName = lc.name;
			mxm.addPart(partName, lc); //, int signatureNumerator, int signatureDenominator, String fileName, int barCount);
			
			if (b){
				addChordSymbols(lc.name, mxm, cf);
				for (String key: bs.getBoundaryListMap().keySet()){
					for (Double d: bs.getBoundaryListMap().get(key)){
						mxm.addTextDirection(partName, key, d, MXM.PLACEMENT_BELOW, xmlBoundaryTextSize  );					
					}
				}
				for (Double d: bs.boundaryList()){
					mxm.addTextDirection(partName, "xxx", d, MXM.PLACEMENT_BELOW, xmlBoundaryTextSize);
					
				}
				b = false;
			}
			
		}
		
		return mxm;
		
		
	}
	private void addChordSymbols(String partName, MusicXMLMaker mxm, ChordForm cf) {
		for (DoubleAndString das: cf.getListOfSimpleKeyChordAnalysis()){
			mxm.addTextDirection(partName, das.str, das.d, MXM.PLACEMENT_ABOVE);
		}
		
	}

	private LiveClip makeRectifiedLiveClip(BatchSegmenter bs, ChordForm cf) {
		
//		ArrayList<Double> changePosList = new ArrayList<Double>();
		ArrayList<ArrayList<LiveMidiNote>> phraseList = makePhraseList(bs);
		ArrayList<ArrayList<LiveMidiNote>> newPhraseList = adjustFinalPhraseNotes(phraseList, cf, bs.plc().lc);
		LiveClip lc = new LiveClip(0, 0);
		for (ArrayList<LiveMidiNote> lmnList: newPhraseList){
			for (LiveMidiNote lmn: lmnList){
				lc.addNote(lmn);
			}
		}
		lc.loopStart = bs.plc().loopStart;
		lc.loopEnd = bs.plc().loopEnd;
		lc.length = bs.plc().length;
		lc.name = "rectified";
		return lc;
	}

	private ArrayList<ArrayList<LiveMidiNote>> adjustFinalPhraseNotes(ArrayList<ArrayList<LiveMidiNote>> phraseList, ChordForm cf, LiveClip lc) {
		ArrayList<ArrayList<LiveMidiNote>> newList = new ArrayList<ArrayList<LiveMidiNote>>();
		for (ArrayList<LiveMidiNote> list: phraseList){
			if (list.size() > 0){
				ArrayList<LiveMidiNote> llist = new ArrayList<LiveMidiNote>();
				for (LiveMidiNote lmn: list){
					llist.add(lmn.clone());
				}
				newList.add(llist);
				LiveMidiNote lmn = llist.get(llist.size() - 1);
				ChordInKeyObject ciko = cf.getPrevailingCIKO(lmn.position, lc);
				int[] modChordTones = ciko.getModChordTones();
				ArrayList<Integer> allChordTones = makeAllTwelveOctaves(modChordTones);
				lmn.note = getNearestChordTone(allChordTones, lmn.note);
			}			
		}
		return newList;		
	}

	private int getNearestChordTone(ArrayList<Integer> allChordTones, int note) {
		int distance = 1000;	// arbitrarily large
		int newNote = 0;
		for (Integer i: allChordTones){
			if (i >= low && i <= hi){
				int tdist = Math.abs(note - i);
				double tsgn = Math.signum(note - i);
				if (tdist == 0){
					return i;
				} else {
					if (tdist < distance){
						distance = tdist;
						newNote = i;
					} else if (tdist == distance){
						if (note > registerCentreOfGravity){
							if (tsgn == 1.0){
								newNote = i;
							}
						} else {
							if (tsgn == -1.0){
								newNote = i;
							}
						}
					}
				}				
			}
		}
		return newNote;
	}

	private ArrayList<Integer> makeAllTwelveOctaves(int[] arr) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < 12; i++){
			for (int n: arr){
				list.add(i * 12 + n);
			}
		}
		return list;
	}

	private ArrayList<ArrayList<LiveMidiNote>> makePhraseList(BatchSegmenter bs) {
		ArrayList<ArrayList<LiveMidiNote>> phraseList = new ArrayList<ArrayList<LiveMidiNote>>();
		ArrayList<LiveMidiNote> phraseNoteList = new ArrayList<LiveMidiNote>();
		for (int index = 0; index < bs.plc().lc.noteList.size(); index++){
			LiveMidiNote lmn = bs.plc().lc.noteList.get(index);
			if (bs.boundaryList().contains(lmn.position)){
				phraseList.add(phraseNoteList);
				phraseNoteList = new ArrayList<LiveMidiNote>();
				
			} 
			phraseNoteList.add(lmn);
		}
		phraseList.add(phraseNoteList);
		return phraseList;
	}

//	private LiveClip makeLiveClip(ArrayList<Double> posList) {	// chromatic
//		LiveClip lc = new LiveClip(0, 0);
//		int note = rnd.nextInt(hi - low) + low;
//		
//		for (double pos: posList){
//			int interval = intervalOptions[rnd.nextInt(intervalOptions.length)];
//			note += interval;
//			if (note > hicut){
//				double chance = (note - hicut) * 1.0 / (hi - hicut);
//				println("hi note=" + note + " chance=" + chance);
//				if (rnd.nextDouble() < chance){
//					note -= 12;
//				}
//			}
//			if (note < lowcut){
//				double chance = (lowcut - note) * 1.0 / (lowcut - low);
//				println("low note=" + note + " chance=" + chance);
//				if (rnd.nextDouble() < chance){
//					note += 12;
//				}
//			}
//			while (note < low) note += 12;
//			while (note > hi) note -= 12;
//			lc.addNote(note, pos, 0.5, 100, 0);
//		}
//		return lc;
//	}
	private LiveClip makeLiveClip(ArrayList<Double> posList) {  // diatonic
		LiveClip lc = new LiveClip(0, 0);
		ArrayList<Integer> bigScale = makeAllTwelveOctaves(scaleDegrees);
		int noteIndex = 0; 
		while (bigScale.get(noteIndex) < lowcut || bigScale.get(noteIndex) > hicut){
			noteIndex = rnd.nextInt(bigScale.size());
		}
		for (double pos: posList){
			int interval = intervalOptions[rnd.nextInt(intervalOptions.length)];
			noteIndex += interval;
			int note = bigScale.get(noteIndex);
			if (note > hicut){
				double chance = (note - hicut) * 1.0 / (hi - hicut);
				println("hi note=" + note + " chance=" + chance);
				if (rnd.nextDouble() < chance){
					noteIndex -= 8;
				}
			}
			if (note < lowcut){
				double chance = (lowcut - note) * 1.0 / (lowcut - low);
				println("low note=" + note + " chance=" + chance);
				if (rnd.nextDouble() < chance){
					noteIndex += 8;
				}
			}
//			while (note < low) noteIndex += 8;
//			while (note > hi) noteIndex -= 12;
			lc.addNote(bigScale.get(noteIndex), pos, 0.5, 100, 0);
			
		}
		return lc;
	}
	private ArrayList<Double> makePosList(ChordForm cf) {
		ArrayList<Double> list = new ArrayList<Double>();
		double length = cf.length();
		for (double xpos = 0.0; xpos < length; xpos += 2.0){
			int index = rnd.nextInt(posOptions.length);
			for (double pos: posOptions[index]){
				list.add(xpos + pos);
			}
		}
		return list;
	}

	private ChordForm getOutputChordForm() {
		LiveClip lc = new LiveClip(0, 0);
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(chordFormPath)));
			lc.instantiateClipFromBufferedReader(br);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ChordForm(lc);
	}
}
