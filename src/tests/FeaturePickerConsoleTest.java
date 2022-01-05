package tests;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import DataObjects.incomplete_note_utils.FinalListNote;
import ResourceUtils.ChordForm;
import StaticChordScaleDictionary.CSD;
import XMLMaker.MXM;
import XMLMaker.MusicXMLMaker;
import XMLMaker.XMLTimeSignatureZone;
import XMLMaker.XMLKeyZone;
import acm.program.ConsoleProgram;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_handler.FeatureHandler;
import repetition_analysis.feature_handler.Handler;
import repetition_analysis.feature_picker.FeaturePicker;
import repetition_analysis.info_objects.RegisterInfo;
import repetition_analysis.repeat_schema_list.SchemaListObject;
import repetition_analysis.repetition_generator.GenThree;

public class FeaturePickerConsoleTest extends ConsoleProgram {
	
	private String path = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ShortTest.liveclip";
	private String chordspath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ShortTest.chords.liveclip";
	private String outputChordPath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ChordProgressionTestFiles";
	
	private String outputPath = "D:/_DALooperTXT/XMLMakerFiles/genThreeOutput.xml";
	
	private boolean manualInput = true;
	private int outputChordProgressionIndex = 3;
	private String keepTimeSignature = "y";
	private int chordlessFormLength = 16;
	private int timeSignatureNumerator = 5;
	private int timeSignatureDenominator = 4;
	private int numberOfFormsInput = 2;
	private int registerSlectionIndex = 2;
	private int masterRepeatSchemaIndex = 9;
	private int[] rhythmFeatureSelection = new int[]{4, 0};
	private int rhythmAttachedToIndex = 4;
	private int[] durationFeatureSelection = new int[]{11, 4};
	private int durationAttachedToIndex = 4;
	private int[] dynamicsFeatureSelection = new int[]{8, 2};
	private int dynamicsAttachedToIndex = 4;
	private int[] pitchFeatureSelection = new int[]{5};
	private int pitchAttachedToIndex = 5;
	private int[] contourFeatureSelection = new int[]{1};
	private int contourAttachedToIndex = 5;


	public void run(){
		setSize(1300, 900);
		
		LiveClip lc = getLiveClip();
		LiveClip chords = getChordsClip();
		int cellLength = 3;
		RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLength, chords);
		FeaturePicker fp = new FeaturePicker(ra);
		//RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLength);
		println(ra.toString() + "\n");
		

	// output ChordForm capture ---------------------------------------------	
		ChordForm outputCF = getOutputCF();
		println("\noutputCF: " + outputCF.shortToString() + "\n");
		boolean useAbsolutePosition = true;
		double xover = 0.5;		// arb test value
		if (!ra.isSameTimeSignature(outputCF.getTimeSignature())){
			xover = getTSCrossover();
			useAbsolutePosition = false;
		}
				
		
	// output register --------------------------------------------------------
		RegisterInfo ri = getRegisterInfo(lc);
		println(ri.toString());
		
		
		
	// get master repeat schema----------------------------------------------------------
		printRepeatSchemaList(fp);
		//println(ra.chordFormToString());
		FeatureChunkRepeatSchema masterRepeatSchema = getMasterRepeatSchemaFromIndex(fp);
		println(masterRepeatSchema.toString());
		fp.setMasterRepeatSchema(masterRepeatSchema);

		
		
