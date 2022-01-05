package repetition_analysis.info_objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/*
 * class for wrapping register information for a musical part
 */
public class RegisterInfo {

	public int min;
	public int max;
	public int avg;
	public String name;
	public int comfortableMin;
	public int comfortableMax;
	
	public RegisterInfo(int min, int max, int comfortableMin, int comfortableMax, int avg, String name){
		this.min = min;
		this.max = max;
		this.comfortableMin = comfortableMin;
		this.comfortableMax = comfortableMax;
		this.avg = avg;
		this.name = name;
	}
	
	public int centre(){
		return (max - min) / 2 + min;
	}
	public String toString(){
		String str = "RegisterInfo: " + name + " min=" + min + " max=" + max + " comfy_min=" + comfortableMin + " comfy_max=" + comfortableMax +" avg=" + avg + " centre=" + centre();
		return str;
	}
	
	
	public static final String TRUMPET = "trumpet";
	public static final String SOPRANO_SAX = "soprano_sax";
	public static final String ALTO_SAX = "alto_sax";
	//....etc
	
	public static final HashMap<String, RegisterInfo> registerMap = new HashMap<String, RegisterInfo>()
			{{
				put(TRUMPET, new RegisterInfo(42, 70, 42, 70, 52, TRUMPET));
				put(SOPRANO_SAX, new RegisterInfo(44, 68, 44, 68, 58, SOPRANO_SAX));
				put(ALTO_SAX, new RegisterInfo(35, 64, 35, 64, 42, ALTO_SAX));
			}};

	public static RegisterInfo getNewRegisterInfoFromFile(File file) {

		if (file.isFile()){
			try {
				BufferedReader b = new BufferedReader(new FileReader(file));				
				String tempName = "";
				int low1 = 0, low2 = 0, high1 = 0, high2 = 0, av = 0;
				
				while (true){
					String str = b.readLine();
					if (str == null) break;
					String[] line = str.split(",");
					if (line.length == 2){
						switch (line[0]){
						case "name": tempName = line[1];
						break;
						case "extremeHigh":	high2 = Integer.parseInt(line[1]);
						break;
						case "comfortableHigh":	high1 = Integer.parseInt(line[1]);
						break;
						case "comfortableLow":	low1 = Integer.parseInt(line[1]);
						break;
						case "extremeLow":	low2 = Integer.parseInt(line[1]);
						break;
						case "average":	av = Integer.parseInt(line[1]);
						break;

						}
					}
				}
				b.close();
				return new RegisterInfo(low2, high2, low1, high1, av, tempName);
			} catch (Exception ex){
				
			}
		}
		return null;
	}
}
