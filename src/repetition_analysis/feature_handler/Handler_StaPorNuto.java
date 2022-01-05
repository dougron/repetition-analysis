package repetition_analysis.feature_handler;
import DataObjects.combo_variables.IntAndString;
import StaticChordScaleDictionary.CSD;
import repetition_analysis.feature_chunk.FeatureChunk;
import repetition_analysis.note_chunk.NoteChunk;
import repetition_analysis.note_chunk.NoteChunkListObject;

public class Handler_StaPorNuto extends Handler implements FeatureHandler {
	
	private double DEFAULT_STACCATO = CSD.quantResolution("8n");

	public Handler_StaPorNuto(){
		super("STACCATO_PORTATO_TENUTO", "dur_spn", false, false, Handler.DURATION_TYPE);
		setDescription("Duration: staccato, portato or tenuto");
	}
	
	public FeatureInfo getInfo(FeatureChunk fc){
		//System.out.println("Handler_TopNoteChromaticChordTone----------------------------");
		return new FeatureInfo(getIntArr(fc), FeatureChunk.STACCATO_PORTATO_TENUTO);
	}
	
	private double[] getIntArr(FeatureChunk fc) {
		double[] darr = new double[fc.chunkList.size()];
		for (int i = 0; i < fc.chunkList.size(); i++){
			NoteChunk nc = fc.chunkList.get(i);
			NoteChunkListObject nextnc = nc.next();
			//System.out.println(nc.toString());
			//System.out.println("nc.position=" + nc.position + "nc.length=" + nc.length + " nextnc.position=" + nextnc.position());
			double d = 0.0;
			if (nextnc.position() - nc.length - nc.position < 0.05){	// effectively == 0.0 but allowing for some leeway
				d = 1.0;
			} else {
				if (nc.length < DEFAULT_STACCATO + 0.05){
					d = 0.0;
				} else {
					d = nc.length / (nextnc.position() - nc.position);
				}
			}
			darr[i] = d;
			
			
		}
		
		
		return darr;
	}

	public String shortToString(FeatureChunk fc) {
		return shortName() + ":" + getInfo(fc).getFeatureString();
	}
}