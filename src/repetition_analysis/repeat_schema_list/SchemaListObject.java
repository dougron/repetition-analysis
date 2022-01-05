package repetition_analysis.repeat_schema_list;

import repetition_analysis.feature_chunk.FeatureChunkWrapper;

public interface SchemaListObject {

	
	public void add(FeatureChunkWrapper fcw);
	public int count();
	public String toString();
	public String toShortString();
	public String repeatsToString();
	public String toShortString(double d, double e);
}