	// Rhythm capturer ----------------------------------------------------
		FeatureChunkRepeatSchema[] rhythmFeatureArr;
		int rhythmFeatureMasterSchemaFeatureID;
		if (manualInput){
			rhythmFeatureArr = getFeatureArray(fp, FeatureChunk.GAP_VALUE, "rhythm");
			for (FeatureChunkRepeatSchema fcrs: rhythmFeatureArr){
				println(fcrs.toShortString());
			}
			println("MASTER REPEAT SCHEMA ----------------------------");
			println(masterRepeatSchema.toString());
			// Index of the secondary feature in the masterRepeatSchema to which rhythm will be attached
			rhythmFeatureMasterSchemaFeatureID = getIntInput(ra.getHandlerMap().size(), "Attach rhythm to? --");		
			println("rhythm attached to " + ra.getHandlerMap().get(rhythmFeatureMasterSchemaFeatureID).toString());

		} else {
			rhythmFeatureArr = getPresetFeatureArray(fp, FeatureChunk.GAP_VALUE, rhythmFeatureSelection );
			for (FeatureChunkRepeatSchema fcrs: rhythmFeatureArr){
				println(fcrs.toShortString());
			}
			rhythmFeatureMasterSchemaFeatureID = rhythmAttachedToIndex ;
			println("rhythm attached to " + ra.getHandlerMap().get(rhythmFeatureMasterSchemaFeatureID).toString());
		}
		fp.setRhythmFeatureArr(rhythmFeatureArr);
		fp.setRhythmFeatureMasterSchemaFeatureID(rhythmFeatureMasterSchemaFeatureID);
		
		
	// Duration capturer ----------------------------------------------------
		FeatureChunkRepeatSchema[] durationFeatureArr;
		int durationFeatureMasterSchemaFeatureID;
		if (manualInput){
			durationFeatureArr = getFeatureArray(
					fp, 
					new int[]{
							FeatureChunk.ABSOLUTE_DURATION,
							FeatureChunk.STACCATO_PORTATO_TENUTO
					}, 
					"duration");
			for (FeatureChunkRepeatSchema fcrs: durationFeatureArr){
				println(fcrs.toShortString());
			}		
			println("MASTER REPEAT SCHEMA ----------------------------");
			println(masterRepeatSchema.toString());
			// Index of the secondary feature in the masterRepeatSchema to which rhythm will be attached
			durationFeatureMasterSchemaFeatureID = getIntInput(ra.getHandlerMap().size(), "Attach duration to? --");		
			println("duration attached to " + ra.getHandlerMap().get(durationFeatureMasterSchemaFeatureID).toString());
		} else {
			durationFeatureArr = getPresetFeatureArray(
					fp, 
					new int[]{
							FeatureChunk.ABSOLUTE_DURATION,
							FeatureChunk.STACCATO_PORTATO_TENUTO
					}, 
					durationFeatureSelection);
			for (FeatureChunkRepeatSchema fcrs: durationFeatureArr){
				println(fcrs.toShortString());
			}		
			// Index of the secondary feature in the masterRepeatSchema to which rhythm will be attached
			durationFeatureMasterSchemaFeatureID = durationAttachedToIndex ;		
			println("duration attached to " + ra.getHandlerMap().get(durationFeatureMasterSchemaFeatureID).toString());
		}
		fp.setDurationFeatureArr(rhythmFeatureArr);
		fp.setDurationFeatureMasterSchemaFeatureID(durationFeatureMasterSchemaFeatureID);
		
		
		
	// Dynamics capturer ---------------------------------------------------
		FeatureChunkRepeatSchema[] dynamicsFeatureArr;
		int dynamicsFeatureMasterSchemaFeatureID;
		if (manualInput){
			dynamicsFeatureArr = getFeatureArray(
					fp, 
					new int[]{
							FeatureChunk.LOUD_SOFT_DYNAMICS,
							FeatureChunk.SIMPLE_DYNAMICS
					},
					"dynamics");
			for (FeatureChunkRepeatSchema fcrs: dynamicsFeatureArr){
				println(fcrs.toString());
			}		
			println("MASTER REPEAT SCHEMA ----------------------------");
			println(masterRepeatSchema.toString());
			// Index of the secondary feature in the masterRepeatSchema to which rhythm will be attached
			dynamicsFeatureMasterSchemaFeatureID = getIntInput(ra.getHandlerMap().size(), "Attach dynamics to? --");		
			println("dynamics attached to " + ra.getHandlerMap().get(dynamicsFeatureMasterSchemaFeatureID).toString());
			
		} else {
			dynamicsFeatureArr = getPresetFeatureArray(
					fp, 
					new int[]{
							FeatureChunk.LOUD_SOFT_DYNAMICS,
							FeatureChunk.SIMPLE_DYNAMICS
					},
					dynamicsFeatureSelection);
			for (FeatureChunkRepeatSchema fcrs: dynamicsFeatureArr){
				println(fcrs.toString());
			}		
			// Index of the secondary feature in the masterRepeatSchema to which rhythm will be attached
			dynamicsFeatureMasterSchemaFeatureID = dynamicsAttachedToIndex;		
			println("dynamics attached to " + ra.getHandlerMap().get(dynamicsFeatureMasterSchemaFeatureID).toString());
			
		}
		fp.setDynamicsFeatureArr(dynamicsFeatureArr);
		fp.setDynamicsFeatureMasterSchemaFeatureID(dynamicsFeatureMasterSchemaFeatureID);
		
