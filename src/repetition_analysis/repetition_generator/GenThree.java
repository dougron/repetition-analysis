package repetition_analysis.repetition_generator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import DataObjects.chord_chunk.ChordChunk;
import DataObjects.combo_variables.IntAndDouble;
import DataObjects.combo_variables.IntAndString;
import DataObjects.incomplete_note_utils.FinalListNote;
import DataObjects.incomplete_note_utils.FinalListNoteAnnotation;
import ResourceUtils.ChordForm;
import StaticChordScaleDictionary.CSD;
import chord_progression_analyzer.ChordInKeyObject;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_chunk.FeatureChunkWrapper;
import repetition_analysis.feature_handler.FeatureInfo;
import repetition_analysis.feature_handler.Handler_LoudSoftDynamics;
import repetition_analysis.feature_handler.Handler_SimpleDynamics;
import repetition_analysis.feature_picker.FeaturePicker;
import repetition_analysis.gen_output.GenOutput;
import repetition_analysis.info_objects.PitchConstructionInfo;
import repetition_analysis.info_objects.RegisterInfo;

/*
 * first attempt at fully featured generator that produces a TreeMap<Double, FinalListNote>
 * 
 * the idea is that outputs of many of these can be combined by a choosing algorithm into
 * a final TreeMap<Double, FinalListNote> which then gets converted to a LiveClip etc for testing
 * 
 */

public class GenThree {
	
	private TreeMap<Double, FinalListNote> finalNoteMap = new TreeMap<Double, FinalListNote>();
	private boolean copyOverRule = true;	// true = any info in the final note list will be overwritten
											// if new info needs to be put inthe same place
	private double DEFAULT_STACCATO = CSD.quantResolution("8n");
	private String latestRenderName = "";
	private static final DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
	
	public GenThree(){
		
	}
	public GenThree(
			FeatureChunkRepeatSchema masterRepeatSchema,
			ChordForm outputCF, 
			double xover, 
			RegisterInfo ri, 
			FeatureChunkRepeatSchema[] rhythmFeatureArr, 
			int rhythmFeatureMasterSchemaFeatureID, 
			FeatureChunkRepeatSchema[] durationFeatureArr,
			int durationFeatureMasterSchemaFeatureID, 
			FeatureChunkRepeatSchema[] velocityFeatureArr, 
			int velocityFeatureMasterSchemaFeatureID, 
			FeatureChunkRepeatSchema[] pitchFeatureArr, 
			int pitchFeatureMasterSchemaFeatureID, 
			FeatureChunkRepeatSchema[] contourFeatureArr, 
			int contourFeatureMasterSchemaFeatureID
			){
		init(
				masterRepeatSchema,
				outputCF, 
				xover, 
				ri, 
				rhythmFeatureArr, 
				rhythmFeatureMasterSchemaFeatureID, 
				durationFeatureArr,
				durationFeatureMasterSchemaFeatureID, 
				velocityFeatureArr, 
				velocityFeatureMasterSchemaFeatureID, 
				pitchFeatureArr, 
				pitchFeatureMasterSchemaFeatureID, 
				contourFeatureArr, 
				contourFeatureMasterSchemaFeatureID);
		
	}
	
	public GenThree(ChordForm outputCF, double xover, RegisterInfo ri, FeaturePicker fp) {
		init(
				fp.getMasterRepeatSchema(),
				outputCF, 
				xover, 
				ri, 
				fp.getRhythmFeatureArr(), 
				fp.rhythmID(), 
				fp.getDurationFeatureArr(),
				fp.durationID(), 
				fp.getDynamicsFeatureArr(), 
				fp.dynamicsID(), 
				fp.getPitchFeatureArr(), 
				fp.pitchID(), 
				fp.getContourFeatureArr(), 
				fp.contourID());
	}
	public GenThree(FeaturePicker fp, double xover, RegisterInfo ri) {
		init(
				fp.getMasterRepeatSchema(),
				fp.outputCF(), 
				xover, 
				ri, 
				fp.getRhythmFeatureArr(), 
				fp.rhythmID(), 
				fp.getDurationFeatureArr(),
				fp.durationID(), 
				fp.getDynamicsFeatureArr(), 
				fp.dynamicsID(), 
				fp.getPitchFeatureArr(), 
				fp.pitchID(), 
				fp.getContourFeatureArr(),
				fp.contourID());
	}
	public void recalc(FeaturePicker fp, double xover, RegisterInfo ri){
		finalNoteMap = new TreeMap<Double, FinalListNote>();
		init(
				fp.getMasterRepeatSchema(),
				fp.outputCF(), 
				xover, 
				ri, 
				fp.getRhythmFeatureArr(), 
				fp.rhythmID(), 
				fp.getDurationFeatureArr(),
				fp.durationID(), 
				fp.getDynamicsFeatureArr(), 
				fp.dynamicsID(), 
				fp.getPitchFeatureArr(), 
				fp.pitchID(), 
				fp.getContourFeatureArr(),
				fp.contourID());
	}

