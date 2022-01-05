package repetition_analysis.feature_chunk;

import java.util.ArrayList;
import java.util.Comparator;

import DataObjects.combo_variables.IntAndDouble;
import DataObjects.combo_variables.IntAndString;

public class FeatureChunkRepeatSchemaSortWrapper {

	// class to wrap a FeatureChunkRepeatSchema object and deliver all sorts of sorting information
	// for the purposes of making the master repeat schema list make more sense
	
	public FeatureChunkRepeatSchema fcrs;
	private double start;
	private double end;
	private ArrayList<FeatureChunkWrapper> chunkList;
	private double[] posArray;
	private IntAndDouble[] relativePosArray;
	private double[] gapArray;
	private boolean usesAbsolutePosition;
	private int outputBarCount;
//	private boolean hasDoublePosArray = false;
//	private Double[] doublePosArray;
//	private boolean hasDoubleGapArray = false;
//	private Double[] doubleGapArray;
	private static double staticXOver = 0.5;	// seperation point in bar for bar relative positioning expressed as percentage

	public FeatureChunkRepeatSchemaSortWrapper(FeatureChunkRepeatSchema fcrs, double start, double end){
		//start and end are the bounds of the output chord form, to constrain the start positions list
		this.fcrs = fcrs;
		usesAbsolutePosition = true;
		this.start = start;
		this.end = end;
		makeChunkList();
		makePosArray();
//		makeRelativePosArray();
		makeGapArray();
//		System.out.println("FeatureChunkRepeatSchemaSortWrapper instantiator:\n" + toString());
	}

	public FeatureChunkRepeatSchemaSortWrapper(FeatureChunkRepeatSchema fcrs, int outputBarCount) {
		this.fcrs = fcrs;
		usesAbsolutePosition = false;
		this.outputBarCount = outputBarCount;
		makeChunkList();
		makePosArray();
//		makeRelativePosArray();
		makeGapArray();
//		System.out.println("FeatureChunkRepeatSchemaSortWrapper instantiator:\n" + toString());
	}

	public double[] posArray(){
		return posArray;
	}
	public double[] gapArray(){
		return gapArray;
	}
	public int size(){
		return posArray.length;
	}
	public double firstPos() {
		if (posArray.length > 0){
			return posArray[0];
		}
		return 0.0;
	}
	
// toString methods --------------------------------------------------------------------
	public String toString(){
		String str = fcrs.featureShortName() + " " + fcrs.featureString() + " size=" + size();
		str += " posArr:" + posArrayToString();
		str += " gapArr:" + gapArrayToString();
		str += "\nrelativePosArray:";
		for (Object o: relativePosArray){
			if (o == null){
				str += "null,";
			} else {
				if (o instanceof IntAndDouble){
					str += "(" + ((IntAndDouble)o).toString() + ")";
				}
			}
		}
		return str;
	}
	
