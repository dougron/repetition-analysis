package repetition_analysis.gen_output;

import java.util.ArrayList;
import java.util.TreeMap;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import DataObjects.incomplete_note_utils.FinalListNote;
import ResourceUtils.ChordForm;
import repetition_analysis.feature_picker.FeaturePicker;

public class GenOutput {

	private TreeMap<Double, FinalListNote> flnMap;
	private ArrayList<FinalListNote> flnList;
	private ChordForm outputCF;
	private String renderName;
	private LiveClip lc;
	private FeaturePicker fp;
	private boolean copyOverRule;
	private boolean hasFeaturePicker = false;

	public GenOutput(TreeMap<Double, FinalListNote> treeMap, String renderName, ChordForm outputCF, FeaturePicker fp, boolean copyOverRule){
		this.outputCF = outputCF;
		this.renderName = renderName;
		this.flnMap = treeMap;
		this.flnList = makeFinalNoteList(flnMap);
		this.lc = makeLiveClip(flnMap, outputCF);
		this.fp = fp;
		this.copyOverRule = copyOverRule;
		hasFeaturePicker = true;
	}
	public GenOutput(TreeMap<Double, FinalListNote> treeMap, String renderName, ChordForm outputCF, boolean copyOverRule){
		// for use in multitabbed fpPanels for the final result which is a combination of GenOutputs so no single FeaturePicker
		this.outputCF = outputCF;
		this.renderName = renderName;
		this.flnMap = treeMap;
		this.flnList = makeFinalNoteList(flnMap);
		this.lc = makeLiveClip(flnMap, outputCF);
//		this.fp = fp;
		this.copyOverRule = copyOverRule;
	}
	public boolean copyOverRule(){
		return copyOverRule;
	}

	public String renderName() {
		return renderName;
	}

	public LiveClip lc() {
		return lc;
	}

	public TreeMap<Double, FinalListNote> flnMap() {
		return flnMap;
	}

	public ChordForm outputCF() {
		return outputCF;
	}

	public ArrayList<FinalListNote> flnList() {
		return flnList;
	}

	private ArrayList<FinalListNote> makeFinalNoteList(TreeMap<Double, FinalListNote> outputTreeMap) {
		ArrayList<FinalListNote> flnList = new ArrayList<FinalListNote>();
		for (Double d : outputTreeMap.keySet()) {
			flnList.add(outputTreeMap.get(d));
		}
		return flnList;
	}

	private LiveClip makeLiveClip(TreeMap<Double, FinalListNote> outputTreeMap, ChordForm cf) {
		LiveClip outputClip = new LiveClip(0, 0);
		for (FinalListNote fln : outputTreeMap.values()) {
			outputClip.addNoteList(fln.getNoteList());
		}
		outputClip.loopEnd = cf.totalLength();
		outputClip.length = outputClip.loopEnd;
		outputClip.name = renderName;
		outputClip.signatureNumerator = cf.getTimeSignature()[0];
		outputClip.signatureDenominator = cf.getTimeSignature()[1];
		return outputClip;
	}

	public FeaturePicker fp() {
		return fp;
	}

	public boolean isSameAs(GenOutput tempOutput) {
//		System.out.println("GenOutput.isSameAs:");
//		System.out.println(lc.toString());
//		System.out.println(tempOutput.lc.toString());
		if (lc.noteList.size() == tempOutput.lc().noteList.size()){
			for (int i = 0; i < lc.noteList.size(); i++){
				LiveMidiNote lmn1 = lc.noteList.get(i);
				LiveMidiNote lmn2 = tempOutput.lc().noteList.get(i);
				if (!(lmn1.note == lmn2.note && lmn1.length == lmn2.length && lmn1.velocity == lmn2.velocity && lmn1.position == lmn2.position)){
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
		
	}
	public String finalNoteListToString(){
		String str = "GenOutput.finalNoteList:-\n";
		for (FinalListNote fln: flnList){
			str += fln.toString() + "\n";
		}
		
		return str;
	}
	public boolean hasFeaturePicker() {
		return hasFeaturePicker;
	}
}