	public void recalc(ChordForm outputCF, double xover, RegisterInfo ri, FeaturePicker fp){
		finalNoteMap = new TreeMap<Double, FinalListNote>();
		init(
				fp.getMasterRepeatSchema(),
				outputCF, 
				fp.xover(), 
				ri, 
				fp.getRhythmFeatureArr(), 
				fp.rhythmID(), 
				fp.getDurationFeatureArr(),
				fp.durationID(), 
				fp.getDynamicsFeatureArr(), 
				fp.dynamicsID(), 
				fp.getPitchFeatureArr(), 
				fp.pitchID(), 
				fp.getContourFeatureArr(), 
				fp.contourID());
	}
	
	
	public void init(
			FeatureChunkRepeatSchema masterRepeatSchema,
			ChordForm outputCF, 
			double xover, 
			RegisterInfo ri, 
			FeatureChunkRepeatSchema[] rhythmFeatureArr, 
			int rhythmFeatureMasterSchemaFeatureID, 
			FeatureChunkRepeatSchema[] durationFeatureArr,
			int durationFeatureMasterSchemaFeatureID, 
			FeatureChunkRepeatSchema[] velocityFeatureArr, 
			int velocityFeatureMasterSchemaFeatureID, 
			FeatureChunkRepeatSchema[] pitchFeatureArr, 
			int pitchFeatureMasterSchemaFeatureID, 
			FeatureChunkRepeatSchema[] contourFeatureArr, 
			int contourFeatureMasterSchemaFeatureID
			){
//		massiveDataSysOut(outputCF, xover, masterRepeatSchema);

		//----
//		double[] posArray = finalPosArray(masterRepeatSchema, outputCF, xover);
		
//		systemOutDoubleArray(posArray, "masterRepeatSchema barPos posArray");
		
		//---
		//finalNoteMap.clear();
		int[] rhythmIndexArr = masterRepeatSchema.getIndexList(rhythmFeatureMasterSchemaFeatureID).getiList();
		int[] durationIndexArr = masterRepeatSchema.getIndexList(durationFeatureMasterSchemaFeatureID).getiList();
		int[] velocityIndexArr = masterRepeatSchema.getIndexList(velocityFeatureMasterSchemaFeatureID).getiList();
		int[] pitchIndexArr = masterRepeatSchema.getIndexList(pitchFeatureMasterSchemaFeatureID).getiList();
		int[] contourIndexArr = masterRepeatSchema.getIndexList(contourFeatureMasterSchemaFeatureID).getiList();
		int index = 0;
		for (IntAndDouble iad: masterRepeatSchema.barXOverPosArray(xover)){
			int form = 0;
			boolean flag = true;
			FeatureChunkWrapper rhythm = rhythmFeatureArr[rhythmIndexArr[index % rhythmIndexArr.length] % rhythmFeatureArr.length].getPrimaryFeatureChunk();
			FeatureInfo rhythmFI = rhythm.getFeatureInfo();
			double[] microPosArr = getArrWithZeroArBeginning(rhythmFI.doubleArrOne);
			//System.out.println("GenThree - microPosArr.length=" + microPosArr.length);
			FeatureChunkWrapper duration = durationFeatureArr[durationIndexArr[index % durationIndexArr.length] % durationFeatureArr.length].getPrimaryFeatureChunk();
			FeatureInfo durationFI = duration.getFeatureInfo();
			FeatureChunkWrapper velocity = velocityFeatureArr[velocityIndexArr[index % velocityIndexArr.length] % velocityFeatureArr.length].getPrimaryFeatureChunk();
			FeatureInfo velocityFI = velocity.getFeatureInfo();
			FeatureChunkWrapper pitch = pitchFeatureArr[pitchIndexArr[index % pitchIndexArr.length] % pitchFeatureArr.length].getPrimaryFeatureChunk();
			FeatureInfo pitchFI = pitch.getFeatureInfo();
			FeatureChunkWrapper contour;
			FeatureInfo contourFI;
			
			if (contourFeatureArr == null || contourFeatureArr.length == 0){
				contour = pitch.originalSimpleContour();
//				contourFI = null;
			} else {
				
				contour = contourFeatureArr[contourIndexArr[index % contourIndexArr.length] % contourFeatureArr.length].getPrimaryFeatureChunk();
				
			}
			contourFI = contour.getFeatureInfo();
			
			
			while (flag){
				//System.out.println("barPos=" + iad.toString() + " outputCF.barLength()=" + outputCF.barLength());
				double pos = outputCF.barLength() * iad.i + (form * masterRepeatSchema.barCount() * outputCF.barLength()) + iad.d;
				//System.out.println("GenThree calculated pos=" + pos);
				for (int i2 = 0; i2 < microPosArr.length; i2++){
					double microPos = microPosArr[i2];
					double actualPos = pos + microPos;
					if (actualPos < outputCF.totalLength()){			// this fails the formcount issue
						FinalListNote fln = new FinalListNote(actualPos);
						
						doDurationToFLN(fln, durationFI, i2);
						doDynamicsToFLN(fln, velocityFI, i2);
						doPitchToFLN(fln, pitch, i2, contour);
						if (i2 == 0){
							fln.addAnnotation(rhythm.shortName() + ": " + rhythm.featureString());
							fln.addAnnotation(duration.shortName() + ": " + duration.featureString());
							fln.addAnnotation(velocity.shortName() + ": " + velocity.featureString());
							fln.addAnnotation(pitch.shortName() + ": " + pitch.featureString());	// pitch.featureString());
							fln.addAnnotation(contour.shortName() + ": " + contour.featureString());
						}
						// adds in annotations for overwritten cell beginnings in brackets
						if (finalNoteMap.containsKey(actualPos)){
							if (copyOverRule){
								if (finalNoteMap.get(actualPos).annotationList().size() > 0){
									for (FinalListNoteAnnotation flna: finalNoteMap.get(actualPos).annotationList()){
										String newstr = "(" + flna.text() + ")";
										fln.addAnnotation(newstr);
									}
								}
								finalNoteMap.put(actualPos, fln);
							} else {
								if (fln.annotationList().size() > 0){
									for (FinalListNoteAnnotation flna: fln.annotationList()){
										String newstr = "(" + flna.text() + ")";
										finalNoteMap.get(actualPos).addAnnotation(newstr);
									}
								}
							}
						} else {
							finalNoteMap.put(actualPos, fln);
						}
						
//						if (copyOverRule || (!copyOverRule && !finalNoteMap.containsKey(actualPos))){
//							finalNoteMap.put(actualPos, fln);
//						} 
					} else {
						flag = false;
						break;
					}
				}
				form++;
				
			}
			index++;
		}
		doNextNotes(outputCF, ri);
		doDurations();
		doPitches(ri, outputCF);
		latestRenderName = makeLatestRenderName();
		
	}
	public boolean copyOverRule(){
		return copyOverRule;
	}
	public void setCopyOverRule(boolean b){
		copyOverRule = b;
	}
	public GenOutput getGenOutput(ChordForm outputCF, double xover, RegisterInfo ri, FeaturePicker fp, boolean copyOverRule){
		this.copyOverRule = copyOverRule;
		recalc(outputCF, xover, ri, fp);
		GenOutput go = new GenOutput(getFinalNoteMap(), latestRenderName(), outputCF, fp, copyOverRule());
		return go;
	}

