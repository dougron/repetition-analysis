package gui_objects.feature_chooser_panel;

public interface FeaturePickerManagerParent {
	
	
	public void removeTab(FeaturePickerGamePanel fpPanel);
	public void addNewToRightOfTab(FeaturePickerGamePanel fpPanel);
	public void moveLeft(FeaturePickerGamePanel fpPanel);
	public void moveRight(FeaturePickerGamePanel fpPanel);

}
