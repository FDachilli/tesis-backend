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

    public String parseConductaDirecto (String file, String clas) throws ParseException, FileNotFoundException, JSONException {

    	Weka weka = getClassifier(clas);
        ProcessDataset process = new DirectProcessing(weka, true, true);
        return process.classify(file, "");

    }


    public String parseConductaFases (String file, String clas) throws ParseException, FileNotFoundException, JSONException{
        Weka weka = getClassifier(clas);
        PhasesProcessingSingleClassifier process = new PhasesProcessingSingleClassifier(weka, true, true);
        return process.classify(file, "");
    }
    
    public Weka getClassifier(String clas) {
    	   switch (clas) {
	           case "J48":
	               return new WekaJ48(10, 1, 3);
	           case "NaiveBayes":
	               return new WekaNaiveBayes(10, 1, 3);
	           case "SMO":
	               return new WekaSMO(10, 1, 3);
	           case "IBk":
	               return new WekaIBk(10, 1, 3);
	           case "KStar":
	               return new WekaKStar(10, 1, 3);
	           case "PART":
	               return new WekaPART(10, 1, 3);
	           case "JRip":
	               return new WekaJRip(10, 1, 3);
	           case "DecisionTable":
	               return new WekaDecisionTable(10, 1, 3);
	           default:
	               return new WekaSMO(10, 1, 3);
    	   }
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
