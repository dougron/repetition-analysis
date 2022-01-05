package gui_objects;

import java.io.File;
import javax.swing.filechooser.*;;


public class LiveClipFileFilter extends FileFilter{

	private final String extension = "liveclip";
	private final String exclude = "chords.liveclip";
	
	public boolean accept(File file){
		if (file.isDirectory()){
			return true;
		} else if (file.getName().toLowerCase().endsWith(extension)){
			if (file.getName().toLowerCase().endsWith(exclude)){
				return false;
			} else {
				return true;
			}			
		} else {
			return false;
		}
			
	}

	@Override
	public String getDescription() {
		return "only .liveclip";
	}
}
