package com.tesis.predictor;

import java.io.IOException;
import java.text.ParseException;

import com.tesis.commons.Constants;
import com.tesis.commons.Util;
import com.tesis.organizador.OrganizadorPrediccion;
import com.tesis.parser.ParserPrediccion;

import weka.core.Instances;

public class PredictorFases {

    public String predecirFases (String file, String modelFase1, String modelFase2, String modelFase3) throws Exception {
    	String folderName = predecirFase1(file, modelFase1);
    	Instances fase2Results = predecirFase2(folderName, modelFase2);
        PredictorFase3 predictorFase3 = new PredictorFase3();
        Instances fase3Results = predictorFase3.predecirFase3(fase2Results, modelFase3, folderName);
        System.out.println (fase3Results.toString());
        Util.deleteFolder(folderName);
        return fase3Results.toString();
    }
    
    public String predecirFasesCompuesto (String file, String modelFase1, String modelFase2, String modelFase3c1, String modelFase3c2, String modelFase3c3) throws Exception {
    	String folderName = predecirFase1(file, modelFase1);
    	Instances fase2Results = predecirFase2(folderName, modelFase2);
        PredictorFase3Compuesto predictorFase3 = new PredictorFase3Compuesto();
        Instances fase3Results = predictorFase3.predecirFase3Compuesto(fase2Results, modelFase3c1, modelFase3c2, modelFase3c3, folderName);
        System.out.println (fase3Results.toString());
        Util.deleteFolder(folderName);
        return fase3Results.toString();
    }
    
    public String predecirFase1(String file, String modelFase1) throws ParseException, IOException {
    	ParserPrediccion parserPrediccion = new ParserPrediccion();
    	String folderName =  parserPrediccion.parseJsonParcial(file, modelFase1);
        OrganizadorPrediccion organizadorPrediccion = new OrganizadorPrediccion();
		organizadorPrediccion.organizar_carpeta(folderName, folderName + "resumen.arff");
		return folderName;
    }
    
    public Instances predecirFase2 (String folderName, String modelFase2) throws Exception {
    	PredictorFase2 predictorFase2 = new PredictorFase2();
        return predictorFase2.predecirFase2(folderName + "resumen.arff", modelFase2, folderName);
    }

}