	private String makeLatestRenderName() {
		Date dateobj = new Date();
		return df.format(dateobj);
		
	}

	private void doPitches(RegisterInfo ri, ChordForm outputCF) {		
		for (Double key: finalNoteMap.keySet()){
			FinalListNote fln = finalNoteMap.get(key);
			fln.changePositionToOutputFormRange(outputCF);
			PitchConstructionInfo pci = (PitchConstructionInfo)fln.getPitchConstructor();
			if (pci.featureID == FeatureChunk.ABSOLUTE_TOP_NOTE_PITCH){
				fln.addNote(pci.pitchData.getFeatureInfo().getInt(pci.pitchIndex));
			} else if (pci.featureID == FeatureChunk.ABSOLUTE_TOP_NOTE_INTERVAL){
				doAbsoluteTopNoteInterval(pci, fln, ri, outputCF);				
			} else if (pci.featureID == FeatureChunk.MOD_TOP_NOTE_PITCH){
				doModTopNote(pci, fln, ri, outputCF);				
			} else if (pci.featureID == FeatureChunk.CHROMATIC_SCALE_DEGREE){
				doTopNoteChromaticScaleDegree(pci, fln, ri, outputCF);
			} else if (pci.featureID == FeatureChunk.CHROMATIC_CHORD_TONE){
				doTopNoteChromaticChordTone(pci, fln, ri, outputCF);
			} else if (pci.featureID == FeatureChunk.DIATONIC_KEY_SCALE_TONE){
				doTopNoteDiatonicScaleTone(pci, fln, ri, outputCF);
			} else if (pci.featureID == FeatureChunk.DIATONIC_CHORD_FUNCTION_TONE){
				doTopNoteDiatonicChordTone(pci, fln, ri, outputCF);
			} else if (pci.featureID == FeatureChunk.CHORD_SCALE_NONSCALE_TONE){
				doChordScaleNonTone(pci, fln, ri, outputCF);
			}
		}
	}

