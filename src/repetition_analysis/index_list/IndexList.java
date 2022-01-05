package repetition_analysis.index_list;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

/*
 * wraps an ArrayList of Integers and returns some stats about them
 */
public class IndexList {

	private ArrayList<Integer> iList = new ArrayList<Integer>();
	private HashSet<Integer> iSet = new HashSet<Integer>();
	private int featureID;
	
	public IndexList(int featureID){
		this.featureID = featureID;
	}
	public void add(int i){
		iList.add(i);
		iSet.add(i);
	}
	public int size(){
		return iList.size();
	}
	public int setSize(){
		return iSet.size();
	}
	public double variety(){
		// a larger number (tending towards 1.0) indicates a large variety of item, 
		// smaller number (tending towards 0.0)  indicates less variety
		return (double)(iSet.size() - 1) / (iList.size() - 1);
	}
	public String listToString() {
		String str = "";
		for (int i: iList){
			str += i + ",";
		}
		return str;
	}
	public int[] getiList(){
		int[] arr = new int[iList.size()];
		for (int i = 0; i < iList.size(); i++){
			arr[i] = iList.get(i);
		}
		return arr;
	}
	
// comparators -------------------------------------------------------------------------------
	public static Comparator<IndexList> varietyComparator = new Comparator<IndexList>(){
		public int compare(IndexList note1, IndexList note2){
			if (note1.variety() < note2.variety()) return -1;
			if (note1.variety() > note2.variety()) return 1;
			return 0;
		}
	};

	public int featureID() {
		return featureID;
	}
}
