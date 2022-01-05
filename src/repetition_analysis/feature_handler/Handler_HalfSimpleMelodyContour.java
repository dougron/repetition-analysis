package repetition_analysis.feature_handler;

import repetition_analysis.feature_chunk.FeatureChunk;

public class Handler_HalfSimpleMelodyContour extends Handler implements FeatureHandler {

	private Handler_AbsoluteTopNoteInterval intervalHandler = new Handler_AbsoluteTopNoteInterval();
	
	// this now describes any interval larger than 4 or -4 as large
	// based on melodic implication/realization theory of Narmour
	
	public Handler_HalfSimpleMelodyContour(){
		super("HALF_SIMPLE_MELODY_CONTOUR", "cnt_hsc", false, true, Handler.CONTOUR_TYPE);
		setDescription("Half simple melody contour. Range (-2, 2)");
	}
	public FeatureInfo getInfo(FeatureChunk fc){
		return new FeatureInfo(getIntArr(fc), FeatureChunk.HALF_SIMPLE_MELODY_CONTOUR);
	}
	private int[] getIntArr(FeatureChunk fc) {
		FeatureInfo fi = intervalHandler.getInfo(fc);
		int[] arr = new int[fi.intArrOne.length];
		for (int i = 0; i < fi.intArrOne.length; i++){
			int x = fi.intArrOne[i];
			if (x == 0){
				arr[i] = 0;
			} else if (x > 0){
				if (x < 5){
					arr[i] = 1;
				} else {
					arr[i] = 2;
				}
			} else {
				if (x > -5){
					arr[i] = -1;
				} else {
					arr[i] = -2;
				}
			}
		}
		return arr;
	}
	private int[] getIntArrOLD(FeatureChunk fc) {
		// this was a relative calculation within the FeatureChunk
		// 1/-1 was relatively small 2/-2 was relatively large
		// uselessresults when cell size = 1. Everything was large
		FeatureInfo fi = intervalHandler.getInfo(fc);
		int largest = 0;
		for (int i: fi.intArrOne){
			//System.out.println(i);
			int absi = Math.abs(i);
			if (absi > largest) largest = absi;
		}
		int[] arr = new int[fi.intArrOne.length];
		for (int index = 0; index < fi.intArrOne.length; index++){
			double d = (double)fi.intArrOne[index] * 2.0 / (double)largest;
			//System.out.println(d);
			int x = (int)(Math.abs(d) + 0.5) * (int)Math.signum(d);
			//System.out.println(x);
			arr[index] = x;
		}
		return arr;
	}

	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();

	}

}
