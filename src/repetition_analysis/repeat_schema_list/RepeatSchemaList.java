package repetition_analysis.repeat_schema_list;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

import javax.swing.ListModel;

import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchemaSortWrapper;
import repetition_analysis.feature_chunk.FeatureChunkWrapper;

public class RepeatSchemaList {

	public ArrayList<SchemaListObject> schemaList = new ArrayList<SchemaListObject>();
	SchemaListObject firstObj;
	public RepetitionAnalysis parent;
	
	public RepeatSchemaList(ArrayList<FeatureChunk> featureChunkList, RepetitionAnalysis parent){
		this.parent = parent;
		SchemaListObject endObj = new SchemaListEnd(schemaList, this);
		firstObj = endObj;
		schemaList.add(endObj);
		makeSchemaList(featureChunkList);
		
	}

	private void makeSchemaList(ArrayList<FeatureChunk> featureChunkList) {
		for (int i: parent.getHandlerMap().keySet()){
			for (FeatureChunk fc: featureChunkList){
				//System.out.println("handlerMap index=" + i + " FeatureChunk:" + fc.toString());
				FeatureChunkWrapper fcw = new FeatureChunkWrapper(fc, i);
				firstObj.add(fcw);
			}
		}		
	}

	public void sortList() {
		//System.out.println("SchemaRepeatList: Sorting schemaList..............................");
		Collections.sort(schemaList, repetitionCountComparator);
		
	}
	public String toString(){
		String str = "";
		int index = 0;
		for (SchemaListObject slo: schemaList){
			str += "index=" + index + " " + slo.toString() + "\n";
			index++;
		}
		return str;
	}
	public String toShortString() {
		String str = "";
		int index = 0;
		for (SchemaListObject slo: schemaList){
			str += "index=" + index + " " + slo.toShortString() + "\n";
			index++;
		}
		return str;
	}
	
	public static Comparator<SchemaListObject> repetitionCountComparator = new Comparator<SchemaListObject>(){
		public int compare(SchemaListObject slo1, SchemaListObject slo2){
			if (slo1.count() < slo2.count()) return 1;
			if (slo1.count() > slo2.count()) return -1;
			return 0;
		}
	};
	public static Comparator<FeatureChunkRepeatSchema> featureAndPosComparator = new Comparator<FeatureChunkRepeatSchema>(){
		public int compare(FeatureChunkRepeatSchema fcrs1, FeatureChunkRepeatSchema fcrs2){
			if (fcrs1.featureID() < fcrs2.featureID()) return 1;
			if (fcrs1.featureID() > fcrs2.featureID()) return -1;
			if (fcrs1.count() < fcrs2.count()) return 1;
			if (fcrs1.count() > fcrs2.count()) return -1;
			return 0;
		}
	};
	public ArrayList<FeatureChunkRepeatSchema> getFeatureList(int featureID){
		ArrayList<FeatureChunkRepeatSchema> featureList = new ArrayList<FeatureChunkRepeatSchema>();
		for (SchemaListObject slo: schemaList){
			if (slo instanceof FeatureChunkRepeatSchema){
				if (((FeatureChunkRepeatSchema) slo).featureID() == featureID){
					featureList.add((FeatureChunkRepeatSchema) slo);
				}
			}
		}
		Collections.sort(featureList, repetitionCountComparator);
		return featureList;
	}
	public int size(){
		return schemaList.size();
	}
	public FeatureChunkRepeatSchema get(int index){
		if (index > schemaList.size() - 2){
			return null;
		} else {
			return (FeatureChunkRepeatSchema)schemaList.get(index);
		}
		
	}
	public ArrayList<FeatureChunkRepeatSchemaSortWrapper> makePositionConstrainedSchemaList(double start, double end){
		ArrayList<FeatureChunkRepeatSchemaSortWrapper> sList = new ArrayList<FeatureChunkRepeatSchemaSortWrapper>();
		for (SchemaListObject slo: schemaList){
			if (slo instanceof FeatureChunkRepeatSchema){
				FeatureChunkRepeatSchemaSortWrapper sw = new FeatureChunkRepeatSchemaSortWrapper((FeatureChunkRepeatSchema)slo, start, end);
				if (sw.size() > 0){
					sList.add(sw);
				}				
			}
		}
		return sList;
	}
	public ArrayList<FeatureChunkRepeatSchemaSortWrapper> makePositionConstrainedSchemaList(int outputBarCount) {
		ArrayList<FeatureChunkRepeatSchemaSortWrapper> sList = new ArrayList<FeatureChunkRepeatSchemaSortWrapper>();
		for (SchemaListObject slo: schemaList){
			if (slo instanceof FeatureChunkRepeatSchema){
				FeatureChunkRepeatSchemaSortWrapper sw = new FeatureChunkRepeatSchemaSortWrapper((FeatureChunkRepeatSchema)slo, outputBarCount);
				if (sw.size() > 0){
					sList.add(sw);
				}				
			}
		}
		return sList;
	}
	public TreeMap<String, FCRSSortWrapperList> getPosMap(double start, double end){
		ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList = makePositionConstrainedSchemaList(start, end);
		return getPosMap(swList);
	}

