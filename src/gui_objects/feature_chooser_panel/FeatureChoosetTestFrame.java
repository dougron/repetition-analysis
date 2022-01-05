package gui_objects.feature_chooser_panel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import DataObjects.ableton_live_clip.LiveClip;
import repetition_analysis.RepetitionAnalysis;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.feature_chunk.FeatureChunkRepeatSchema;
import repetition_analysis.feature_picker.FeaturePicker;

public class FeatureChoosetTestFrame extends JFrame {

	private JPanel contentPane;

	private String path = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ShortTest.liveclip";
	private String chordspath = "C:/Users/Doug/Documents/_MASTER OF UNIBERSE/Repetition text files/ShortTest.chords.liveclip";



	/**
	 * Create the frame.
	 */
	public FeatureChoosetTestFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		LiveClip lc = getLiveClip();
		LiveClip chords = getChordsClip();
		int cellLength = 3;
		RepetitionAnalysis ra = new RepetitionAnalysis(lc, cellLength, chords);
		FeaturePicker fp = new FeaturePicker(ra);
		
		FeatureChooser rhythmChooser = new FeatureChooser(fp, FeatureChooser.RHYTHM, "TestBaby...");
		contentPane.add(rhythmChooser, BorderLayout.CENTER);
		ArrayList<FeatureChunkRepeatSchema> list = fp.getFeatureList(FeatureChunk.GAP_VALUE);
		rhythmChooser.setOptionList(list);
		fp.setMasterRepeatSchema(fp.getRepeatSchema(4));	//arb for testing
		rhythmChooser.setAttachList(fp.getMasterRepeatSchema().secondaryIndexList());
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
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FeatureChoosetTestFrame frame = new FeatureChoosetTestFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
