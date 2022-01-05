package repetition_analysis.corpus_capture;

/*
 * mxj module that works in the GetAllMidiClips Max patch and which gets all clips in an
 * Ableton Live set and saves it as a .liveclip file in the path directory
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.cycling74.max.DataTypes;
import com.cycling74.max.MaxObject;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;

public class ClipFileMaker extends MaxObject{

	private String path = "C:\\Users\\Doug\\Documents\\_MASTER OF UNIBERSE\\Repetition text files\\FileMakerRepository\\";
	private LiveClip lc;
	private static final String extension = ".liveclip";
	private static int noname = 0;
	private HashMap<String, Integer> usedNameMap = new HashMap<String, Integer>();
	
	public ClipFileMaker(){
		setMaxInlets();	
		makeNewLiveClip();
	}
	public void hello(){
		post("hello......");
	}
	public void clear(){
		usedNameMap.clear();
		noname = 0;
	}
	public void done(){
		String str = path;
		if (lc.name.equals("") || lc.name == null || lc.name.equals("<empty>")){
			System.out.println("'" + lc.name + "' is a dud name");
			str += "clip_" + addLeadingZeros(noname, 3) + extension;
			noname++;
		} else {
			System.out.println("'" + lc.name + "'");
			if (usedNameMap.containsKey(lc.name)){
				int nnn = usedNameMap.get(lc.name);
				str += lc.name + "_" + addLeadingZeros(nnn, 3) + extension;
				usedNameMap.put(lc.name, nnn + 1);
			} else {
				str += lc.name + extension;
				usedNameMap.put(lc.name, 0);
			}
			
		}
		writeLiveClipToFile(str);
		lc = new LiveClip(0, 0);
	}
	private String addLeadingZeros(int i, int length){
		String str = "" + i;
		for (int j = 0; j < length - str.length() + 1; j++){
			str = "0" + str;
		}
		return str;
	}
	public void length(double d){
	}
	public void loop_start(double d){
		lc.loopStart = d;
	}
	public void loop_end(double d){
		lc.loopEnd = d;
	}
	public void start_marker(double d){
		lc.startMarker = d;
	}
	public void end_marker(double d){
		lc.endMarker = d;
	}
	public void signature_numerator(int i){
		lc.signatureNumerator = i;
	}
	public void signature_denominator(int i){
		lc.signatureDenominator = i;
	}
	public void name(String str){
		lc.name = str;
	}
	public void notes(int i){
		// placeholder......
	}
	public void note(int note, double pos, double len, int vel, int mute){
		lc.addNote(note, pos, len, vel, mute);
	}
	

	// privates ----------------------------------------------------------------------
	private void writeLiveClipToFile(String filepath) {
		try {
			FileWriter fw = new FileWriter(filepath);
			BufferedWriter bw = new BufferedWriter(fw);
//			System.out.println("writeLiveClipToFile called:");
//			System.out.println(lc.toString());
			String str = lc.getClipAsTextFile();
//			System.out.println(str);
			bw.write(str);
			bw.close();
			fw.close();
		} catch (IOException ex){
			System.out.println("writeLiveClipToFile failed to work");
			System.out.println(ex.getMessage());
		}
		
	}
	private void makeNewLiveClip() {
		lc = new LiveClip(0, 0);		
	}

	private void setMaxInlets(){
		declareInlets(new int[]{
				DataTypes.ALL,  
				DataTypes.INT}
		);
		declareOutlets(new int[]{ 
				DataTypes.ALL, 
				DataTypes.ALL}
		);
		setInletAssist(new String[]{
			"messages in",
			"erm...."}
		);
		setOutletAssist(new String[]{
			"erm......",
			"dump out"}
		);
	}
}
