package gui_objects.repeat_schema_panel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui_objects.main_ui.RepetitionAnalysisGameFrame;

public class RepeatSchemaGraphic extends JPanel {
	
	private String linePath = "D:/Documents/DougzJavaz/RepetitionAnalysis/graphics/repSchemaLine.png";
	private String dotPath = "D:/Documents/DougzJavaz/RepetitionAnalysis/graphics/repSchemaDot.png";
	private String barPath = "D:/Documents/DougzJavaz/RepetitionAnalysis/graphics/repSchemaBar.png";
	private int IMAGE_WIDTH = 15;
	private int IMAGE_HEIGHT = 20;
//	private int lineLength = 4;		// number of bars per line


	public RepeatSchemaGraphic(ArrayList<Double[]> posList, double length, double resolution, double barLength, int lineLength){
		setPathsFromPropertiesFile();
		setLayout(new GridBagLayout());
		setGraphic(posList, length, resolution, barLength, lineLength);
		
	}
	public RepeatSchemaGraphic(){
		setLayout(new GridBagLayout());
	}
	public void setGraphic(ArrayList<Double[]> posList, double length, double resolution, double barLength, int lineLength){
		int yIndex = 0;
		clear();
		makeTextRuler(yIndex, length, resolution, barLength, lineLength, posList.size() + 1);
		yIndex = 1;
		for (Double[] dArr: posList){
			makeSchema(yIndex, dArr, length, resolution, barLength, lineLength, posList.size() + 1);
			yIndex++;
		}
	}
	private void setPathsFromPropertiesFile (){
		try (InputStream input = new FileInputStream(RepetitionAnalysisGameFrame.propertiesPath)){
			Properties props = new Properties();
			props.load(input);
			String graphicPath = props.getProperty("graphics_path");
			linePath = graphicPath + "repSchemaLine.png";
			dotPath = graphicPath + "repSchemaDot.png";
			barPath = graphicPath + "repSchemaBar.png";
		} catch (IOException ex){
			ex.printStackTrace();
		}	
	}
	private void makeRuler(int yIndex, double length, double resolution, double barLength, int lineLength, int yInc) {
		
		GridBagConstraints gbc = new GridBagConstraints();
		JLabel label;
		int x = 0;
		

		for (double d = 0.0; d < length; d += resolution){
			//System.out.println("d=" + d);
			if (x * resolution >= lineLength * barLength){
				x = 0;
				yIndex += yInc;
			}
			gbc.gridy = yIndex;
			gbc.gridx = x;
			label = new JLabel();
			if (d % barLength > 0){
				label.setIcon(getScaledIcon(linePath));
			} else {
				label.setIcon(getScaledIcon(barPath));
			}
			
		
			add(label, gbc);
			x++;

		}
		
	}
	private void makeTextRuler(int yIndex, double length, double resolution, double barLength, int lineLength, int yInc) {
		
		GridBagConstraints gbc = new GridBagConstraints();
		JLabel label;
		int x = 0;
		

		for (double d = 0.0; d < length; d += resolution){
			//System.out.println("d=" + d);
			if (x * resolution >= lineLength * barLength){
				x = 0;
				yIndex += yInc;
			}
			gbc.gridy = yIndex;
			gbc.gridx = x;
			label = new JLabel();
			label.setText(" ");
			label.setFont(new Font("TimesRoman", Font.PLAIN, 32));
			
		
			add(label, gbc);
			x++;

		}
		
	}
	private void makeSchema(int yIndex, Double[] posArr, double length, double resolution, double barLength, int lineLength, int yInc){
		GridBagConstraints gbc = new GridBagConstraints();
		JLabel label;
		int x = 0;
		for (double d = 0.0; d < length; d += resolution){
			//System.out.println("d=" + d);
			if (x * resolution >= lineLength * barLength){
				x = 0;
				yIndex += yInc;
			}
			gbc.gridy = yIndex;
			gbc.gridx = x;
			label = new JLabel();
			if (arrContains(posArr, d)){
				label.setIcon(getScaledIcon(dotPath));
			} else {
				if (d % barLength > 0){
					label.setIcon(getScaledIcon(linePath));
				} else {
					label.setIcon(getScaledIcon(barPath));
				}
			}
			
			
		
			add(label, gbc);
			x++;

		}
	}

	private boolean arrContains(Double[] posArr, double d) {
		for (Double DD: posArr){
			if (DD == d) return true;
		}
		return false;
	}
//	private void makeSchemaOld(int yIndex, Double[] posArr, double length, double resolution, double barLength, int lineLength, int yInc){
//		for (boolean b: arr){
//			System.out.println(b);
//		}
//		GridBagConstraints gbc = new GridBagConstraints();
//		JLabel label;
//		int x = 0;
		
//		double pos = 0.0;
//		for (boolean b: arr){
//			//System.out.println("x=" + x);
//			if (x * resolution >= lineLength * barLength){
//				x = 0;
//				yIndex += yInc;
//			}
//			gbc.gridy = yIndex;
//			gbc.gridx = x;
//			label = new JLabel();
//			if (b){
//				label.setIcon(getScaledIcon(dotPath));
//			} else {
//				if (pos % barLength > 0){
//					label.setIcon(getScaledIcon(linePath));
//				} else {
//					label.setIcon(getScaledIcon(barPath));
//				}
//			}
//			add(label, gbc);
	//		x++;
//			pos += resolution;
//		}
		
//	}
	private ImageIcon getScaledIcon(String path) {
		ImageIcon icon = new ImageIcon(path);
		Image img = icon.getImage() ;  
		Image newimg = img.getScaledInstance(IMAGE_WIDTH , IMAGE_HEIGHT,  java.awt.Image.SCALE_SMOOTH) ;  
		icon = new ImageIcon( newimg );
		return icon;
	}

	private boolean[] makeArr(Double[] posArr, double length, double resolution) {
		boolean[] arr = new boolean[(int)(length / resolution)];
		for (double pos: posArr){
			if (pos < length){
				arr[(int)(pos / resolution)] = true;
			}			
		}
		
		return arr;
	}
	public void clear() {
		removeAll();
		
	}
	
	//ImageIcon icon2 = getScaledIcon(icon.getIconPath());
}
