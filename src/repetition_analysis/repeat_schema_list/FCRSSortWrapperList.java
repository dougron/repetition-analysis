package repetition_analysis.repeat_schema_list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import DataObjects.ableton_live_clip.LiveMidiNote;
import DataObjects.combo_variables.IntAndDouble;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchemaSortWrapper;

public class FCRSSortWrapperList{

	private ArrayList<FeatureChunkRepeatSchemaSortWrapper> list;
	private double[] doubleArray;
	
	public FCRSSortWrapperList(double[] darr){
		doubleArray = darr;
		list = new ArrayList<FeatureChunkRepeatSchemaSortWrapper>();
	}
	public void add(FeatureChunkRepeatSchemaSortWrapper sw){
		list.add(sw);
	}
	public FeatureChunkRepeatSchemaSortWrapper get(int i){
		if (i < list.size() && i > -1){
			return list.get(i);
		} else {
			return null;
		}
			
	}
	public int size(){
		return list.size();
	}
	public int featureArrayLength(){
		return doubleArray.length;
	}
	public double getFirstFeature(){
		// this will only return the 
		if (doubleArray.length > 0){
			return doubleArray[0];
		} else {
			return 0.0;
		}
	}
	public double[] getDoubleArray(){
		return doubleArray;
	}
	public String keyArrayToString(){
		// String version of the double array for printing etc..
		String str = "";
		for (double d: doubleArray){
			str += d + ",";
		}
		
		return str;
	}
	public String relativeBarPosToString() {
		String str = "";
		if (list.size() > 0){
			str += list.get(0).relativePosToString();
		}
		
		return str;
	}
	public String firstBarPosToString() {
		
		if (list.size() > 0){
			return list.get(0).firstBarPosToString();
		}
		
		return new IntAndDouble(-1, 0.0).toString();
	}
	public void clear(){
		list.clear();
	}
	public Iterator<FeatureChunkRepeatSchemaSortWrapper> iterator() {

		return list.iterator();
	}
	public static Comparator<FCRSSortWrapperList> positionComparator = new Comparator<FCRSSortWrapperList>(){
		public int compare(FCRSSortWrapperList swl1, FCRSSortWrapperList swl2){
			int index = 0;
			while (true){
				if (swl1.doubleArray.length == index){
					if (swl2.doubleArray.length == index){
						return 0;
					} else {
						return -1;
					}
				} else {
					if (swl2.doubleArray.length == index){
						return 1;
					} else {
						if (swl1.doubleArray[index] < swl2.doubleArray[index]) return -1;
						if (swl1.doubleArray[index] > swl2.doubleArray[index]) return 1;
					}
				}
				
				index++;
			}
		}
	};
	public static Comparator<FCRSSortWrapperList> sizeAndPosComparator = new Comparator<FCRSSortWrapperList>(){
		public int compare(FCRSSortWrapperList swl1, FCRSSortWrapperList swl2){
			if (swl1.size() < swl2.size()) return 1;
			if (swl1.size() > swl2.size()) return -1;
			int index = 0;
			while (true){
				if (swl1.doubleArray.length == index){
					if (swl2.doubleArray.length == index){
						return 0;
					} else {
						return -1;
					}
				} else {
					if (swl2.doubleArray.length == index){
						return 1;
					} else {
						if (swl1.doubleArray[index] < swl2.doubleArray[index]) return -1;
						if (swl1.doubleArray[index] > swl2.doubleArray[index]) return 1;
					}
				}
				
				index++;
			}
		}
	};
	public static Comparator<FCRSSortWrapperList> featureLengthAndPosComparator = new Comparator<FCRSSortWrapperList>(){
		public int compare(FCRSSortWrapperList swl1, FCRSSortWrapperList swl2){
			if (swl1.doubleArray.length < swl2.doubleArray.length) return 1;
			if (swl1.doubleArray.length > swl2.doubleArray.length) return -1;
			int index = 0;
			while (true){
				if (swl1.doubleArray.length == index){
					if (swl2.doubleArray.length == index){
						return 0;
					} else {
						return -1;
					}
				} else {
					if (swl2.doubleArray.length == index){
						return 1;
					} else {
						if (swl1.doubleArray[index] < swl2.doubleArray[index]) return -1;
						if (swl1.doubleArray[index] > swl2.doubleArray[index]) return 1;
					}
				}
				
				index++;
			}
		}
	};
	public static Comparator<FCRSSortWrapperList> featureLengthComparator = new Comparator<FCRSSortWrapperList>(){
		public int compare(FCRSSortWrapperList swl1, FCRSSortWrapperList swl2){
			if (swl1.doubleArray.length < swl2.doubleArray.length) return 1;
			if (swl1.doubleArray.length > swl2.doubleArray.length) return -1;
			return 0;
		}
	};

	public void sortList(Comparator<FeatureChunkRepeatSchemaSortWrapper> comp) {
		Collections.sort(list, comp);
		
	}
	public FeatureChunkRepeatSchemaSortWrapper getFirstSortWrapper() {
		if (list.size() > 0) return list.get(0);
		return null;
	}

	
	
}
