package gui_objects;

import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class ListAndModel {

	
	private DefaultListModel model;
	private JList list;

	public ListAndModel(){
		this.model = new DefaultListModel();
		this.list = new JList(model);
	}
	public JList list(){
		return list;
	}
	public DefaultListModel model(){
		return model;
	}
	public void setModelContent(ArrayList<String> strList){
		model.clear();
		for (String str: strList){
			model.addElement(str);
		}
	}
}
