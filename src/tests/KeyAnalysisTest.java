package tests;


import java.util.ArrayList;

import com.cycling74.max.Atom;
import com.cycling74.max.DataTypes;
import com.cycling74.max.MaxObject;

import ChordScaleDictionary.ChordScaleDictionary;
import ChordScaleDictionary.chord_analysis.ChordAnalysisObject;
import ChordScaleDictionary.key_utils.Key;
import ChordScaleDictionary.key_utils.KeyArea;
import ChordScaleDictionary.key_utils.KeyScorer;
import ChordScaleDictionary.key_utils.KeyScorerArray;



public class KeyAnalysisTest extends MaxObject {
	
	ChordScaleDictionary csd = new ChordScaleDictionary();
	private ArrayList<ArrayList<Integer>> chordList = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ChordAnalysisObject> caoList = new ArrayList<ChordAnalysisObject>();
	private ArrayList<Integer> buffy = new ArrayList<Integer>();
	private int messOutlet = 0;
	private static final String chordDisplayMess = "chorddisplay";
	private KeyScorer ks;
	private ArrayList<KeyArea> kaList;
	KeyScorerArray ksa; 

	public KeyAnalysisTest(){
		setMaxInlets();


		post("KeyAnalysisTest initialized....!");
	}
//	outputBuffy();
//	clearBuffyAnalysis();
//	clearGrid();
	public void note(int i){
		buffy.add(i);
		outputBuffy();
		outputBuffyAnalysis();
	}

	public void addBuffy(){
		addBuffyToChordList();
		addCAO();
		outputChordList();
		buffy.clear();
		outputBuffy();
		
	}

	public void clearBuffy(){
		buffy.clear();
		outputBuffy();
	}
	public void clearChords(){
		chordList.clear();
		caoList.clear();
		outputChordList();
		ksa = new KeyScorerArray(new Key[]{csd.keyList.get(0)});
	}
	
	

	// privates ========================================================	
	private void addCAO() {
		ChordAnalysisObject cao = new ChordAnalysisObject(chordList.get(chordList.size() - 1));
		caoList.add(cao);
		makeKeyScorerArray();
		
	}
	private void makeKeyScorerArray() {
		ksa = new KeyScorerArray(new Key[]{csd.keyList.get(0)});	
		for (ChordAnalysisObject cao: caoList){
			ksa.newChord(cao);
		}
		ksa.makeKeyAreas();
	}
	private void outputChordList() {
		clearGrid();
		setCols(chordList.size() + 1);
		ArrayList<String[]> ksaList = ksa.getChordAnalysisStringArrayList();
		setRows(ksaList.size() + 1);
		//addChords();
		addKeyScorerArrayOutput(ksaList);
	}
	private void addKeyScorerArrayOutput(ArrayList<String[]> ksaList) {
		int row = 0;
		for (String[] strArr: ksaList){
			int col = 0;
			for (String str: strArr){
				outlet(messOutlet, new Atom[]{
						Atom.newAtom(chordDisplayMess),
						Atom.newAtom("set"),
						Atom.newAtom(col),
						Atom.newAtom(row),
						Atom.newAtom(str),
				});
				col++;
			}
			row++;
		}
		
	}
	private void addKeyAreas() {
		int row = 1;
		for (KeyArea ka: kaList){
			
		}
		
	}
	private void clearGrid() {
		setCols(1);
		setRows(1);
		outlet(messOutlet, new Atom[]{
				Atom.newAtom(chordDisplayMess),
				Atom.newAtom("clear"),
		});
		
	}
	private void addChords() {
		int index = 1;
		for (ArrayList<Integer> iList: chordList){
			outlet(messOutlet, new Atom[]{
					Atom.newAtom(chordDisplayMess),
					Atom.newAtom("set"),
					Atom.newAtom(index),
					Atom.newAtom(0),
					Atom.newAtom(new ChordAnalysisObject(iList).chordToString()),
			});
			index++;
		}
		
	}
	private void setRows(int i) {
		outlet(messOutlet, new Atom[]{
				Atom.newAtom(chordDisplayMess),
				Atom.newAtom("rows"),
				Atom.newAtom(i)
		});
		
	}
	private void setCols(int i) {
		outlet(messOutlet, new Atom[]{
				Atom.newAtom(chordDisplayMess),
				Atom.newAtom("cols"),
				Atom.newAtom(i)
		});
		
	}
	private void addBuffyToChordList() {
		ArrayList<Integer> iList = new ArrayList<Integer>();
		for (Integer i: buffy){
			iList.add(i);
		}
		chordList.add(iList);
		
	}

	private void outputBuffyAnalysis() {
		ChordAnalysisObject cao = new ChordAnalysisObject(buffy);
		Atom[] mess = new Atom[2];
		mess[0] = Atom.newAtom("bufferanalysis");
		mess[1] = Atom.newAtom(cao.chordToString());
		outlet(messOutlet, mess);
		
	}
	private void clearBuffyAnalysis(){
		outlet(messOutlet, new Atom[]{
				Atom.newAtom("bufferanalysis"),
				Atom.newAtom("...")
		});
	}
	private void outputBuffy(){
		Atom[] mess = new Atom[buffy.size() + 1];
		mess[0] = Atom.newAtom("notebuffer");
		int index = 1;
		for (Integer i: buffy){
			mess[index] = Atom.newAtom(i);
			index++;
		}
		outlet(messOutlet , mess);
	}
	private void setMaxInlets(){

		declareInlets(new int[]{
				DataTypes.ALL,  
				DataTypes.INT}
		);
		declareOutlets(new int[]{ 
				DataTypes.ALL, 
				DataTypes.ALL}
		);
		setInletAssist(new String[]{
				"all inputs",
				"other inputs"}
		);
		setOutletAssist(new String[]{
				"all out",
				"comment out"}
		);
	}
}