	private void doChordScaleNonTone(PitchConstructionInfo pci, FinalListNote fln, RegisterInfo ri,
			ChordForm outputCF) {
		int noteType = pci.pitchData.getFeatureInfo().getInt(pci.pitchIndex);
		ChordInKeyObject ciko = outputCF.getPrevailingCIKO(fln);
		int[] chordTones = ciko.getModChordTones();
		int[] scaleTones = ciko.getModScaleTones();
		int[] nonTones = ciko.getModNonTones();
		int[] testArr;
		if (noteType == 1){
			testArr = scaleTones;
		} else if (noteType == 2){
			testArr = nonTones;
		} else {
			testArr = chordTones;
		}
		
		int contourValue = pci.contourData.getFeatureInfo().getInt(pci.pitchIndex); // pitchIndex is correct
		int note = fln.previous().topNote();
		int inc = 0;
		
		if (contourValue == 0){
			while (true){
				if (arrContainsInt(testArr, (note + inc) % 12)){
					note += inc;
					break;
				}
				if (arrContainsInt(testArr, (note + inc) % 12)){
					note -= inc;
					break;
				}
				inc++;
			}
		} else {
			int count = Math.abs(contourValue);
			inc = (int)Math.signum(contourValue);
			
			if (inc == 0){
				System.out.println("GenThree.doChordScaleNonTone() error: inc was ca;lculated at 0 and will loop FOREEEEVVVVVAAAAAAAAR!!!!!");
			} else {
				count--;
				while (true){
					if (arrContainsInt(testArr, (note + inc) % 12)){
						if (count <= 0){
							note += inc;
							break;
						} else {
							count--;
						}
					}
					note += inc;
				}				
			}
		}	
		note = testNoteForRegister(note , ri);
		fln.addNote(note);
	}
	

	private void doTopNoteDiatonicChordTone(PitchConstructionInfo pci, FinalListNote fln, RegisterInfo ri,
			ChordForm outputCF) {
		FeatureInfo fi = pci.pitchData.getFeatureInfo();
		int chordDegree[] = pci.pitchData.getFeatureInfo().getIntArr(pci.pitchIndex);
		int contourValue = pci.contourData.getFeatureInfo().getInt(pci.pitchIndex); // pitchIndex is correct
		ChordInKeyObject ciko = outputCF.getPrevailingCIKO(fln);
		
		// not a perfect differentiation of chromatic scale tones...........
		int interval = ciko.chordToneInterval(chordDegree);		// + chordDegree[1]; // don't know why this was in there before
		
		//System.out.println("fln.position()=" + fln.position() + " chord: " + chord.toString());
		int modnote = (ciko.rootIndex + interval) % 12;
		int note = getNoteFromModNoteAndContour(contourValue, modnote, fln, ri, pci);
		fln.addNote(note);
		
	}

