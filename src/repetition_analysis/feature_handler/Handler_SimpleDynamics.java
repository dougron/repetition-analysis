package repetition_analysis.feature_handler;

import repetition_analysis.feature_chunk.FeatureChunk;

/*
 * low medium high. needs a centre value for final rendering.....
 */
public class Handler_SimpleDynamics extends Handler implements FeatureHandler {

	public Handler_SimpleDynamics(){
		super("SIMPLE_DYNAMICS", "dyn_sim", false, false, Handler.DYNAMICS_TYPE);
		setDescription("Simple 3 value dynamics: loud = 2, medium = 1, soft = 0");
	}
	public FeatureInfo getInfo(FeatureChunk fc){
		return new FeatureInfo(getIntArr(fc), FeatureChunk.SIMPLE_DYNAMICS);
	}
	private int[] getIntArr(FeatureChunk fc) {
		int[] arr = new int[fc.chunkList.size()];
		for (int i = 0; i < fc.chunkList.size(); i++){
			if (fc.chunkList.get(i).averageVelocity() <= fc.lc().lowVelocity()){
				arr[i] = 0;
			} else if (fc.chunkList.get(i).averageVelocity() >= fc.lc().highVelocity()){
				arr[i] = 2;
			} else {
				arr[i] = 1;
			}
		}
		return arr;
	}
	private int[] getIntArrOLD(FeatureChunk fc) {
		// this does a relative low medium high dynamic based on the FeatureChunk contents
		// replaced by one that considers content of entire LiveClip 
		int[] arr = getAbsoluteDynamicsArray(fc);
		int low = getLowestValue(arr);
		for (int i = 0; i < arr.length; i++){
			arr[i] = arr[i] - low;
		}
		int high = getHighestValue(arr);
		for (int i = 0; i < arr.length; i++){
			arr[i] = (int)(((double)arr[i] * 2 / (double)high) + 0.5);
		}
		return arr;
	}

	private int getHighestValue(int[] arr) {
		int i = -1000;		// arbly low
		for (int x: arr){
			if (x > i) i = x;
		}
		return i;
	}
	private int getLowestValue(int[] arr) {
		int i = 1000;		// arbly high
		for (int x: arr){
			if (x < i) i = x;
		}
		return i;
	}
	private int[] getAbsoluteDynamicsArray(FeatureChunk fc) {
		int[] arr = new int[fc.chunkList.size()];
		for (int i = 0; i < fc.chunkList.size(); i++){
			arr[i] = fc.chunkList.get(i).averageVelocity();
		}
		return arr;
	}
	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();

	}
	public static final int LOW_DYNAMIC = 48;
	public static final int MEDIUM_DYNAMIC = 65;
	public static final int HIGH_DYNAMIC = 96;
	public static final int[] DEFAULT_DYNAMIC = new int[]{
			LOW_DYNAMIC, MEDIUM_DYNAMIC, HIGH_DYNAMIC
	};
}
