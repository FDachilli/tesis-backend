package com.tesis.commons;


import org.ipa.GroupAnalysisResult;
import org.ipa.GroupAnalysisRow;
import org.ipa.IpaAnalysis;
import org.json.JSONException;
import org.processDataset.DirectProcessing;
import org.processDataset.PhasesProcessingSingleClassifier;
import org.processDataset.ProcessDataset;
import org.weka.*;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class IpaClasiffier {

    public String parseConductaDirecto (String file) throws ParseException, FileNotFoundException, JSONException {

        Weka weka = new WekaSMO(10, 1, 3);
        ProcessDataset process = new DirectProcessing(weka, true, true);
        //TODO path como properties o constant
        return process.classify(file, "");

    }


    public String parseConductaFases (String file) throws ParseException, FileNotFoundException, JSONException{
        Weka weka = new WekaDecisionTable(10, 1, 3);
        PhasesProcessingSingleClassifier process = new PhasesProcessingSingleClassifier(weka, true, true);
        return process.classify(file, "");
    }
    
    
    public List<String> getConflictos (String path){
    	//Solo se analiza de a un grupo
    	List<String> conflictos = new ArrayList<>();
    	List<String> paths =  new ArrayList<String>() {{
    			    add(path);}};
    	IpaAnalysis ipaAnalysis = new IpaAnalysis(paths);
    	List<GroupAnalysisResult> groupAnalysisResults = ipaAnalysis.analizeGroups();
    	if (groupAnalysisResults != null && groupAnalysisResults.size()>0) {
    		for (GroupAnalysisRow result: groupAnalysisResults.get(0).getAnalysisResults()) {
    			  float porcentaje = result.getPercentage() != null ? Float.parseFloat(result.getPercentage().split("%")[0].replace(",", ".")) : 0;
    			  float limiteInferior = result.getInfLimit() != null ?  Float.parseFloat(result.getInfLimit().split("%")[0]) : 0;
    			  float limiteSuperior =  result.getSupLimit() != null ?  Float.parseFloat(result.getSupLimit().split("%")[0]) : 0;
    			  if (porcentaje < limiteInferior || porcentaje > limiteSuperior) {
    				  conflictos.add(result.getConflict());
    			  }
    		}
    	}
    	return null;
    }


}