	private void doTopNoteDiatonicScaleTone(PitchConstructionInfo pci, FinalListNote fln, RegisterInfo ri,
			ChordForm outputCF) {
		int[] keyDegree = pci.pitchData.getFeatureInfo().getIntArr(pci.pitchIndex);
		IntAndString key = outputCF.getPrevailingKey(fln.position());
		int contourValue = pci.contourData.getFeatureInfo().getInt(pci.pitchIndex); // pitchIndex is correct
		int interval = CSD.getIntervalFromDiatonicKeyInfo(key.str, keyDegree);
		//System.out.println("degree=" + keyDegree[0] + "," + keyDegree[1] + " key:" + key.toString() + " interval=" + interval);
		int modnote = (key.i + interval) % 12;
		int note = getNoteFromModNoteAndContour(contourValue, modnote, fln, ri, pci);
		fln.addNote(note);
	}

	private void doTopNoteChromaticChordTone(PitchConstructionInfo pci, FinalListNote fln, RegisterInfo ri,
			ChordForm outputCF) {
		int chordDegree = pci.pitchData.getFeatureInfo().getInt(pci.pitchIndex);
		int contourValue = pci.contourData.getFeatureInfo().getInt(pci.pitchIndex); // pitchIndex is correct
		IntAndString chord = outputCF.getPrevailingChord(fln);
		//System.out.println("fln.position()=" + fln.position() + " chord: " + chord.toString());
		int modnote = (chord.i + chordDegree) % 12;
		int note = getNoteFromModNoteAndContour(contourValue, modnote, fln, ri, pci);
		fln.addNote(note);
	}

	private void doTopNoteChromaticScaleDegree(PitchConstructionInfo pci, FinalListNote fln, RegisterInfo ri, ChordForm outputCF) {
		int scaleDegree = pci.pitchData.getFeatureInfo().getInt(pci.pitchIndex);
		int contourValue = pci.contourData.getFeatureInfo().getInt(pci.pitchIndex); // pitchIndex is correct
		IntAndString scaleRoot = outputCF.getPrevailingKey(fln.position());
		//System.out.println("fln.position()=" + fln.position() + " scaleRoot: " + scaleRoot.toString());
		int modnote = (scaleRoot.i + scaleDegree) % 12;
		int note = getNoteFromModNoteAndContour(contourValue, modnote, fln, ri, pci);
		fln.addNote(note);
	}

	private void doModTopNote(PitchConstructionInfo pci, FinalListNote fln, RegisterInfo ri, ChordForm outputCF) {
		int modnote = pci.pitchData.getFeatureInfo().getInt(pci.pitchIndex);
		int contourValue = pci.contourData.getFeatureInfo().getInt(pci.pitchIndex); // pitchIndex is correct
		int note = getNoteFromModNoteAndContour(contourValue, modnote, fln, ri, pci);
		fln.addNote(note);
		
	}
	private int getNoteFromModNoteAndContour(int contourValue, int modnote, FinalListNote fln, RegisterInfo ri, PitchConstructionInfo pci){
		int note = 0;
		if (pci.contourData.featureID() == FeatureChunk.SIMPLE_MELODY_CONTOUR && contourValue != 0){
			note = getNearestModNoteInContourDirection(modnote, fln.previous().topNote(), contourValue);
			note = testNoteForRegister(note, ri);
			
		} else if (pci.contourData.featureID() == FeatureChunk.HALF_SIMPLE_MELODY_CONTOUR 
				||(pci.contourData.featureID() == FeatureChunk.SIMPLE_MELODY_CONTOUR &&
				contourValue == 0)){
			// this takes the absolute value of the half simple contour and goes for the 
			// closer modnote if it is 0 or 1 and the further if 2, ignoring actual contour
			int startNote = fln.previous().topNote();
			int upperDistance = getNearestModNoteInContourDirection(modnote, startNote, 1) - startNote;
			int lowerDistance = startNote - getNearestModNoteInContourDirection(modnote, startNote, -1);
			if (contourValue == 2){
				if (upperDistance > lowerDistance){
					note = startNote + upperDistance;
				} else {
					note = startNote - lowerDistance;
				}
			} else {
				if (upperDistance > lowerDistance){
					note = startNote - lowerDistance;
				} else {
					note = startNote + upperDistance;
				}
			}
		}
		return note;
	}

	private int testNoteForRegister(int note, RegisterInfo ri) {
		//System.out.println("testing note=" + note + " against " + ri.toString());
		while (note < ri.min) note += 12;
		while (note > ri.max) note -= 12;
		//System.out.println("new note=" + note);
		return note;
	}

