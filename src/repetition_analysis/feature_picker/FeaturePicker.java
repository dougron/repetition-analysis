package repetition_analysis.feature_picker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import DataObjects.ableton_live_clip.LiveClip;
import ResourceUtils.ChordForm;
import gui_objects.feature_chooser_panel.FeatureChooser;
import gui_objects.feature_chooser_panel.FeaturePickerPanel;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_handler.FeatureHandler;
import repetition_analysis.repeat_schema_list.RepeatSchemaList;
import repetition_analysis.repeat_schema_list.SchemaListObject;

public class FeaturePicker {

	private RepetitionAnalysis ra = null;
	FeatureChunkRepeatSchema masterRepeatSchema = null;
	FeatureChunkRepeatSchema[] rhythmFeatureArr = null;
	int rhythmFeatureMasterSchemaFeatureID;
	FeatureChunkRepeatSchema[] durationFeatureArr = null;
	int durationFeatureMasterSchemaFeatureID;
	FeatureChunkRepeatSchema[] dynamicsFeatureArr = null;
	int dynamicsFeatureMasterSchemaFeatureID;
	FeatureChunkRepeatSchema[] pitchFeatureArr = null;
	int pitchFeatureMasterSchemaFeatureID;
	FeatureChunkRepeatSchema[] contourFeatureArr = null;
	int contourFeatureMasterSchemaFeatureID;
	private boolean needsContour = true;
	private FeaturePickerPanel panel = null;
	private ChordForm outputCF = null;
	private double xover = 0.5;			// default value
	private boolean copyOverRule = true;
	private boolean copyOverRuleHasBeenSet = false;
	
	
	private static final FeatureChunkRepeatSchema nullSchema = new FeatureChunkRepeatSchema();

	public FeaturePicker(RepetitionAnalysis ra){
		this.ra = ra;
		sysOutToString();
	}
	public FeaturePicker(){
		
	}
	public FeaturePicker(FeaturePickerPanel panel) {
		this.panel = panel;
	}
	public RepetitionAnalysis ra(){
		return ra;
	}

	public ArrayList<String> getSchemaListNames() {
		ArrayList<String> list = new ArrayList<String>();
		for (SchemaListObject slo: ra.getSchemaList().schemaList){
			if (slo instanceof FeatureChunkRepeatSchema) list.add(slo.toShortString());
		}
		return list;
	}

	public RepeatSchemaList getSchemaList() {		
		return ra.getSchemaList();
	}

	public FeatureChunkRepeatSchema getRepeatSchema(int index) {
		if (ra == null){
			return null;
		} else {
			return ra.getSchemaList().get(index);
		}
		
	}

	public FeatureChunkRepeatSchema[] getRhythmFeatureArr (){
		return rhythmFeatureArr;
	}
	
	public FeatureChunkRepeatSchema[] getDurationFeatureArr (){
		return durationFeatureArr;
	}
	
	public FeatureChunkRepeatSchema[] getDynamicsFeatureArr (){
		return dynamicsFeatureArr;
	}
	
	public FeatureChunkRepeatSchema[] getPitchFeatureArr (){
		return pitchFeatureArr;
	}
	
	public FeatureChunkRepeatSchema[] getContourFeatureArr (){
		return contourFeatureArr;
	}
	
	public int schemaListSize() {
		return ra.getSchemaList().size();
	}

	public void setMasterRepeatSchema(FeatureChunkRepeatSchema masterRepeatSchema) {
		this.masterRepeatSchema = masterRepeatSchema;
		sysOutToString();
	}

	public ArrayList<FeatureChunkRepeatSchema> getFeatureList(int[] featureIDArr) {
		ArrayList<FeatureChunkRepeatSchema> list = new ArrayList<FeatureChunkRepeatSchema>();
		for (int i: featureIDArr){
			list.addAll(getFeatureList(i));
		}
		return list;
	}
	public ArrayList<FeatureChunkRepeatSchema> getFeatureList(int featureID){
		if (ra != null){
			return ra.getSchemaList().getFeatureList(featureID);
		} else {
			return new ArrayList<FeatureChunkRepeatSchema>();
		}
		
	}
	public void bang(){
		// only works when there is a panel in existance. calls the panel to test if things are complete()
		if (panel != null){
			panel.bang();
		}
	}
	public void setOutputChordForm(ChordForm cf){
		outputCF = cf;
		sysOutToString();
//		bang();
	}
	public ChordForm outputCF(){
		return outputCF;
	}

