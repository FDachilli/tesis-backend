package com.tesis.predictor;

import org.weka.Weka;
import com.tesis.weka.WekaRoles;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;


public abstract class Predictor extends PredictorAbstracto{

	protected String pathGrupo;
	protected String model;

    public Instances predecir(String unlabeledFilePath, String attributesToRemove, String pathModel, String posNombre) throws Exception {
    	//TODO una carpeta modelos y despues que se divida en directos y en fases.
        Instances unlabeled = Weka.loadDataset(unlabeledFilePath);

        unlabeled = prepareArff(unlabeled, attributesToRemove);

        //Classifier cls = wekaRoles.loadModel("results\\procesamientoDirecto\\modelos\\" + namesClasificadores[j] + "-directo.dat");
        Classifier cls = wekaRoles.loadModel(pathModel);
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

        // Save newly labeled data
        //ConverterUtils.DataSink.write(labeledFilePath, labeled);
        return labeled;

    }
    

	@Override
	public abstract Instances prepareArff(Instances arff, String attributesToRemove) throws Exception;

   

}
