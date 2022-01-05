package repetition_analysis.corpus_capture;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import ChordScaleDictionary.ChordScaleDictionary;
import DataObjects.ableton_device_control_utils.DeviceParamInfo;
import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import UDPUtils.OSCAtom;
import UDPUtils.OSCMessMaker;
import UDPUtils.OSCUtils;
import repetition_analysis.clip_injector_object.ClipInjectorObject;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.note_chunk.ChunkList;

/*
 * captures material from Ableton Live
 * 
 * ... probably should use the 
 */
public class RepetitionCorpusCapturer {

	
	private ClipInjectorObject playInject;
	private int count = 0;
	private LiveClip lc = new LiveClip(0, 0);
	private ChordScaleDictionary csd = new ChordScaleDictionary();
	private ChunkList chunkList;
	private String chunkFileName = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/chunkList.txt";
	private String featureChunkFileName = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/featureList.txt";
	private String featureRepeatFileName = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/featureRepeatList.txt";
	private String oneLinerFileName = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/oneLinerList.txt";
	private ArrayList<FeatureChunk> featureChunkList = new ArrayList<FeatureChunk>();
	
	public RepetitionCorpusCapturer(){
		setClipInjectors();
		try {
			int port = 7802;
			
			DatagramSocket dsocket = new DatagramSocket(port);
			
			byte[] buffer = new byte[2048];
			
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
			while (true){
				dsocket.receive(packet);
				String msg = new String(buffer, 0, packet.getLength());

				//System.out.println(packet.getAddress().getHostName() + ": " + msg);
				ArrayList<OSCAtom> atList = OSCUtils.makeAtomList(buffer);
//				oscReceiveBuffer.add(atList);
				System.out.println("udp----" + makeStringFromAtomList(atList));
				packet.setLength(buffer.length);
				dealWithInput(atList);
			}
			
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	private void setClipInjectors() {
		ClipInjectorObject.sendResetInitializationMessage();
		playInject = new ClipInjectorObject(1, 0, ClipInjectorObject.ofTrackType);
		ClipInjectorObject.conn.sendUDPMessage(liveClipObjectInitializationMessage(playInject));
		
	}
	private OSCMessMaker liveClipObjectInitializationMessage(ClipInjectorObject cio) {
		OSCMessMaker mm = new OSCMessMaker();
		mm.addItem(DeviceParamInfo.initString);
		cio.instrumentInitializationMessage(mm);
//		System.out.println("NodeListGenerator.liveClipObjectInitializationMessage-------");
//		System.out.println(mm.toString());
		return mm;
	}
	private void dealWithInput(ArrayList<OSCAtom> atList) {
		if (atList.size() > 0){
			String first = atList.get(0).stringValue;
			if (first.equals("reset")){
				System.out.println("resetting count");
				count  = 0;
				lc = new LiveClip(0, 0);
			} else if (first.equals("note")){
				System.out.println("incrementing count");
				count++;
				LiveMidiNote lmn = new LiveMidiNote(atList.get(1).intValue, atList.get(2).floatValue, atList.get(3).floatValue, atList.get(4).intValue, atList.get(5).intValue);
				System.out.println(lmn.toLOMString());
				lc.addNote(lmn);
			} else if (first.equals("count")){
				lc.sortNoteList();
				System.out.println(lc.toString());
				System.out.println(count);
				lc.endMarker = 5000;
				lc.length = 5000;
				playInject.sendClip(lc);
			} else if (first.equals("makechunklist")){
//				makeChunkList();
//				makeFeatureChunkList(4);
//				makeFeatureRepeatMap(4);
				makeOneLinerList();
			} else if (first.equals("signature_numerator")){
				System.out.println("signature_numerator=" + atList.get(1).intValue);
				lc.signatureNumerator = atList.get(1).intValue;
			} else if (first.equals("signature_denominator")){
				System.out.println("signature_denominator=" + atList.get(1).intValue);
				lc.signatureDenominator = atList.get(1).intValue;
			} else if (first.equals("start_marker")){
				System.out.println("start_marker=" + atList.get(1).floatValue);
				lc.startMarker = atList.get(1).floatValue; 
			} else if (first.equals("end_marker")){
				System.out.println("end_marker=" + atList.get(1).floatValue);
				lc.endMarker = atList.get(1).floatValue;
			} else if (first.equals("length")){
				System.out.println("length=" + atList.get(1).floatValue);
				lc.length = atList.get(1).floatValue;
			} else if (first.equals("loop_start")){
				// forthe purposes of this analysis project,the loop_start point is the downbeat of the piece
				System.out.println("loop_start=" + atList.get(1).floatValue);
				lc.loopStart = atList.get(1).floatValue;
			} else if (first.equals("loop_end")){
				System.out.println("loop_end=" + atList.get(1).floatValue);
				lc.loopEnd = atList.get(1).floatValue;
			} else if (first.equals("name")){
				System.out.println("name=" + atList.get(1).stringValue);
				lc.name = atList.get(1).stringValue;
			}
		}
		
	}
	private void makeOneLinerList() {
		try {
			FileWriter fw = new FileWriter(oneLinerFileName);
			BufferedWriter bw = new BufferedWriter(fw);
			System.out.println("makeOneLinerList called:");
			System.out.println(lc.toString());
			String str = lc.getClipAsTextFile();
//			String str = "name," + lc.name + "\n";
//			str += "length," + lc.length + "\n";
//			str += "loopStart," + lc.loopStart + "\n";
//			str += "loopEnd," + lc.loopEnd + "\n";
//			str += "startMarker," + lc.startMarker + "\n";
//			str += "endMarker," + lc.endMarker + "\n";
//			str += "signatureNumerator," + lc.signatureNumerator + "\n";
//			str += "signatureDenominator," + lc.signatureDenominator + "\n";
//			str += "type,note,modnote,octave,position,length,velocity\n";
//			for (LiveMidiNote lmn: lc.noteList){
//				str += lmn.oneLineToString() + "\n";
//			}
			System.out.println(str);
			bw.write(str);
			bw.close();
			fw.close();
		} catch (IOException ex){
			System.out.println("makeOneLinerList failed to work");
			System.out.println(ex.getMessage());
		}
		
	}
//	private void makeFeatureRepeatMap(int chunkLength) {
//		HashMap<String, FeatureChunkRepeatSchema> fcMap = chunkList.getRepeatListMap(chunkLength);
//		makeFeatureRepeatListTextFile(fcMap);
//		
//	}
	private void makeFeatureRepeatListTextFile(HashMap<String, FeatureChunkRepeatSchema> fcMap) {
		try {
			FileWriter fw = new FileWriter(featureRepeatFileName);
			BufferedWriter bw = new BufferedWriter(fw);
			String str = "";
			ArrayList<FeatureChunkRepeatSchema> fList = new ArrayList<FeatureChunkRepeatSchema>();
			for (FeatureChunkRepeatSchema fcrl: fcMap.values()){
				fList.add(fcrl);
			}
			Collections.sort(fList, FeatureChunkRepeatSchema.repCountComparator);
			
			for (FeatureChunkRepeatSchema fcrl: fList){
				str += fcrl.toString() + "\n";
			}
			System.out.println(str);
			bw.write(str);
			bw.close();
			fw.close();
		} catch (IOException ex){
			
		}
		
	}
	private void makeFeatureChunkList(int chunkLength) {
		featureChunkList = chunkList.getFeatureChunkList(chunkLength);
		makeFeatureChunkListTextFile();
	}
	private void makeFeatureChunkListTextFile() {
		try {
			FileWriter fw = new FileWriter(featureChunkFileName);
			BufferedWriter bw = new BufferedWriter(fw);
			String str = "";
			for (FeatureChunk fc: featureChunkList){
				str += fc.toString() + "\n";
			}
			System.out.println(str);
			bw.write(str);
			bw.close();
			fw.close();
		} catch (IOException ex){
			
		}
		
	}
	private void makeChunkList() {
		chunkList = new ChunkList();
		chunkList.addClip(lc);
		System.out.println(chunkList.toString());
		makeChunkListTestFile();
	}
	private void makeChunkListTestFile() {
		try {
			FileWriter fw = new FileWriter(chunkFileName);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(chunkList.toString());
			bw.close();
			fw.close();
		} catch (IOException ex){
			
		}
		
	}
	private String makeStringFromAtomList(ArrayList<OSCAtom> atList){
		String ret = "";
		for (OSCAtom atom: atList){
			ret = ret + atom.itemAsString() + ", ";
		}
		return ret;
	}
	public LiveClip getCorpusClip() {
		lc.clear();
//		sendGetClipMessage();
		return lc;
	}

}
