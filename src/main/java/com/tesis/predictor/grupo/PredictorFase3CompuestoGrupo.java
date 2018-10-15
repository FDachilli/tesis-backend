package com.tesis.predictor.grupo;

import java.io.File;

import com.tesis.commons.Constants;
import weka.core.Instance;
import weka.core.Instances;

public class PredictorFase3CompuestoGrupo extends PredictorFase3Grupo {
	
	/**
     * Predice fases compuesto
     * @param file archivo para predecir
     * @param modelc1 modelo de clasificador 1
     * @param modelc2 modelo de clasificador 2
     * @param modelc3 modelo de clasificador 3
     * @return Instances con prediccion
     */
	public Instances predecir(String folderName, String modelc1, String modelc2, String modelc3, Instances labeledDataset) throws Exception {
		Instances retorno = null;
		Instances clas1 = predecir(folderName + "fase2Grupo" + Constants.ARFF_FILE, "", System.getProperty("user.dir") + File.separator + Constants.CLAS1_MODELS_GRUPO + modelc1 + Constants.DAT_FILE, labeledDataset, 4, null, null, "2", "2,3");
		retorno = combinarResultados(retorno, clas1, labeledDataset, "4", "2,3");
    	Instances clas2 = predecir(folderName + "fase2Grupo" + Constants.ARFF_FILE, "", System.getProperty("user.dir") + File.separator + Constants.CLAS2_MODELS_GRUPO + modelc2 + Constants.DAT_FILE, labeledDataset, 4, null, null, "2", "1,3");
    	retorno = combinarResultados(retorno, clas2, labeledDataset, "4", "1,3");
    	Instances clas3 = predecir(folderName + "fase2Grupo" + Constants.ARFF_FILE, "", System.getProperty("user.dir") + File.separator + Constants.CLAS3_MODELS_GRUPO + modelc3 + Constants.DAT_FILE, labeledDataset, 4, null, null, "2", "1,2");
    	retorno = combinarResultados(retorno, clas3, labeledDataset, "4", "1,2");
    	return retorno;
	}

	/**
     * Agrega las instancias mergeadas de los grupos al dataset general
     * @param result Instances Dataset concatenado
     * @param grupo Instances Dataset de la clasificacion de grupo
     * @param general Instances Dataset general al que se le agrega el valor calculado en grupo
     * @return Instances Dataset
	 * @throws Exception 
     */
	public Instances combinarResultados (Instances result ,Instances grupo, Instances general, String attributeIndex, String nominalIndices) throws Exception {	  
		  
		general = removeInstances(general, attributeIndex, nominalIndices);
		general.setClassIndex(4);
		for (int i = 0; i < grupo.numInstances(); i++) {
			Instance instance = grupo.instance(i);
			String rol_companeros = instance.stringValue(0);
            double label = grupo.attribute(0).indexOfValue(rol_companeros);
            general.instance(i).setClassValue(label);
            if (result != null) {
	            result.add(general.instance(i));
	            //Para solucionar el problema del nombre cuando agrego instancias.
	            String nombre = general.instance(i).stringValue(2);
	            result.instance(result.numInstances()-1).setValue(2, nombre);
            }
        }
		if (result == null && general.numInstances() > 0) {
			result = new Instances(general);
		}
		return result;
	}
}
