package gui_objects.chord_panel;

import java.io.File;
import javax.swing.filechooser.*;;


public class ChordsLiveClipFileFilter extends FileFilter{

	private final String extension = "chords.liveclip";
	
	public boolean accept(File file){
		if (file.isDirectory()){
			return true;
		} else if (file.getName().toLowerCase().endsWith(extension)){
			return true;
		} else {
			return false;
		}
			
	}

	@Override
	public String getDescription() {
		return "only chords.liveclip";
	}
}
