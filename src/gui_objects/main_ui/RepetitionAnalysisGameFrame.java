 package gui_objects.main_ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import DataObjects.ableton_live_clip.LiveClip;
import GUIObjects.FileBrowserFilter;
import ResourceUtils.ChordForm;

public class RepetitionAnalysisGameFrame extends JFrame{

	public static final int sendPort = 7800;
	public static final String propertiesPath = "src/resources/repetition_analysis.properties";
	private int xsize = 1400;
	private int ysize = 1000;
	private RepetitionAnalysisGamePanel ui;
	private FileBrowserFilter liveClipFileFilter = new FileBrowserFilter(new String[]{"liveclip"});

	//private ChordScaleDictionary csd = new ChordScaleDictionary();

	public RepetitionAnalysisGameFrame(){
		initUI();
	}

// privates ===========================================================================

		private void initUI(){				
			setTitle("RepetitionAnalysisGameLoopFrame");
			setSize(xsize, ysize);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			statusLine = addStatusBar();
			ui = new RepetitionAnalysisGamePanel();
			setupMenuBar();
			add(ui);
			//statusMessage("urg...");
		}
		private void setupMenuBar() {
			JMenuBar jmb = new JMenuBar();			
			setJMenuBar(jmb);
			jmb.add(liveMenu());
		}
		private JMenu liveMenu() {
			JMenu mnOutput = new JMenu("Live");
			mnOutput.add(mntmResendInitMessage());
			mnOutput.add(mntmLoadLiveClipFromFile());
			return mnOutput;
		}
		private JMenuItem mntmLoadLiveClipFromFile() {

			JMenuItem menuItem = new JMenuItem("Load liveclip");
			menuItem.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent arg0) {
					JButton open = new JButton();
					JFileChooser fc = new JFileChooser();
					fc.setCurrentDirectory(new File(ui.getLiveclipFolderPath()));
					fc.setDialogTitle("Select liveclip");
					fc.setFileFilter(liveClipFileFilter);
					if (fc.showOpenDialog(open) == JFileChooser.APPROVE_OPTION){
						File f = fc.getSelectedFile();
						//System.out.println(selectedFile.getPath());
						//System.out.println(selectedFile.getName());
						try {
							BufferedReader b = new BufferedReader(new FileReader(f));
							LiveClip lc = new LiveClip(0, 0);
							lc.instantiateClipFromBufferedReader(b);
							ui.cio.sendClip(lc);
						} catch (Exception ex){
							
						}
					}	
				}
			});
			return menuItem;
		}
		private JMenuItem mntmResendInitMessage() {
			JMenuItem menuItem = new JMenuItem("Resend init message(s)");
			menuItem.addActionListener(new ActionListener() {
				

				public void actionPerformed(ActionEvent arg0) {
					ui.cio.sendResetInitializationMessage();
					ui.cio.sendInitializationMessage();
					ui.cio.sendClip(LiveClip.emptyClip());
				}
			});
			return menuItem;
		}
	
	
// runnable =-=-=-=--=-====--------------------------------------------------
	public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	RepetitionAnalysisGameFrame ex = new RepetitionAnalysisGameFrame();
                ex.setVisible(true);
            }
        });
    }

}
