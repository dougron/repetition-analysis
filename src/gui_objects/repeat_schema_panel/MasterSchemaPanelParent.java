package gui_objects.repeat_schema_panel;

import java.util.ArrayList;

import repetition_analysis.index_list.IndexList;

public interface MasterSchemaPanelParent {

	
	public void setAttachListContentInFeatureChooserPanel(ArrayList<IndexList> list);
	public void setLinkedFieldsInContentBrowser(String clipPath, String chordsPath, ArrayList<Integer> arrayList);
	public void setLinkedCellLengthIndexInContentBrowser(ArrayList<Integer> cellLengthSelections);
	public void updateRSGraphic();

}
