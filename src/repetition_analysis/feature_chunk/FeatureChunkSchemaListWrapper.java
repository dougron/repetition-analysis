package repetition_analysis.feature_chunk;

import repetition_analysis.repeat_schema_list.SchemaListObject;

public class FeatureChunkSchemaListWrapper implements SchemaListObject {
	
	FeatureChunkRepeatSchema fcrs;
	
	public FeatureChunkSchemaListWrapper(FeatureChunkRepeatSchema fcrs){
		this.fcrs = fcrs;
	}



	@Override
	public void add(FeatureChunkWrapper fcw) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int count() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public String toShortString() {
		
		return toString();
	}



	@Override
	public String repeatsToString() {
		return toString();
	}



	@Override
	public String toShortString(double d, double e) {
		return toString();
	}

}
