package repetition_analysis.feature_chunk;

import java.util.ArrayList;

public class ConstrainedFeatureChunkList {

	// a class to wrap a featureChunkList contraned to a position range
	
	public double start;
	public double end;
	public ArrayList<FeatureChunkWrapper> chunkList;
	public double[] gapArray;
	
	public ConstrainedFeatureChunkList(ArrayList<FeatureChunkWrapper> list, double start, double end){
		this.start = start;
		this.end = end;
		chunkList = new ArrayList<FeatureChunkWrapper>();
		for (FeatureChunkWrapper fcw: list){
			if (fcw.position() >= start && fcw.position() < end){
				chunkList.add(fcw);
			}
		}
		gapArray = gapArray();
	}

	public String repeatsToString() {
		String str = "";
		for (FeatureChunkWrapper fcw: chunkList){
			str += fcw.featureChunk().position() + ",";
		}
		return str;
	}
	private double[] gapArray(){
		// the gaps between each repetition
		if (chunkList.size() > 0){
			double[] arr = new double[chunkList.size() - 1];
			for (int i = 0; i < arr.length; i++){
				arr[i] = chunkList.get(i + 1).featureChunk().position() - chunkList.get(i).featureChunk().position();
			}
			return arr;
		} else {
			return new double[]{};
		}
		
	}

	public String gapsToString() {
		String str = "";
		for (double d: gapArray()){
			str += d + ",";
		}
		return str;
	}
	
	
}
