package tests;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import gui_objects.repeat_schema_panel.RepeatSchemaGraphic;

public class RepeatSchemaGraphicTestFrame extends JFrame {
	
	
	public RepeatSchemaGraphicTestFrame(){
		setLayout(new BorderLayout());
		setSize(500, 200);
		ArrayList<Double[]> posList = makePosList();
		RepeatSchemaGraphic rsg = new RepeatSchemaGraphic(posList, 32.0, 0.5, 4.0, 4);
		rsg.setSize(new Dimension(200, 200));
		add(new JScrollPane(rsg), BorderLayout.CENTER);
	}
	
	
	

	private ArrayList<Double[]> makePosList() {
		ArrayList<Double[]> list = new ArrayList<Double[]>();
		list.add(new Double[]{0.0, 1.5, 3.5, 6.0, 9.5, 11.5, 12.0, 18.0, 22.5, 25.5});
		list.add(new Double[]{0.5, 5.5, 7.5, 14.0, 20.5, 29.0});
		list.add(new Double[]{0.5, 5.5, 7.5, 14.0, 20.5, 29.0});
		
		return list;
	}




	// main =============================================================================
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	RepeatSchemaGraphicTestFrame ex = new RepeatSchemaGraphicTestFrame();
                ex.setVisible(true);
            }
        });

	}

}
