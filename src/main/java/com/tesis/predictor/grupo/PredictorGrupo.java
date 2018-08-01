package com.tesis.predictor.grupo;

import org.weka.Weka;

import com.tesis.predictor.PredictorAbstracto;
import com.tesis.weka.WekaRoles;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public abstract class PredictorGrupo extends PredictorAbstracto{
	
    public Instances predecir(String unlabeledFilePath, String attributesToRemove, String pathModel, Attribute attPred, Instances labeled) throws Exception {
    	//TODO una carpeta modelos y despues que se divida en directos y en fases.
        Instances unlabeled = Weka.loadDataset(unlabeledFilePath);

        unlabeled = prepareArff(unlabeled, attributesToRemove);

        Classifier cls = wekaRoles.loadModel(pathModel);
        // set class attribute
        unlabeled.setClassIndex(0);
        //TODO por parametro !!!!
        labeled.setClassIndex(2);
        
        unlabeled = WekaRoles.removeAttributes(unlabeled, "2");

        // label instances
        for (int i = 0; i < unlabeled.numInstances(); i++) {
            double clsLabel = cls.classifyInstance(unlabeled.instance(i));
            //labeled.instance(i).setValue(attPred, clsLabel);
            labeled.instance(i).setClassValue(clsLabel);
        }

        // Save newly labeled data
       // ConverterUtils.DataSink.write(labeledFilePath, labeled);
        return labeled;

    }

	@Override
	public abstract Instances prepareArff(Instances arff, String attributesToRemove) throws Exception;

}