	private int getNearestModNoteInContourDirection(int modnote, int topNote, int contourValue) {
		int note = topNote;
		while (note % 12 != modnote){
			note += contourValue;
		}
		return note;
	}

	private void doAbsoluteTopNoteInterval(PitchConstructionInfo pci, FinalListNote fln, RegisterInfo ri, ChordForm outputCF) {
		int interval = pci.pitchData.getFeatureInfo().getInt(pci.pitchIndex);
		int note = fln.previous().topNote() + interval;
		note = testNoteForRegister(note, ri);
//		fln.clearNoteList();
		fln.addNote(note);
		
	}

	private void doPitchToFLN(FinalListNote fln, FeatureChunkWrapper pitchFCW, int index, FeatureChunkWrapper contourFCW) {
		
		PitchConstructionInfo pci = new PitchConstructionInfo(pitchFCW.featureID());
//		if (pci.featureID == FeatureChunk.ABSOLUTE_TOP_NOTE_PITCH || pci.featureID == FeatureChunk.ABSOLUTE_TOP_NOTE_INTERVAL){
//			pci.pitchData = pitchFCW;
//			pci.pitchIndex = index;
//		}
		pci.pitchData = pitchFCW;
		pci.pitchIndex = index;
		pci.contourData = contourFCW;
		//pci.contourIndex = contourIndex;
		fln.setPitchConstructor(pci);
	}
	private void doDynamicsToFLN(FinalListNote fln, FeatureInfo velocityFI, int index) {
		if (velocityFI.featureID == FeatureChunk.SIMPLE_DYNAMICS){
			fln.setVelocity(Handler_SimpleDynamics.DEFAULT_DYNAMIC[velocityFI.getInt(index)]);
			if (fln.velocity() == Handler_SimpleDynamics.HIGH_DYNAMIC){
				fln.setArticulation(FinalListNote.ACCENT_ARTICULATION);
			}
		} else if(velocityFI.featureID == FeatureChunk.LOUD_SOFT_DYNAMICS){
			fln.setVelocity(velocityFI.getInt(index));
			if (fln.velocity() == Handler_LoudSoftDynamics.loudDynamic){
				fln.setArticulation(FinalListNote.ACCENT_ARTICULATION);
			}
		}
		
	}
	private void doDurations() {
		for (FinalListNote fln: finalNoteMap.values()){
			double totalGap = fln.next().position() - fln.position();
			if (fln.hasDurationModel()){
				double gap = totalGap - DEFAULT_STACCATO;
				if (gap < 0){
					fln.setDuration(fln.next().position() - fln.position());
				} else {
					fln.setDuration(gap * fln.durationModel() + DEFAULT_STACCATO);
				}
				
			} else {
				if (fln.duration() > totalGap){
					fln.setDuration(totalGap);
				}
			}
		}
		
	}
	private void doDurationToFLN(FinalListNote fln, FeatureInfo durationFI, int index) {
		double dur = durationFI.getDouble(index);
		if (durationFI.featureID == FeatureChunk.ABSOLUTE_DURATION || durationFI.featureID == FeatureChunk.GAP_VALUE){
			fln.setDuration(dur);
		} else if (durationFI.featureID == FeatureChunk.STACCATO_PORTATO_TENUTO){
			fln.setDurationModel(dur);
		}
		
	}
	private void doNextNotes(ChordForm outputCF, RegisterInfo ri) {
		Iterator<Double> it = finalNoteMap.keySet().iterator();
		//System.out.println("finalNoteMap.size()=" + finalNoteMap.size()); 
		Double current = it.next();
		Double next;
		Double previous = null;
		// invisible first note to start off relative melody calculations
		FinalListNote melodyRegisterSetter = new FinalListNote(-100);	// arbitarily long way before. only used for pitch 
		melodyRegisterSetter.addNote(ri.avg);
		//finalNoteMap.get(finalNoteMap.firstKey()).setPrevious(melodyRegisterSetter);
		FinalListNote previousFLN;
		while(it.hasNext()){
			next = it.next();
			//System.out.println(finalNoteMap.get(current).toString() + " next is " + finalNoteMap.get(next).toString());
			finalNoteMap.get(current).setNextNote(finalNoteMap.get(next));
			if (previous == null){
				finalNoteMap.get(current).setPrevious(melodyRegisterSetter);
			} else {
				finalNoteMap.get(current).setPrevious(finalNoteMap.get(previous));
			}
			
			previous = current;
			current = next;
		}
		finalNoteMap.get(current).setNextNote(new FinalListNote(outputCF.totalLength()));
		finalNoteMap.get(current).setPrevious(finalNoteMap.get(previous));
	}
	public String toString(){
		String str = "GenThree TreeMap";
		for (Double index: finalNoteMap.keySet()){
			str += "\nindex=" + index + "\n  " + finalNoteMap.get(index).toString();
		}
		return str;
	}
	public String latestRenderName(){
		return latestRenderName;
	}
	
