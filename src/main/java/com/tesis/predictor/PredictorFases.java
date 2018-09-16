package com.tesis.predictor;

import com.tesis.commons.Constants;
import com.tesis.commons.Util;
import com.tesis.organizador.OrganizadorPrediccion;
import com.tesis.parser.prediccion.ParserPrediccion;

import weka.core.Instances;

public class PredictorFases {

    public String predecirFases (String file, String modelFase1, String modelFase2, String modelFase3, boolean total) throws Exception {
    	ParserPrediccion parserPrediccion = new ParserPrediccion();
        if (!total) {
    		parserPrediccion.parseJsonParcial(file, modelFase1);
        }else {
        	parserPrediccion.parseJsonTotal(file, modelFase1);
        }
        OrganizadorPrediccion organizadorPrediccion = new OrganizadorPrediccion();
		organizadorPrediccion.organizar_carpeta(Constants.TEMP_PRED_FOLDER_TO_ORG, Constants.TEMP_PRED_FOLDER_TO_ORG + "resumen.arff");
        PredictorFase2 predictorFase2 = new PredictorFase2();
        Instances fase2Results = predictorFase2.predecirFase2(Constants.TEMP_PRED_FOLDER_TO_ORG + "resumen.arff", modelFase2);
        PredictorFase3 predictorFase3 = new PredictorFase3();
        Instances fase3Results = predictorFase3.predecirFase3(fase2Results, modelFase3);
        System.out.println (fase3Results.toString());
        Util.deleteFolder(Constants.USR_TEMP_FOLDER);
        return fase3Results.toString();
    }

}