		//manualInput = true;
		
	// Pitch capture --------------------------------------------------------
	
		FeatureChunkRepeatSchema[] pitchFeatureArr;
		int pitchFeatureMasterSchemaFeatureID;
		int[] pitchFeatureOptions = getPitchFeatureOptions(fp, outputCF);
		if (manualInput){
			pitchFeatureArr = getFeatureArray(fp, pitchFeatureOptions, "pitch");
			for (FeatureChunkRepeatSchema fcrs: pitchFeatureArr){
				println(fcrs.toString());
			}		
			println("MASTER REPEAT SCHEMA ----------------------------");
			println(masterRepeatSchema.toString());
			pitchFeatureMasterSchemaFeatureID = getIntInput(ra.getHandlerMap().size(), "Attach pitch to? --");		
			println("pitch attached to " + ra.getHandlerMap().get(pitchFeatureMasterSchemaFeatureID).toString());
			
		} else {
			pitchFeatureArr = getPresetFeatureArray(fp, pitchFeatureOptions, pitchFeatureSelection);
			for (FeatureChunkRepeatSchema fcrs: pitchFeatureArr){
				println(fcrs.toString());
			}		
			println("MASTER REPEAT SCHEMA ----------------------------");
			println(masterRepeatSchema.toString());
			
			pitchFeatureMasterSchemaFeatureID = pitchAttachedToIndex;		
			println("pitch attached to " + ra.getHandlerMap().get(pitchFeatureMasterSchemaFeatureID).toString());
			
			
			
		}
		fp.setPitchFeatureArr(pitchFeatureArr);
		fp.setPitchFeatureMasterSchemaFeatureID(pitchFeatureMasterSchemaFeatureID);
		
		
	// Contour capture,if neccesary .......................................
		boolean needsContour = false;
		for (FeatureChunkRepeatSchema fcrs: pitchFeatureArr){
			println(fcrs.getPrimaryFeatureChunk().toString());
			FeatureHandler fh = FeatureChunk.getHandlerMap().get(fcrs.getPrimaryFeatureChunk().featureID());
			println(".... has handler...." + fh.name());
			println("needsContourFeature=" + fh.needsContourFeature()); 
			if (fh.needsContourFeature()){
				needsContour = true;
				break;
			}
		}
		fp.setNeedsContour(needsContour);
		FeatureChunkRepeatSchema[] contourFeatureArr;
		int contourFeatureMasterSchemaFeatureID = 0;
		if (needsContour){
			boolean useOriginalContour = false;
			println("melody needs contour info.....");
			while (true){
				String str = readLine("1) use original contour or 2) select alternative contour(s)");
				if (str.equals("1")){
					useOriginalContour = true;
					break;
				} else if (str.equals("2")){
					break;
				}
			}
			
			if (manualInput){
				if (!useOriginalContour){
					contourFeatureArr = getFeatureArray(
							fp, 
							new int[]{
									FeatureChunk.SIMPLE_MELODY_CONTOUR,
									FeatureChunk.HALF_SIMPLE_MELODY_CONTOUR
							},
							"melody contour");
					for (FeatureChunkRepeatSchema fcrs: contourFeatureArr){
						println(fcrs.toString());
					}
					println("MASTER REPEAT SCHEMA ----------------------------");
					println(masterRepeatSchema.toString());
					// Index of the secondary feature in the masterRepeatSchema to which pitch will be attached
					contourFeatureMasterSchemaFeatureID = getIntInput(ra.getHandlerMap().size(), "Attach contour to? --");		
					println("contour attached to " + ra.getHandlerMap().get(contourFeatureMasterSchemaFeatureID).toString());
				} else {
					contourFeatureArr = null;
				}
				
			} else {
				contourFeatureArr = getPresetFeatureArray(
					fp, 
					new int[]{
							FeatureChunk.SIMPLE_MELODY_CONTOUR,
							FeatureChunk.HALF_SIMPLE_MELODY_CONTOUR
					},
					contourFeatureSelection);
				for (FeatureChunkRepeatSchema fcrs: contourFeatureArr){
					println(fcrs.toString());
				}
				println("MASTER REPEAT SCHEMA ----------------------------");
				println(masterRepeatSchema.toString());
				// Index of the secondary feature in the masterRepeatSchema to which pitch will be attached
				contourFeatureMasterSchemaFeatureID = contourAttachedToIndex;		
				println("contour attached to " + ra.getHandlerMap().get(contourFeatureMasterSchemaFeatureID).toString());
				
			}		
		} else {
			println("does not need contour......");
			contourFeatureArr = new FeatureChunkRepeatSchema[0];
			
		}
		
