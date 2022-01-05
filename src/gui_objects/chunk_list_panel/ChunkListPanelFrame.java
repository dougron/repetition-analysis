package gui_objects.chunk_list_panel;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JFrame;

import DataObjects.ableton_live_clip.LiveClip;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.repeat_schema_list.RepeatSchemaList;

import java.awt.FlowLayout;

public class ChunkListPanelFrame {

	private String path = "src/resources/short_test/ShortTest.liveclip";
	private String chordspath = "src/resources/short_test/ShortTest.chords.liveclip";

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChunkListPanelFrame window = new ChunkListPanelFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ChunkListPanelFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 650, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		LiveClip lc = getLiveClip();
		LiveClip chords = getChordsClip();
		int cellLength = 4;
		RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLength, chords);
		RepeatSchemaList rsl = ra.getSchemaList();
		
		ChunkListPanel clp = new ChunkListPanel(ra.getChunkList());
		frame.getContentPane().add(clp);
	}
	
	private LiveClip getClip(String p) {
		LiveClip lc = new LiveClip(0, 0);
		try {
			lc.instantiateClipFromBufferedReader(new BufferedReader(new FileReader(p)));			
		} catch (Exception ex){
			
		}
		return lc;
	}
	public LiveClip getChordsClip(){
		return getClip(chordspath);
	}
	public LiveClip getLiveClip(){
		return getClip(path);
	}

}