	public void setRhythmFeatureArr(FeatureChunkRepeatSchema[] rhythmFeatureArr) {
		this.rhythmFeatureArr = rhythmFeatureArr;
		sysOutToString();
	}

	public void setRhythmFeatureMasterSchemaFeatureID(int rhythmFeatureMasterSchemaFeatureID) {
		this.rhythmFeatureMasterSchemaFeatureID = rhythmFeatureMasterSchemaFeatureID;
		sysOutToString();
	}

	public void setDurationFeatureArr(FeatureChunkRepeatSchema[] durationFeatureArr) {
		this.durationFeatureArr = durationFeatureArr;
		sysOutToString();
	}
	public void setDurationFeatureMasterSchemaFeatureID(int durationFeatureMasterSchemaFeatureID) {
		this.durationFeatureMasterSchemaFeatureID = durationFeatureMasterSchemaFeatureID;
		sysOutToString();
	}

	public void setDynamicsFeatureArr(FeatureChunkRepeatSchema[] dynamicsFeatureArr) {
		this.dynamicsFeatureArr = dynamicsFeatureArr;
		sysOutToString();
	}

	public void setDynamicsFeatureMasterSchemaFeatureID(int dynamicsFeatureMasterSchemaFeatureID) {
		this.dynamicsFeatureMasterSchemaFeatureID = dynamicsFeatureMasterSchemaFeatureID;
		sysOutToString();
	}

	public void setPitchFeatureArr(FeatureChunkRepeatSchema[] pitchFeatureOptions) {
		this.pitchFeatureArr = pitchFeatureOptions;
		setNeedsContour(testNeedsContour());
		sysOutToString();
	}

	private boolean testNeedsContour() {
		boolean bool = false;
		if (pitchFeatureArr == null || pitchFeatureArr.length == 0){
			
		} else {
			for (FeatureChunkRepeatSchema fcrs: pitchFeatureArr){
				if (FeatureChunk.getHandlerMap().containsKey(fcrs.featureID())){
					if (FeatureChunk.getHandlerMap().get(fcrs.featureID()).needsContourFeature()){
						bool = true;
					}
				}
			}
		}
		return bool;
	}
	public void setPitchFeatureMasterSchemaFeatureID(int pitchFeatureMasterSchemaFeatureID) {
		this.pitchFeatureMasterSchemaFeatureID = pitchFeatureMasterSchemaFeatureID;
		sysOutToString();
	}

	public void setContourFeatureArr(FeatureChunkRepeatSchema[] contourFeatureArr) {
		this.contourFeatureArr = contourFeatureArr;
		sysOutToString();
	}

