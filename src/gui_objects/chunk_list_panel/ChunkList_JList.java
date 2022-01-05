package gui_objects.chunk_list_panel;

import javax.swing.JList;

import repetition_analysis.note_chunk.ChunkList;

public class ChunkList_JList extends JList<String> {
	
	public ChunkList cl;

	public ChunkList_JList(String[] strArr, ChunkList cl){
		super(strArr);
		this.cl = cl;
	}
}
