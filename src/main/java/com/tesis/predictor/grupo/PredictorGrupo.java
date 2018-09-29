package com.tesis.predictor.grupo;

import org.weka.Weka;

import com.tesis.predictor.PredictorAbstracto;
import com.tesis.weka.WekaRoles;

import weka.classifiers.Classifier;
import weka.core.Instances;

public abstract class PredictorGrupo extends PredictorAbstracto{
	
    public Instances predecir(String unlabeledFilePath, String attributesToRemove, String pathModel, Instances labeled, int indiceAttPred, String posNombre, String newPath) throws Exception {
    
        return predecir(unlabeledFilePath, attributesToRemove, pathModel, labeled, indiceAttPred, posNombre, newPath, "", "");

    }
    
    public Instances predecir(String unlabeledFilePath, String attributesToRemove, String pathModel, Instances labeled, int indiceAttPred, String posNombre, String newPath, String attributeIndex, String nominalIndices) throws Exception {
        
        Instances unlabeled = Weka.loadDataset(unlabeledFilePath);

        unlabeled = prepareArff(unlabeled, attributesToRemove, null);
        if(!attributeIndex.isEmpty() && !nominalIndices.isEmpty()){
        	unlabeled = removeInstances(unlabeled, attributeIndex, nominalIndices);
        }

        Classifier cls = wekaRoles.loadModel(pathModel);
        // set class attribute
        unlabeled.setClassIndex(0);
        labeled.setClassIndex(indiceAttPred);
        
        if (posNombre != null)
        	unlabeled = WekaRoles.removeAttributes(unlabeled, posNombre);

        // label instances
        for (int i = 0; i < unlabeled.numInstances(); i++) {
            double clsLabel = cls.classifyInstance(unlabeled.instance(i));
            unlabeled.instance(i).setClassValue(clsLabel);
            if(attributeIndex.isEmpty() && nominalIndices.isEmpty()){
            	labeled.instance(i).setClassValue(clsLabel);
            }
        }

        if (newPath != null) {
        	Weka.saveDataset(unlabeled, newPath);
        }
        
        if(!attributeIndex.isEmpty() && !nominalIndices.isEmpty()){
        	//Cuando es en cascada se retorna el archivo de grupo, para mergear los resultados y ahi recien pasar los resultados al general
        	return unlabeled;
        }else {
        	 return labeled;
        }

    }

	@Override
	public abstract Instances prepareArff(Instances arff, String attributesToRemove, String folderName) throws Exception;

}
