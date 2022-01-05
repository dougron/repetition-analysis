package gui_objects.main_ui;

import ResourceUtils.ChordForm;

public interface RepetitionAnalysisParent {

	public ChordForm cf();
	public boolean parameterHasChanged();
	public void setParameterHasChanged(boolean b);
	public boolean isCalculating();
	public void setMasterRepeatSchemaPanelBarCount(int barCount);
	public void updateRSGraphic();

}
