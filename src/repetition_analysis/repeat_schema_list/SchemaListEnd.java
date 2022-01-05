package repetition_analysis.repeat_schema_list;
import java.util.ArrayList;

import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_chunk.FeatureChunkWrapper;

public class SchemaListEnd implements SchemaListObject {

	private ArrayList<SchemaListObject> schemaList;
//	private RepeatSchemaList parent;
//	private boolean firstTimeUse = true;
	private SchemaListObject mostRecentAddition = this;
	private RepeatSchemaList rsl;

	public SchemaListEnd(ArrayList<SchemaListObject> schemaList2, RepeatSchemaList parent){
		this.schemaList = schemaList2;
		this.rsl = parent;
	}

	@Override
	public void add(FeatureChunkWrapper fcw) {
		//System.out.println(fcw.toString() + " arrived at listEnd and new Schema made.");
		FeatureChunkRepeatSchema fcrs = new FeatureChunkRepeatSchema(fcw, mostRecentAddition, rsl);
		schemaList.add(0, fcrs);
		mostRecentAddition = fcrs;
		rsl.firstObj = fcrs;
	}

	@Override
	public int count() {
		return 0;			// not a real repeatobject
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
