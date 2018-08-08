package com.tesis.predictor.grupo;

import org.weka.Weka;

import com.tesis.predictor.PredictorAbstracto;
import com.tesis.weka.WekaRoles;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public abstract class PredictorGrupo extends PredictorAbstracto{
	
    public Instances predecir(String unlabeledFilePath, String attributesToRemove, String pathModel, Instances labeled, int indiceAttPred, String posNombre, String newPath) throws Exception {
    
        Instances unlabeled = Weka.loadDataset(unlabeledFilePath);

        unlabeled = prepareArff(unlabeled, attributesToRemove);

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
            labeled.instance(i).setClassValue(clsLabel);
        }

        if (newPath != null) {
        	Weka.saveDataset(unlabeled, newPath);
        }
        
        return labeled;

    }

	@Override
	public abstract Instances prepareArff(Instances arff, String attributesToRemove) throws Exception;

}
