package repetition_analysis.feature_handler;

import repetition_analysis.feature_chunk.FeatureChunk;

public interface FeatureHandler {

	public FeatureInfo getInfo(FeatureChunk fc);
	public String name();
	public String shortToString(FeatureChunk fc);
	public boolean isUsable();
	public boolean needsAnalysisChordForm();
	public boolean needsOutputChordForm();
	public int type();
	public boolean needsContourFeature();
	public boolean isPolyphonic();
	public String shortName();
	public String description();
}