	public TreeMap<String, FCRSSortWrapperList> getPosMap(ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList) {
		TreeMap<String, FCRSSortWrapperList> map = new TreeMap<String, FCRSSortWrapperList>();
		for (FeatureChunkRepeatSchemaSortWrapper sw: swList){
			if (!map.containsKey(sw.posArrayToString())){
				map.put(sw.posArrayToString(), new FCRSSortWrapperList(sw.posArray()));
			}
			map.get(sw.posArrayToString()).add(sw);
		}
		
		return map;
	
	}
	public TreeMap<String, FCRSSortWrapperList> getGapMap(ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList) {
		TreeMap<String, FCRSSortWrapperList> map = new TreeMap<String, FCRSSortWrapperList>();
		for (FeatureChunkRepeatSchemaSortWrapper sw: swList){
			if (!map.containsKey(sw.gapArrayToString())){
				map.put(sw.gapArrayToString(), new FCRSSortWrapperList(sw.gapArray()));
			}
			map.get(sw.gapArrayToString()).add(sw);
		}
		
		return map;
	
	}
	public ArrayList<FCRSSortWrapperList> getPosSortWrapperList(ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList){
		return getPosSortWrapperList(swList, -1, -1);			// -1 in both cases makes for no lower or upper limit to feature array size;
	}
	public ArrayList<FCRSSortWrapperList> getPosSortWrapperList(
			ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList,
			int smallestFeatureArraySize,							// value of -1 negates this as a criteria
			int largestFeatureArraySize){							// ditto
		ArrayList<FCRSSortWrapperList> list = new ArrayList<FCRSSortWrapperList>();
		for (FCRSSortWrapperList swl: getPosMap(swList).values()){
			boolean b = true;
			if (smallestFeatureArraySize > 0){
				if (swl.getDoubleArray().length < smallestFeatureArraySize){
					b = false;
				}
				
			}
			if (largestFeatureArraySize > 0){
				if (swl.getDoubleArray().length > largestFeatureArraySize){
					b = false;
				}
			}
			if (b)list.add(swl);
		}
//		Collections.sort(list, FCRSSortWrapperList.featureLengthAndPosComparator); // there are many sorting options. this should be dealt with by the app calling this method
		return list;
	}

	public ArrayList<FCRSSortWrapperList> getGapSortWrapperList(ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList) {
		return getGapSortWrapperList(swList, -1, -1);

	}

	public ArrayList<FCRSSortWrapperList> getGapSortWrapperList(ArrayList<FeatureChunkRepeatSchemaSortWrapper> swList,
			int smallestFeatureArraySize, int largestFeatureArraySize) {
		ArrayList<FCRSSortWrapperList> list = new ArrayList<FCRSSortWrapperList>();
		for (FCRSSortWrapperList swl: getGapMap(swList).values()){
			if (swl.getDoubleArray().length > -1){
				boolean b = true;
				if (smallestFeatureArraySize > 0){
					if (swl.getDoubleArray().length < smallestFeatureArraySize){
						b = false;
					}
					
				}
				if (largestFeatureArraySize > 0){
					if (swl.getDoubleArray().length > largestFeatureArraySize){
						b = false;
					}
				}
				if (b)list.add(swl);
			}
			
		}
//		Collections.sort(list, FCRSSortWrapperList.featureLengthAndPosComparator); // there are many sorting options. this should be dealt with by the app calling this method
		return list;
	}

	


}