		fp.setContourFeatureArr(contourFeatureArr);
		fp.setContourFeatureMasterSchemaFeatureID(contourFeatureMasterSchemaFeatureID);
	
	// the algorithm begins ####################################################
	// #########################################################################
		
//		GenThree gen = new GenThree(
//				masterRepeatSchema, 
//				outputCF,
//				xover,
//				ri,
//				rhythmFeatureArr,
//				rhythmFeatureMasterSchemaFeatureID,
//				durationFeatureArr,
//				durationFeatureMasterSchemaFeatureID,
//				dynamicsFeatureArr,
//				dynamicsFeatureMasterSchemaFeatureID,
//				pitchFeatureArr,
//				pitchFeatureMasterSchemaFeatureID,
//				contourFeatureArr,
//				contourFeatureMasterSchemaFeatureID
//				);
		GenThree gen = new GenThree(outputCF, xover, ri, fp);
		println(gen.toString());
		LiveClip outputClip = new LiveClip(0, 0);
		for (FinalListNote fln: gen.getFinalNoteMap().values()){			
			outputClip.addNoteList(fln.getNoteList());
		}
		outputClip.loopEnd = outputCF.totalLength();
		println(outputClip.toString());
		lc.transpose(12);
		//outputClip.transpose(12);
		makeXML(lc, outputClip);
		println("end of FeaturePickerConsoleTest.");
	}

	
	private RegisterInfo getRegisterInfo(LiveClip lc) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(lc.name);
		for (RegisterInfo ri: RegisterInfo.registerMap.values()){
			list.add(ri.name);
		}
		int i = 0;
		for (String str: list){
			println("index=" + i + " - " + str);
			i++;
		}
		int index;
		if (manualInput){
			index = getIntInput(list.size(), "registerInfo index:");
		} else {
			index = registerSlectionIndex ;
		}
		
		if (index == 0){
			return new RegisterInfo(lc.minimum(), lc.maximum(), lc.minimum(), lc.maximum(), lc.average(), lc.name);
		} else {
			return RegisterInfo.registerMap.get(list.get(index));
		}
		
	}
	private int[] getPitchFeatureOptions(FeaturePicker fp, ChordForm outputCF) {
		ArrayList<Integer> iArr = new ArrayList<Integer>();
		for (Integer index: fp.getHandlerMap().keySet()){
			FeatureHandler fh = fp.getHandlerMap().get(index);
//			if (fh.needsAnalysisChordForm() == ra.hasChordsClip() && fh.needsOutputChordForm() == outputCF.hasNoteListContent()){
//				iArr.add(index);
//			}
			if (fh.type() == Handler.MELODY_TYPE && !fh.isPolyphonic()){		
				boolean add = true;
				if (fh.needsAnalysisChordForm() && !fp.hasChordsClip()) add = false;
				if (fh.needsOutputChordForm() && !outputCF.hasNoteListContent()) add = false;
				if (add){
					iArr.add(index);
				}
			}
			
		}
		int[] pitchFeatureOptions = new int[iArr.size()];
		for (int i = 0; i < iArr.size(); i++){
			pitchFeatureOptions[i] = iArr.get(i);
		}
		return pitchFeatureOptions;
	}
	private double getTSCrossover() {
		int ii = -1;
		while (ii < 0 || ii > 100){
			ii = getIntInput(100, "get timesignature crossover(0-100%): ");			
		}
		return (double)ii / 100.0;
	}
	private FeatureChunkRepeatSchema getMasterRepeatSchemaFromIndex(FeaturePicker fp) {
		int index;
		if (manualInput){
			index = getIntInput(fp.schemaListSize(), "masterRepeatSchemaIndex:");
		} else {
			index = masterRepeatSchemaIndex ;
		}
		return fp.getRepeatSchema(index);
	}
	

	private ChordForm getOutputCF() {
		LiveClip outputForm = getOutputChordProgression();
		//if (outputForm != null) System.out.println("point 1 output clip\n" + outputForm.toString());
		int numberOfForms = 1;
		if (outputForm != null){
			//hasOutputForm = true;
			println("outputClip name=" + outputForm.name);
			String yesno = "";
			while (true){
				if (manualInput){
					yesno = readLine("keep time signature(y/n) ");
				} else {
					yesno = keepTimeSignature ;
				}
				
				if (yesno.equals("y") || yesno.equals("n")) break;
			}
			if (yesno.equals("n")){
				int[] ts = getTimeSignature();
				outputForm = outputForm.cloneIntoNewTimeSignature(ts);
				//System.out.println("point 2 output clip\n" + outputForm.toString());
			}
			if (manualInput){
				numberOfForms = getIntInput(1000, "get number of forms ");
			} else {
				numberOfForms = numberOfFormsInput ;
			}
			
		} else {
			int length;
			int ts_num;
			int ts_denom;
			if (manualInput){
				length = getIntInput(1000, "bar length of form ");
				ts_num = getIntInput(40, "signatureNumerator ");
				ts_denom = 4;
				while (true){
					ts_denom = getIntInput(16, "signatureDenominator(2/4/8/16) ");
					if (ts_denom == 2 || ts_denom == 4 || ts_denom == 8 || ts_denom == 16){
						break;
					}
				}
			} else {
				length = chordlessFormLength;
				ts_num = timeSignatureNumerator;
				ts_denom = timeSignatureDenominator;
			}
			
			outputForm = new LiveClip(0, 0);
			outputForm.loopEnd = CSD.getBarLength(ts_num, ts_denom) * length;
			outputForm.signatureNumerator = ts_num;
			outputForm.signatureDenominator = ts_denom;
			
		}
		println(outputForm.lengthAndFormToString());
		ChordForm cf = new ChordForm(outputForm);
		cf.setNumberOfForms(numberOfForms);
		return cf;
	}
	private int[] getTimeSignature() {
		int ts_num;
		int ts_denom;
		if (manualInput){
			ts_num = getIntInput(40, "signatureNumerator ");
			ts_denom = 4;
			while (true){
				ts_denom = getIntInput(16, "signatureDenominator(2/4/8/16) ");
				if (ts_denom == 2 || ts_denom == 4 || ts_denom == 8 || ts_denom == 16){
					break;
				}
			}
		} else {
			ts_num = timeSignatureNumerator;
			ts_denom = timeSignatureDenominator;
		}
		
		return new int[]{ts_num, ts_denom};
	}

	private LiveClip getOutputChordProgression() {
		LiveClip lc = new LiveClip(0, 0);
		File folder = new File(outputChordPath);
		File[] files = folder.listFiles();
		int index = 0;
		ArrayList<File> clipList = getClipList(files);
		for (File f: clipList){
			println(index + ":-" + f.getName());
			index++;
		}
		println("\nchoose " + index + " for no output chord progression");
		int chosen;
		if (manualInput){
			chosen = getIntInput(index + 1, "choose output chord progression");
		} else {
			chosen = outputChordProgressionIndex;
		}
		
		if (chosen == index){
			lc = null;
		} else {
			try {
				lc.instantiateClipFromBufferedReader(new BufferedReader(new FileReader(clipList.get(chosen).getPath())));			
			} catch (Exception ex){
				
			}
		}
		return lc;
	}
	private ArrayList<File> getClipList(File[] files) {
		ArrayList<File> fList = new ArrayList<File>();
		for (File f: files){			
			String name = f.getName();
			//sSystem.out.println(name);
			String[] str = name.split("\\.");
//			for (String s: str){
//				System.out.println(s);
//			}
			if (str.length > 0 && str[str.length - 1].equals("liveclip")){
				fList.add(f);
			}
		}
		return fList;
	}
	private void adjustOctave(LiveClip lc, int i) {
		for (LiveMidiNote lmn: lc.noteList){
			lmn.note += (i * 12);
		}
		
	}
	private int getIntInput(int size, String prompt) {
		while (true){
			String str = readLine(prompt);
			if (isInteger(str)){
				int i = Integer.parseInt(str);
				if (i > -1 && i < size){
					return i;
				} else {
					chooseAValidOneFool();
				}
			} else {
				chooseAValidOneFool();
			}
		}
	}
	private void makeXML(LiveClip lc, LiveClip outputClip) {
		MusicXMLMaker mxm = new MusicXMLMaker(MXM.KEY_OF_C);
		mxm.measureMap.addNewTimeSignatureZone(new XMLTimeSignatureZone(4, 4, 24)); 
		mxm.keyMap.addNewKeyZone(new XMLKeyZone(MXM.KEY_OF_C, 24));
		adjustOctave(lc, -1);
		adjustOctave(outputClip, -1);
		mxm.addPart("ShortTest", lc);
		mxm.addPart("variation", outputClip);
		mxm.makeXML(outputPath);
		
	}
	private FeatureChunkRepeatSchema[] getFeatureArray(FeaturePicker fp, int[] featureIDArr, String prompt) {
		ArrayList<FeatureChunkRepeatSchema> fList = fp.getFeatureList(featureIDArr);
		return getFeatureArrayInput(fList, prompt);
	}
	private FeatureChunkRepeatSchema[] getFeatureArrayInput(ArrayList<FeatureChunkRepeatSchema> fList, String prompt){

		println("\nChoose " + prompt + " schema(s)--------------------------------");
		int index = 0;
		for (FeatureChunkRepeatSchema fcrs: fList){
			println("index=" + index + " " + fcrs.toShortString());
			index++;
		}
		int[] indexList = selectIndexList(index);
		FeatureChunkRepeatSchema[] fArr = new FeatureChunkRepeatSchema[indexList.length];
		for (int i = 0; i < indexList.length; i++){
			fArr[i] = fList.get(indexList[i]);
		}
		return fArr;
	}
	private FeatureChunkRepeatSchema[] getFeatureArray(FeaturePicker fp, int featureID, String prompt) {
		ArrayList<FeatureChunkRepeatSchema> fList = fp.getFeatureList(featureID);
		return getFeatureArrayInput(fList, prompt);
	}
	private FeatureChunkRepeatSchema[] getPresetFeatureArray(FeaturePicker fp, int featureID, int[] selection) {
		ArrayList<FeatureChunkRepeatSchema> fList = fp.getFeatureList(featureID);
		return presetFeatureSelectionFromIndexArray(fList, selection);
	}
	private FeatureChunkRepeatSchema[] getPresetFeatureArray(FeaturePicker fp, int[] featureIDArr, int[] selection) {
		ArrayList<FeatureChunkRepeatSchema> fList = fp.getFeatureList(featureIDArr);
		return presetFeatureSelectionFromIndexArray(fList, selection);
	}

	private FeatureChunkRepeatSchema[] presetFeatureSelectionFromIndexArray(ArrayList<FeatureChunkRepeatSchema> fList,
			int[] selection) {
		FeatureChunkRepeatSchema[] arr = new FeatureChunkRepeatSchema[selection.length];
		for (int i = 0; i < selection.length; i++){
			arr[i] = fList.get(selection[i] % fList.size());
		}
		return arr;
	}

	private FeatureChunkRepeatSchema getMasterRepeatSchemaFromClosestRepeatCount(RepetitionAnalysis ra) {
		int closest = 0;
		if (ra.getSchemaList().size() == 1){
			println("has no features.....");
			return null;
		} else {
			while (true){
				String str = readLine("closest repeat count: ");
				if (isInteger(str)){
					closest = Integer.parseInt(str);
					break;
				} else {
					chooseAValidOneFool();
				}
			}
			SchemaListObject chosenSchema = ra.getSchemaList().schemaList.get(0);
			int difference = Math.abs(chosenSchema.count() - closest);
			int temp;
			for (SchemaListObject fcrs: ra.getSchemaList().schemaList){
				if (fcrs instanceof FeatureChunkRepeatSchema){
					temp = Math.abs(fcrs.count() - closest);
					if (temp < difference){
						chosenSchema = fcrs;
						difference = temp;
					}
				}
			}
			
			return (FeatureChunkRepeatSchema)chosenSchema;
		}
		
	}
	private int[] selectIndexList(int listLength) {
		// generic method for getting a comma delimited list of indices within a certain range
		println("all: select all");
		int[] featureKeyArr;
		while (true){
			String str = readLine("select (use comma for multiple selections):");
			if (str.equals("all")){
				featureKeyArr = makeAllIndices(listLength);
				break;			
			} else {
				String[] strArr = str.split(",");
				int[] tempiArr = new int[strArr.length];
				int count = 0;
				for (String s: strArr){
					if (isInteger(s)){
						int i = Integer.parseInt(s);
						if (i < listLength){
							tempiArr[count] = i;
							count++;
						} 
					}
				}
				if (count == 0){
					chooseAValidOneFool();
				} else {
					featureKeyArr = new int[count];
					for (int ind = 0; ind < count; ind++){
						featureKeyArr[ind] = tempiArr[ind];
					}
					break;
				}
			}

		}
		return featureKeyArr;
		
	}
	private int[] makeAllIndices(int listLength) {
		int[] arr = new int[listLength];
		for (int i = 0; i < listLength; i++){
			arr[i] = i;
		}
		return arr;
	}
	private int[] selectFeatureOptionList(RepetitionAnalysis ra) {
		println("Choose a feature.....");
		for (Integer i: ra.getHandlerMap().keySet()){
			println(i + ": " + ra.getHandlerMap().get(i).name());
		}
		println("all: select all");
		int[] featureKeyArr;
		while (true){
			String str = readLine("input (use comma for multiple selections):");
			if (str.equals("all")){
				featureKeyArr = makeAllKeys(ra);
				break;			
			} else {
				String[] strArr = str.split(",");
				int[] tempiArr = new int[strArr.length];
				int count = 0;
				for (String s: strArr){
					if (isInteger(s)){
						int i = Integer.parseInt(s);
						if (ra.getHandlerMap().containsKey(i)){
							tempiArr[count] = i;
							count++;
						} 
					}
				}
				if (count == 0){
					chooseAValidOneFool();
				} else {
					featureKeyArr = new int[count];
					for (int ind = 0; ind < count; ind++){
						featureKeyArr[ind] = tempiArr[ind];
					}
					break;
				}
			}

		}
		return featureKeyArr;
		
	}
	private int[] makeAllKeys(RepetitionAnalysis ra) {
		int[] featureKeyArr = new int[ra.getHandlerMap().keySet().size()];
		int index = 0;
		for (int key: ra.getHandlerMap().keySet()){
			featureKeyArr[index] = key;
			index++;
		}	
		return featureKeyArr;
	}
	private void chooseAValidOneFool() {
		println("Chjoose a valid one, fool.");
		
	}
	public static boolean isInteger(String s) {
		try{
			Integer.parseInt(s);
			// s is a valid integer
			return true;
		} catch (NumberFormatException ex){
			return false;
		}
	}
	private void printRepeatSchemaList(FeaturePicker fp) {
		ArrayList<String> list = fp.getSchemaListNames();
		int index = 0;
		for (String str: list){
			println("index=" + index + ": " + str);
			index++;
		}
		//println(fp.getSchemaList().toShortString());
		
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