	private double[] getArrWithZeroArBeginning(double[] doubleArrOne) {
		// strictly for GAP_VALUE calculations
		// leaves off the last value calculated by the Handler_GapValue as this referes to the gap to
		// a NoteChunk not in the FeatureChunk
		double[] arr = new double[doubleArrOne.length];
		arr[0] = 0.0;
		double d = 0.0;
		for (int i = 1; i < doubleArrOne.length; i++){
			arr[i] = d + doubleArrOne[i - 1];
			d += doubleArrOne[i - 1];
		}
		return arr;
	}

	private double[] finalPosArray(FeatureChunkRepeatSchema masterRepeatSchema, ChordForm outputCF, double xover) {
		ArrayList<Double> list = new ArrayList<Double>();
		int form = 0;
		boolean flag = true;
		while (flag){
			for (IntAndDouble iad: masterRepeatSchema.barXOverPosArray(xover)){
				double d = outputCF.barLength() * iad.i + (form * masterRepeatSchema.barCount() * outputCF.barLength()) + iad.d;
				//double e = outputCF.barLength() * (iad.i + form * masterRepeatSchema.barCount()) + iad.d;
				if (d < outputCF.totalLength()){
					list.add(d);
				} else {
					flag = false;
					break;
				}
				
			}			
			form++;
		}
		
		double[] arr = new double[list.size()];
		for (int i = 0; i < list.size(); i++){
			arr[i] = list.get(i);
		}
		return arr;
	}

	private void sysOutBarPosData(FeatureChunkRepeatSchema masterRepeatSchema, double xover) {
		System.out.println("barStartParam");
		systemOutIntAndDoubleArray(masterRepeatSchema.barStartPosArray(), "barNumber", "offset");
		System.out.println("barEndParam");
		systemOutIntAndDoubleArray(masterRepeatSchema.barEndPosArray(), "barNumber", "offset");
		System.out.println("barParamXOver=" + xover);
		systemOutIntAndDoubleArray(masterRepeatSchema.barXOverPosArray(xover), "barNumber", "offset");
		
	}

	private void systemOutIntAndDoubleArray(IntAndDouble[] arr, String string, String string2) {
		for (IntAndDouble iad: arr){
			System.out.println(iad.toString(string, string2));
		}		
	}

	private void systemOutIntArray(int[] arr, String name){
		String str = name + ": ";
		for (int i: arr){
			str += i +",";
		}
		System.out.println(str);
	}
	private void systemOutDoubleArray(double[] arr, String name){
		String str = name + ": ";
		for (double i: arr){
			str += i +",";
		}
		System.out.println(str);
	}
	private void massiveDataSysOut(ChordForm outputCF, double xover, FeatureChunkRepeatSchema masterRepeatSchema) {
		System.out.println("GenThree begins........................");
		System.out.println(outputCF.toString());
		
		sysOutBarPosData(masterRepeatSchema, xover);
		
		System.out.println("---");
		systemOutDoubleArray(masterRepeatSchema.posArray(), "master positionArr");
		
		System.out.println("outputCF.length()=" + outputCF.length());
		System.out.println("outputCF.totalLength()=" + outputCF.totalLength());
		System.out.println("masterRepeatSchema.length()=" + masterRepeatSchema.length() + " " + masterRepeatSchema.lc().loopStart + " " + masterRepeatSchema.lc().loopEnd);;
		System.out.println("masterRepeatSchema.barCount()=" + masterRepeatSchema.barCount());
		
	}

	public TreeMap<Double, FinalListNote> getFinalNoteMap() {

		return finalNoteMap;
	}
	private boolean arrContainsInt(int[] arr, int i) {
		for (int test: arr){
			if (test == i){
				return true;
			}
		}
		return false;
	}
}
