package repetition_analysis.repetition_generator;
import java.util.ArrayList;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.incomplete_note_utils.FinalListNote;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.repeat_schema_list.SchemaListObject;

/*
 *  
 */
public interface RepetitionGenerator {
	

	public void addRepetitionSchema(FeatureChunkRepeatSchema fcrs);
	public void addRepetitionSchema(SchemaListObject slo); 
	public void addFeatureChunk(FeatureChunkRepeatSchema fcrs);	// only using the FeatureChuink aspect of this
	public LiveClip getLiveClip();
	public boolean isFinalNoteListComplete();
	public String toString();
	public void addFeatureChunk(SchemaListObject slo2);
	public ArrayList<FinalListNote> getFinalNoteList();
}