	public void setContourFeatureMasterSchemaFeatureID(int contourFeatureMasterSchemaFeatureID) {
		this.contourFeatureMasterSchemaFeatureID = contourFeatureMasterSchemaFeatureID;
		//sysOutToString();
	}
	private void sysOutToString() {
		//System.out.println(toString());
		
	}
	public void setNeedsContour(boolean b){
		needsContour = b;
	}
	public boolean complete(){
		if (ra != null && outputCF != null && masterRepeatSchema != null){
			if (needsContour){
				if (rhythmFeatureArr == null || rhythmFeatureArr.length == 0
						|| durationFeatureArr == null || durationFeatureArr.length == 0
						|| dynamicsFeatureArr == null || dynamicsFeatureArr.length == 0
						|| pitchFeatureArr == null || pitchFeatureArr.length == 0
//						|| contourFeatureArr == null || contourFeatureArr.length == 0
						){
					return false;
				} else {
					return true;
				}
			} else if (rhythmFeatureArr == null || rhythmFeatureArr.length == 0
					|| durationFeatureArr == null || durationFeatureArr.length == 0
					|| dynamicsFeatureArr == null || dynamicsFeatureArr.length == 0
					|| pitchFeatureArr == null || pitchFeatureArr.length == 0){
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	public int rhythmID(){
		return rhythmFeatureMasterSchemaFeatureID;
	}
	public int durationID(){
		return durationFeatureMasterSchemaFeatureID;
	}
	public int dynamicsID(){
		return dynamicsFeatureMasterSchemaFeatureID;
	}
	public int pitchID(){
		return pitchFeatureMasterSchemaFeatureID;
	}
	public int contourID(){
		return contourFeatureMasterSchemaFeatureID;
	}

	public HashMap<Integer, FeatureHandler> getHandlerMap() {
		if (ra == null){
			return new HashMap<Integer, FeatureHandler>();
		} else {
			return ra.getHandlerMap();
		}
		
	}

	public boolean hasChordsClip() {
		return ra.hasChordsClip();
	}
	public boolean hasOutputCF(){
		if (outputCF == null){
			return false;
		} else {
			return true;
		}
	}
	public void setXover(double d){
		// range is 0.0 - 1.0. % used to for quick and dirty avoidance of bullshit input
		if (d == 1.0){
			xover = 1.0;
		} else {
			xover = (d % 1.0);
		}
	}
	public double xover(){
		return xover;
	}
	public void setRepetitionAnalysis(RepetitionAnalysis repetitionAnalysis) {
		ra = repetitionAnalysis;
		//rebuildSchemaArraysFromIndexLists();
	}
	
	public boolean hasRepetitionAnalysis() {
		if (ra == null){
			return false;
		} else {
			return true;
		}
	}
	public void setArray(ArrayList<FeatureChunkRepeatSchema> list, int elementType) {
		FeatureChunkRepeatSchema[] arr = list.toArray(new FeatureChunkRepeatSchema[list.size()]);
		//System.out.println("FeaturePicker.setArray() for elementType=" + elementType + " list size=" + list.size());; 
		switch (elementType){
		case FeatureChooser.RHYTHM: setRhythmFeatureArr(arr);
		break;
		case FeatureChooser.DURATION: setDurationFeatureArr(arr);
		break;
		case FeatureChooser.DYNAMICS: setDynamicsFeatureArr(arr);
		break;
		case FeatureChooser.PITCH: setPitchFeatureArr(arr);
		break;
		case FeatureChooser.CONTOUR: setContourFeatureArr(arr);
		break;
		}
	}
	public void setMasterFeatureID(int index, int elementType){
		//System.out.println("FeaturePicker.setMasterFeatureID for elementType=" + elementType + " to index=" + index);
		switch (elementType){
		case FeatureChooser.RHYTHM: setRhythmFeatureMasterSchemaFeatureID(index);
		break;
		case FeatureChooser.DURATION: setDurationFeatureMasterSchemaFeatureID(index);
		break;
		case FeatureChooser.DYNAMICS: setDynamicsFeatureMasterSchemaFeatureID(index);
		break;
		case FeatureChooser.PITCH: setPitchFeatureMasterSchemaFeatureID(index);
		break;
		case FeatureChooser.CONTOUR: setContourFeatureMasterSchemaFeatureID(index);
		break;
		}
		
	}
	public FeatureChunkRepeatSchema getMasterRepeatSchema(){
		return masterRepeatSchema;
	}
	public String toString(){
		String str = "FeaturePicker: ra=";
		if (ra == null){
			str += "null";
		} else {
			str += ra.contentClipName() + ", " + ra.chordsClipName();
		}
		if (masterRepeatSchema == null){
			str += "\nno master schema";
		} else {
			str += "\nmaster repeats: " + masterRepeatSchema.repeatsToString();
		}
		if (rhythmFeatureArr == null){
			str += "\nno rhythmFeatureArr";
		} else {
			str += "\n" + featureLineToString(rhythmFeatureArr, "rhythmFeatureArr");
		}
		str += "\nrhythmID=" + rhythmID();
		
		if (durationFeatureArr == null){
			str += "\nno durationFeatureArr";
		} else {
			str += "\n" + featureLineToString(durationFeatureArr, "durationFeatureArr");
		}
		str += "\ndurationID=" + durationID();
		
		if (dynamicsFeatureArr == null){
			str += "\nno dynamicsFeatureArr";
		} else {
			str += "\n" + featureLineToString(dynamicsFeatureArr, "dynamicsFeatureArr");
		}
		str += "\ndynamicsID=" + dynamicsID();
		
		if (pitchFeatureArr == null){
			str += "\nno pitchFeatureArr";
		} else {
			str += "\n" + featureLineToString(pitchFeatureArr, "pitchFeatureArr");
		}
		str += "\npitchID=" + pitchID();
		
		if (contourFeatureArr == null){
			str += "\nno contourFeatureArr";
		} else {
			str += "\n" + featureLineToString(contourFeatureArr, "contourFeatureArr");
		}
		str += "\ncontourID=" + contourID();
		str += "\nhasOutputCF=" + hasOutputCF() + " needs contour=" + needsContour;
		str += "\ncomplete()=" + complete();
		return str;
	}
	private String featureLineToString(FeatureChunkRepeatSchema[] arr, String str){
		String string = str + ": ";
		for (FeatureChunkRepeatSchema fcrs: arr){
			string += fcrs.nameAndRepeatCountToString() + ", ";
		}
		return string;
	}
	public void saveInfoSheet(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			for (String str: makeInfoList(path)){
				//			System.out.println(str);				// this prints xml file contents to console
				bw.write(str);
				bw.newLine();
			}

			bw.close();
		} catch (Exception ex){
			System.out.println(ex.toString());
		}

	}
	private ArrayList<String> makeInfoList(String path) {
		ArrayList<String> strList = new ArrayList<String>();
		strList.add("filename: " + path + "\n");
		if (masterRepeatSchema != null){
			strList.add("masterRepeatSchema: " + masterRepeatSchema.repeatsToString());
			strList.add("bar-and-offset list:" + masterRepeatSchema.barXOverPosArrayToString(xover));
			strList.add("xover for bar-and-offset calculation=" + xover);
			if (panel == null){
				if (copyOverRuleHasBeenSet){
					strList.add("copyOverRule=" + copyOverRule);
				} else {
					strList.add("copyOverRule has not been set in FeaturePicker");
				}
				
			} else {
				strList.add(panel.getCopyOverRuleText());
			}
			
		}
		
		strList.add("\n\n");
		strList.addAll(makeFeatureSpecificInfoList("rhythm feature", rhythmFeatureArr, rhythmFeatureMasterSchemaFeatureID));
		
		strList.add("\n\n");
		strList.addAll(makeFeatureSpecificInfoList("duration feature", durationFeatureArr, durationFeatureMasterSchemaFeatureID));
		
		strList.add("\n\n");
		strList.addAll(makeFeatureSpecificInfoList("dynamics feature", dynamicsFeatureArr, dynamicsFeatureMasterSchemaFeatureID));
		
		strList.add("\n\n");
		strList.addAll(makeFeatureSpecificInfoList("pitch feature", pitchFeatureArr, pitchFeatureMasterSchemaFeatureID));
		
		strList.add("\n\n");
		strList.addAll(makeFeatureSpecificInfoList("contour feature", contourFeatureArr, contourFeatureMasterSchemaFeatureID));
		
		
		return strList;
	}
	public void setCopyOverRule(boolean b){
		copyOverRule  = b;
		copyOverRuleHasBeenSet = true;
	}
	
	private ArrayList<String> makeFeatureSpecificInfoList(String featureName, FeatureChunkRepeatSchema[] fcrsArr, int featureID){
		ArrayList<String> strList = new ArrayList<String>();
		int index = 0;
		if (fcrsArr != null){
			strList.add(featureName + "(s): indexList- " + masterRepeatSchema.indexListToString(featureID));
			strList.add("");
			for (FeatureChunkRepeatSchema fcrs: fcrsArr){
				strList.add("   index=" + index + " " + fcrs.nameAndFeatureStringToString());
				index++;
			} 			
		} else {
			strList.add(featureName + "(s): no features present");
		}
		return strList;
	}
}
