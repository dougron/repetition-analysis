package tests;

import java.util.ArrayList;
import java.util.TreeMap;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.incomplete_note_utils.FinalListNote;
import ResourceUtils.ChordForm;
import acm.program.ConsoleProgram;
import repetition_analysis.gen_output.CombineGenOutputs;
import repetition_analysis.gen_output.GenOutput;

public class GenOutputConsoleTest extends ConsoleProgram {

	
	public void run(){
		setSize(700, 700);
		ChordForm outputCF = makeOutputCF();
		println(outputCF.toString());
		TreeMap<Double, FinalListNote> map1 = makeMap1();
		GenOutput go1 = new GenOutput(map1, "poopsypie", outputCF, true);
		TreeMap<Double, FinalListNote> map2 = makeMap2();
		GenOutput go2 = new GenOutput(map2, "poopsypie", outputCF, true);
		
		ArrayList<GenOutput> goList = new ArrayList<GenOutput>();
		goList.add(go1);
		goList.add(go2);
		boolean copyOverStartRule = false;
		boolean copyOverEndRule = true;
		
		println("\n\n\n\n\n================================================");
		
		for (GenOutput gg: goList){
			println(gg.finalNoteListToString());
		}
		
		GenOutput finalGo = CombineGenOutputs.getFinalGenOutput(goList, copyOverStartRule, copyOverEndRule);
		
		println("copyOverStartRule=" + copyOverStartRule + " copyOverEndRule=" + copyOverEndRule);
		println(finalGo.finalNoteListToString());
	}

	private ChordForm makeOutputCF() {
		LiveClip lc = new LiveClip(0, 0);
		double len = 4.0;
		lc.addNote(48, 0.0, len, 100, 0);
		lc.addNote(52, 0.0, len, 100, 0);
		lc.addNote(55, 0.0, len, 100, 0);
		lc.loopStart = 0.0;
		lc.loopEnd = 4.0;
		lc.length = 4.0;
		
		return new ChordForm(lc);
	}

	private TreeMap<Double, FinalListNote> makeMap1() {
		TreeMap<Double, FinalListNote> map = new TreeMap<Double, FinalListNote>();
		double pos = 1.0;
		FinalListNote note1 = new FinalListNote(pos);
		note1.addNote(52);
		note1.setDuration(2.0);
		map.put(pos, note1);
		return map;
	}
	private TreeMap<Double, FinalListNote> makeMap2() {
		TreeMap<Double, FinalListNote> map = new TreeMap<Double, FinalListNote>();
		double pos = 1.0;
		FinalListNote note1 = new FinalListNote(pos);
		note1.addNote(52);
		note1.setDuration(3.0);
		map.put(pos, note1);
		return map;
	}
}
