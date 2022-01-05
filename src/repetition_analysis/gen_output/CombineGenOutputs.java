package repetition_analysis.gen_output;

import java.util.ArrayList;
import java.util.TreeMap;

import DataObjects.incomplete_note_utils.FinalListNote;

public class CombineGenOutputs {
	
	private static final double MINIMUM_ACCEPTABLE_DURATION = 0.05;

	// class that combines a list of GenOutputs using a copyover rule
	
//	public static GenOutput getFinalGenOutput(ArrayList<GenOutput> goList, boolean copyOverStartRule, boolean copyOverEndRule){
//		TreeMap<Double, FinalListNote> map = new TreeMap<Double, FinalListNote>();
//		addGoListToMap(map, goList, )
//	}
	public static GenOutput getFinalGenOutput(ArrayList<GenOutput> goList, boolean copyOverStartRule, boolean copyOverEndRule){
		TreeMap<Double, FinalListNote> map = new TreeMap<Double, FinalListNote>();
		for (Double d: goList.get(0).flnMap().keySet()){
			map.put(d, goList.get(0).flnMap().get(d));
		}
//		System.out.println("map:----------");
//		for (Double d: map.keySet()){
//			System.out.println(d + ": " + map.get(d).toString());
//		}
		for (int i = 1; i < goList.size(); i++){
			TreeMap<Double, FinalListNote> tempMap = goList.get(i).flnMap();
//			System.out.println("tempMap atill to add:----------");
//			for (Double d: tempMap.keySet()){
//				System.out.println(d + ": " + tempMap.get(d).toString());
//			}
			for (Double d: tempMap.keySet()){
				FinalListNote testNote = tempMap.get(d);
				boolean copyTestNote = true;
				
				TreeMap<Double, FinalListNote> overlapEndMap = getOverlapEndMap(testNote, map);
				if (overlapEndMap.size() > 0){
					if (copyOverEndRule){
						for (Double oeMapKey: overlapEndMap.keySet()){
							FinalListNote adjustNote = map.get(oeMapKey);
							adjustNote.setDuration(testNote.position() - adjustNote.position());
						}
						copyTestNote = true;
					} else {
						copyTestNote = false;
					}
				}
				
				TreeMap<Double, FinalListNote> overlapStartMap = getOverlapStartMap(testNote, map);
				if (overlapStartMap.size() > 0){
					if (copyOverStartRule){
						for (Double oeMapKey: overlapStartMap.keySet()){
							map.remove(oeMapKey);
						}
						copyTestNote = true;
					} else {
						Double oeMapKey = overlapStartMap.firstKey();
						FinalListNote adjustNote = map.get(oeMapKey);
						testNote.setDuration(adjustNote.position() - testNote.position());
						
						if (testNote.duration() < MINIMUM_ACCEPTABLE_DURATION){
							copyTestNote = false;
						} else {
							copyTestNote = true;
						}
						
					}
				}
				
//				TreeMap<Double, FinalListNote> overlapStartAnEndMap = getOverlapStartAndEndMap(testNote, map);
//				if (overlapEndMap.size() > 0){
//					if (copyOverStartRule){
//						for (Double oeMapKey: overlapEndMap.keySet()){
//							map.remove(oeMapKey);
//						}
//						copyTestNote = true;
//					} else {
//						for (Double oeMapKey: overlapEndMap.keySet()){
//							FinalListNote adjustNote = map.get(oeMapKey);
//							testNote.setDuration(adjustNote.position() - testNote.position());
//						}
//						copyTestNote = true;
//					}
//				}
				
				if (copyTestNote) map.put(d, testNote);
				
			}
		}
		GenOutput go = new GenOutput(map, goList.get(0).renderName(), goList.get(0).outputCF(), goList.get(0).copyOverRule());
		
		return go;		
	}

	private static TreeMap<Double, FinalListNote> getOverlapStartAndEndMap(FinalListNote testNote,
			TreeMap<Double, FinalListNote> map) {
		TreeMap<Double, FinalListNote> newMap = new TreeMap<Double, FinalListNote>();
		for (FinalListNote fln: map.values()){
			if (fln.position() >= testNote.position() && fln.endPosition() < testNote.endPosition()){
				newMap.put(fln.position(), fln);
			}
		}
		return newMap;
	}

	private static TreeMap<Double, FinalListNote> getOverlapStartMap(FinalListNote testNote,
			TreeMap<Double, FinalListNote> map) {
		TreeMap<Double, FinalListNote> newMap = new TreeMap<Double, FinalListNote>();
		for (FinalListNote fln: map.values()){
			if (fln.position() >= testNote.position() && fln.position() < testNote.endPosition()){
				newMap.put(fln.position(), fln);
			}
		}
		return newMap;
	}

	private static TreeMap<Double, FinalListNote> getOverlapEndMap(FinalListNote testNote,
			TreeMap<Double, FinalListNote> map) {
		TreeMap<Double, FinalListNote> newMap = new TreeMap<Double, FinalListNote>();
		for (FinalListNote fln: map.values()){
			if (testNote.position() > fln.position() && testNote.position() < fln.endPosition()){
				newMap.put(fln.position(), fln);
			}
		}
		return newMap;
	}

}
