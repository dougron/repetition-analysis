package repetition_analysis.repetition_generator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.incomplete_note_utils.FinalListNote;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.repeat_schema_list.SchemaListObject;

public class GenOne implements RepetitionGenerator {
	
	private ArrayList<FeatureChunkRepeatSchema> repSchemaList = new ArrayList<FeatureChunkRepeatSchema>();
	private ArrayList<FeatureChunkRepeatSchema> featureList = new ArrayList<FeatureChunkRepeatSchema>();
	private int repIndex = 0;
	private int featureIndex = 0;
	private TreeMap<Double, FinalListNote> finalNoteMap = new TreeMap<Double, FinalListNote>();

	public GenOne() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addRepetitionSchema(FeatureChunkRepeatSchema fcrs) {
		repSchemaList.add(fcrs);
		generateFinalNoteMap();
	}
	public void addRepetitionSchema(SchemaListObject slo) {
		if (slo instanceof FeatureChunkRepeatSchema){
			addRepetitionSchema((FeatureChunkRepeatSchema)slo);
		}
		
	}


	@Override
	public void addFeatureChunk(FeatureChunkRepeatSchema fcrs) {
		featureList.add(fcrs);
		generateFinalNoteMap();
	}
	@Override
	public void addFeatureChunk(SchemaListObject slo) {
		if (slo instanceof FeatureChunkRepeatSchema){
			addFeatureChunk((FeatureChunkRepeatSchema)slo);
		}
		
	}



	@Override
	public LiveClip getLiveClip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFinalNoteListComplete() {
		// TODO Auto-generated method stub
		return false;
	}
	public String toString(){
		String str = "repSchemaList.size=" + repSchemaList.size();
		str += "\nfeatureList.size()=" + featureList.size();
		str += "\nfinalNoteMap:-\n";
		for (double d: finalNoteMap.keySet()){
			str += d + ": - " + finalNoteMap.get(d).toString() + "\n";
		}
		return str;
	}
	public ArrayList<FinalListNote> getFinalNoteList(){
		ArrayList<FinalListNote> finalNoteList = new ArrayList<FinalListNote>();
		for (FinalListNote fln: finalNoteMap.values()){
			finalNoteList.add(fln);
		}
		return finalNoteList;
	}

// privates -------------------------------------------------------------------------------------------
	private void generateFinalNoteMap() {
		finalNoteMap.clear();
		repIndex = 0;
		featureIndex = 0;
		int endingsReached = 0;
		if (repSchemaList.size() > 0){
			while (endingsReached < 2){
				System.out.println("endingsReached=" + endingsReached);
				FeatureChunkRepeatSchema repSchema = repSchemaList.get(repIndex);
				FeatureChunkRepeatSchema feature = null;
				if (featureList.size() > 0){
					feature = featureList.get(featureIndex);
				}
				for (double d: repSchema.posArray()){
					addNewNote(d);
					if (feature != null){
						if (feature.featureID() == FeatureChunk.GAP_VALUE){
//							for (double gap: feature.gapValueArray()){		// probably does not work due to removing this
//								addNewNote(d + gap);
//							}
						}
					}
				}
				
				repIndex++;
				if (repIndex == repSchemaList.size()){
					repIndex = 0;
					endingsReached++;
				}
				featureIndex++;
				if (featureIndex == featureList.size()){
					featureIndex = 0;
					endingsReached++;
				}
			}
		}
		
		
	}

	private void addNewNote(double d) {
		if (!finalNoteMap.containsKey(d)){
			finalNoteMap.put(d, new FinalListNote(d));
		}
		
	}


	
}