	public String gapArrayToString() {
		String str = "";
		for (double d: gapArray){
			str += d + ",";
		}
		return str;
	}
	public String posArrayToString() {
		String str = "";
		for (double d: posArray){
			str += d + ",";
		}
		return str;
	}
	public String relativePosToString() {
		String str = "";
		for (IntAndDouble iad: relativePosArray){
			str += "(" + iad.toString() + ")";
		}
		return str;
	}
	public String firstBarPosToString(){
		// will not give a useful return under conditions other than when the list contains items starting on the same position
		return "(" + relativePosArray[0].toString() + ")";
	}
	
// comparators ----------------------------------------------------------------------------
	public static Comparator<FeatureChunkRepeatSchemaSortWrapper> sizeComparator = new Comparator<FeatureChunkRepeatSchemaSortWrapper>(){
		// largest to smallest
		public int compare(FeatureChunkRepeatSchemaSortWrapper sw1, FeatureChunkRepeatSchemaSortWrapper sw2){
			if (sw1.size() > sw2.size()) return -1;
			if (sw1.size() < sw2.size()) return 1;
			return 0;
		}
	};
	public static Comparator<FeatureChunkRepeatSchemaSortWrapper> posComparator = new Comparator<FeatureChunkRepeatSchemaSortWrapper>(){
		// largest to smallest
		public int compare(FeatureChunkRepeatSchemaSortWrapper sw1, FeatureChunkRepeatSchemaSortWrapper sw2){
			int index = 0;
			while (true){
				if (sw1.posArray.length == index){
					if (sw2.posArray.length == index){
						return 0;
					} else {
						return -1;
					}
				} else {
					if (sw2.posArray.length == index){
						return 1;
					} else {
						if (sw1.posArray[index] < sw2.posArray[index]) return -1;
						if (sw1.posArray[index] > sw2.posArray[index]) return 1;
					}
				}
				
				index++;
			}
		}
	};
	public static Comparator<FeatureChunkRepeatSchemaSortWrapper> sizeAndFirstPosComparator = new Comparator<FeatureChunkRepeatSchemaSortWrapper>(){
		public int compare(FeatureChunkRepeatSchemaSortWrapper sw1, FeatureChunkRepeatSchemaSortWrapper sw2){
			if (sw1.size() > sw2.size()) return -1;
			if (sw1.size() < sw2.size()) return 1;
			if (sw1.size() > 0){
				if (sw2.size() > 0){
					if (sw1.posArray[0] > sw2.posArray[0]) return 1;
					if (sw1.posArray[0] < sw2.posArray[0]) return -1;
					return 0;
				} else {
					return 1;
				}
			} else {
				if (sw2.size() > 0){
					return -1;
				} else {
					return 0;
				}
			}
			
		}
	};
	
	
// privates -----------------------------------------------------------------------------------------
//	private void makeRelativePosArray() {
//		relativePosArray = new IntAndDouble[posArray.length];
//		for (int i = 0; i < posArray.length; i++){
//			
//		}
//		
//	}
	private void makeGapArray() {
		if (chunkList.size() > 0){
			gapArray = new double[chunkList.size() - 1];
			for (int i = 0; i < posArray.length - 1; i++){
				gapArray[i] = posArray[i + 1] - posArray[i];
			}
		} else {
			gapArray = new double[]{};
		}
		
	}

	private void makePosArray() {
		posArray = new double[chunkList.size()];
		relativePosArray = new IntAndDouble[chunkList.size()];
		for (int i = 0; i < chunkList.size(); i++){
			FeatureChunkWrapper fcw = chunkList.get(i);
			posArray[i] = fcw.position();
			relativePosArray[i] = fcw.getXOverBarPosParam(staticXOver);
		}
	}

	private void makeChunkList() {
		chunkList = new ArrayList<FeatureChunkWrapper>();
		for (FeatureChunkWrapper fc: fcrs.featureChunkList()){
			if (usesAbsolutePosition){
				if (fc.position() >= start && fc.position() < end) chunkList.add(fc);
			} else {
				if (fc.getXOverBarPosParam(staticXOver).i < outputBarCount || 
						(fc.getXOverBarPosParam(staticXOver).i == outputBarCount && fc.getXOverBarPosParam(staticXOver).d < 0.0)){
					chunkList.add(fc);
				} 
			}
			
		}
	}
//	public Double[] posArrayAsDouble() {
//		if (!hasDoublePosArray){
//			doublePosArray = new Double[posArray.length];
//			for (int i = 0; i < posArray.length; i++){
//				doublePosArray[i] = posArray[i];
//			}
//			hasDoublePosArray = true;
//		}		
//		return doublePosArray;
//	}
//	public Double[] gapArrayAsDouble() {
//		if (!hasDoubleGapArray){
//			doubleGapArray = new Double[gapArray.length];
//			for (int i = 0; i < posArray.length; i++){
//				doubleGapArray[i] = gapArray[i];
//			}
//			hasDoubleGapArray = true;
//		}		
//		return doubleGapArray;
//	}
	

}
