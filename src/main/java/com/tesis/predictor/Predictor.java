package com.tesis.predictor;

import org.weka.Weka;
import com.tesis.weka.WekaRoles;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemoveWithValues;


public abstract class Predictor extends PredictorAbstracto{

	protected String model;

    public Instances predecir(String unlabeledFilePath, String attributesToRemove, String pathModel, String posNombre, String folderName) throws Exception {

           return predecir(unlabeledFilePath, attributesToRemove, pathModel, posNombre, folderName, "", "");

    }
    
    public Instances predecir(String unlabeledFilePath, String attributesToRemove, String pathModel, String posNombre, String folderName, String attributeIndex, String nominalIndices) throws Exception {

        Instances unlabeled = Weka.loadDataset(unlabeledFilePath);
        unlabeled = prepareArff(unlabeled, attributesToRemove, folderName);
        Classifier cls = wekaRoles.loadModel(pathModel);
        if(!attributeIndex.isEmpty() && !nominalIndices.isEmpty()){
        	unlabeled = removeInstances(unlabeled, attributeIndex, nominalIndices);
        }
        // set class attribute
        unlabeled.setClassIndex(0);

        // create copy
        Instances labeled = new Instances(unlabeled);

        unlabeled = WekaRoles.removeAttributes(unlabeled, posNombre);

        // label instances
        for (int i = 0; i < unlabeled.numInstances(); i++) {
            double clsLabel = cls.classifyInstance(unlabeled.instance(i));
            labeled.instance(i).setClassValue(clsLabel);
        }

        return labeled;

    }
    

	@Override
	public abstract Instances prepareArff(Instances arff, String attributesToRemove, String folderName) throws Exception;


}
