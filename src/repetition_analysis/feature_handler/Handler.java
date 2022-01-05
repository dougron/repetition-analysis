package repetition_analysis.feature_handler;

public class Handler {

	private String name;
	private String shortName;
	public boolean isUsable = true;
	public boolean needsAnalysisChordForm = true;
	public boolean needsOutputChordForm = true;
	private int type;
	private boolean needsContourFeature;
	private String description = "";

	
	public Handler(
			String name, 
			String shortName, 
			boolean needsAnalysisChords, 
			boolean needsOutputChords, 
			int type,
			boolean needsContourFeature){
		this.name = name;
		this.shortName = shortName;
		this.needsAnalysisChordForm = needsAnalysisChords;
		this.needsOutputChordForm = needsOutputChords;
		this.type = type;
		this.needsContourFeature = needsContourFeature;
	}
	public Handler(
			String name, 
			String shortName, 
			boolean needsAnalysisChords, 
			boolean needsOutputChords, 
			int type){
		this.name = name;
		this.shortName = shortName;
		this.needsAnalysisChordForm = needsAnalysisChords;
		this.needsOutputChordForm = needsOutputChords;
		this.type = type;
		this.needsContourFeature = false;
	}
	
	public void setName(String n){
		name = n;
	}
	public String name() {
		return name;
	}
	public void setShortName(String n){
		shortName = n;
	}
	public String shortName(){
		return shortName;
	}
	public boolean isUsable(){
		return isUsable;
	}
	public boolean needsAnalysisChordForm(){
		return needsAnalysisChordForm;
	}
	public boolean needsOutputChordForm(){
		return needsOutputChordForm;
	}
	public int type(){
		return type;
	}
	public boolean needsContourFeature(){
		return needsContourFeature;
	}
	public boolean isPolyphonic(){
		return false;
	}
	public void setDescription(String str){
		description = str;
	}
	public String description(){
		return description;
	}
	
	public static final int MELODY_TYPE = 0;
	public static final int CONTOUR_TYPE = 1;
	public static final int DYNAMICS_TYPE = 2;
	public static final int DURATION_TYPE = 3;
	public static final int RHYTHM_TYPE = 4;

}
