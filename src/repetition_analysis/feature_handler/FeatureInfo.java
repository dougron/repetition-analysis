package repetition_analysis.feature_handler;

import repetition_analysis.feature_chunk.FeatureChunk;

public class FeatureInfo {

//	public String infoString;		// this may be redundant
	public double[][] doubleArr;
	public double[] doubleArrOne;
	public int featureID;
	public boolean isInt;
	public int[][] intArr;
	public int[] intArrOne;
	public boolean oneDimension = true;
	private boolean isNull = false;

	public FeatureInfo(double[][] doubleArr, int featureID){
//		this.infoString = infoString;
		this.doubleArr = doubleArr;
		this.featureID = featureID;
		this.isInt = false;
		oneDimension = false;
	}
;	public FeatureInfo(int[][] intArr, int featureID){
//		this.infoString = infoString;
		this.intArr = intArr;
		this.featureID = featureID;
		this.isInt = true;
		oneDimension = false;
	}
	public FeatureInfo(int[] intArr, int featureID){
		this.intArrOne = intArr;
		this.featureID = featureID;
		this.isInt = true;
		oneDimension = true;
	}
	public FeatureInfo(double[] doubleArr, int featureID){
		this.doubleArrOne = doubleArr;
		this.featureID = featureID;
		this.isInt = false;
		oneDimension = true;
	}
	public FeatureInfo(){
		this.isNull  = true;
	}
	public String toString(){
		String str = "FeatureInfo: isNull=" + isNull;
		if (!isNull){
			str += " " + FeatureChunk.getFeatureName(featureID) + " isInt=" + isInt;
			
			if (isInt){
				str += "\n  as Value:";
			} else {
				str += "\n  as Value:";
			}
			str += getFeatureString();
		}
		return str;
	}
	public String getFeatureString() {
		if (isInt){
			if (oneDimension){
				return intArrOneAsValueString();
			} else {
				return intArrAsValueString();
			}
			
		} else {
			if (oneDimension){
				return doubleArrOneAsValueString();
			} else {
				return doubleArrAsValueString();
			}
		}
	}
	public double getDouble(int index){		// index wraps around if arr is not long enough
		return doubleArrOne[index % doubleArrOne.length];
	}
	public int getInt(int index){
		return intArrOne[index % intArrOne.length];
	}
	public int[] getIntArr(int index) {
		return intArr[index % intArr.length];
	}
	public int beforeOrAfter(FeatureInfo fi){
		int length = 0;
		if (featureArrayLength() > fi.featureArrayLength()){
			length = featureArrayLength();
		} else {
			length = fi.featureArrayLength();
		}
		//System.out.println("length=" + length);
		double mySortingNumber = getSortingNumber(length);
		double vistorSortingNumber = fi.getSortingNumber(length);
		//System.out.println(getFeatureString() + ", " + mySortingNumber + " vs " + fi.getFeatureString() + ", " + vistorSortingNumber);
		if (mySortingNumber > vistorSortingNumber) return 1;
		if (mySortingNumber < vistorSortingNumber) return -1;
		return 0;
	}
	public int beforeOrAfterLastToFirst(FeatureInfo fi) {
//		int length = 0;
//		if (featureArrayLength() > fi.featureArrayLength()){
//			length = featureArrayLength();
//		} else {
//			length = fi.featureArrayLength();
//		}
//		//System.out.println("length=" + length);
		double mySortingNumber = getLastToFirstSortingNumber();
		double vistorSortingNumber = fi.getLastToFirstSortingNumber();
		//System.out.println(getFeatureString() + ", " + mySortingNumber + " vs " + fi.getFeatureString() + ", " + vistorSortingNumber);
		if (mySortingNumber > vistorSortingNumber) return 1;
		if (mySortingNumber < vistorSortingNumber) return -1;
		return 0;
	}

	public int featureArrayLength(){
		if (oneDimension){
			if (isInt){
				return intArrOne.length;
			} else {
				return doubleArrOne.length;
			}
		} else {
			if (isInt){
				return intArr.length;
			} else {
				return doubleArr.length;
			}
		}
	}
	public double getSortingNumber(int zeroCount){
		double sum = 0.0;
		if (oneDimension){
			if (isInt){
				for (int i: intArrOne){
					sum += i * (Math.pow(10, zeroCount));
					zeroCount--;
				}
			} else {
				for (double d: doubleArrOne){
					sum += d * (Math.pow(10, zeroCount));
					zeroCount--;
				}
			}
		} else {
			if (isInt){
				for (int[] iarr: intArr){
					sum += (iarr[0] + iarr[1] / 10) * Math.pow(10, zeroCount);
					zeroCount--;
				}
			} else {
				for (double[] darr: doubleArr){
					sum += (darr[0] + darr[1] / 10) * Math.pow(10, zeroCount);
					zeroCount--;
				}
			}
		}
		return sum;
	}
	private double getLastToFirstSortingNumber() {
		int zeroCount = 1;
		double sum = 0.0;
		if (oneDimension){
			if (isInt){
				for (int i: intArrOne){
					sum += i * (Math.pow(10, zeroCount));
					zeroCount++;
				}
			} else {
				for (double d: doubleArrOne){
					sum += d * (Math.pow(10, zeroCount));
					zeroCount++;
				}
			}
		} else {
			if (isInt){
				for (int[] iarr: intArr){
					sum += (iarr[0] + iarr[1] / 10) * Math.pow(10, zeroCount);
					zeroCount++;
				}
			} else {
				for (double[] darr: doubleArr){
					sum += (darr[0] + darr[1] / 10) * Math.pow(10, zeroCount);
					zeroCount++;
				}
			}
		}
		return sum;
	}
	
	private String doubleArrOneAsValueString() {
		String str = "";
		for (double d: doubleArrOne){
			double dd = Math.round(d * 100) / 100.0;
			str += dd + "_";
		}
		return str;
	}
	private String intArrOneAsValueString() {
		String str = "";
		for (int i: intArrOne){
			str += i + "_";
		}
		return str;
	}
	private String intArrAsValueString() {
		String str = "";
		for (int[] iarr: intArr){
			for (int index = 0; index < iarr.length; index++){
				str += iarr[index];
				if (index < iarr.length - 1){
					str += ",";
				}
			}
			str += "_";
		}
		return str;
	}
	private String doubleArrAsValueString() {
		// doubles are rounded to 2 decimal places for length issues
		String str = "";
		for (double[] darr: doubleArr){
			for (int index = 0; index < darr.length; index++){
				str += (Math.round(darr[index] * 100) / 100);
				if (index < darr.length - 1){
					str += ",";
				}
			}
			str += "_";
		}
		return str;
	}
	
	

}
