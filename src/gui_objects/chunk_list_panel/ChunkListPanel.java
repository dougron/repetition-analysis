package gui_objects.chunk_list_panel;

import javax.swing.JPanel;

import repetition_analysis.note_chunk.ChunkList;
import repetition_analysis.repeat_schema_list.RepeatSchemaList;

import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JList;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class ChunkListPanel extends JPanel {

	private ChunkList cl;
	JList chunkdata;

	/**
	 * Create the panel.
	 */
	public ChunkListPanel(ChunkList cl) {
		this.cl = cl;
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{400, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		ChunkList_JList chunklist = new ChunkList_JList(cl.nameArray(), cl);
		chunklist.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				ChunkList_JList jl = (ChunkList_JList)event.getSource();
				int index = jl.getSelectedIndex();
				chunkdata.setListData(jl.cl.getNoteChunk(index).toStringArray());
			}
		});
		chunklist.setMinimumSize(new Dimension(200, 300));
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 0;
		add(chunklist, gbc_list);
		
		chunkdata = new JList();
		chunkdata.setMinimumSize(new Dimension(200, 300));
		GridBagConstraints gbd_list = new GridBagConstraints();
		gbd_list.fill = GridBagConstraints.BOTH;
		gbd_list.gridx = 1;
		gbd_list.gridy = 0;
		add(chunkdata, gbd_list);
	}

}
