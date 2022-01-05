package gui_objects.feature_chooser_panel;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public interface FeatureSortForTreeParent {

	
	public void refreshSelectionDisplay();
	public DefaultMutableTreeNode root();
	public JTree tree();
}
