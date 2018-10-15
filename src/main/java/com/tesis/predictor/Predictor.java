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

	/**
     * Predice los roles y tipos de rol
     * @param unlabeledFilePath path del archivo a partir del cual se predice
     * @param attributesToRemove posicion de los atributos a remover
     * @param pathModel path del modelo con el que se va a predecir
     * @param labeled instancias donde se va a cargar el valor predecido
     * @param indiceAttPred indice donde se va a cargar el valor predecido en labeled
     * @param posNombre posicion del atributo nombre
     * @param newPath path donde se va a almacenar el archivo resultado
     * @return Instances instancias predecidas
     */
    public Instances predecir(String unlabeledFilePath, String attributesToRemove, String pathModel, String posNombre, String folderName) throws Exception {

           return predecir(unlabeledFilePath, attributesToRemove, pathModel, posNombre, folderName, "", "");

    }
    
    /**
     * Predice los roles y tipos de rol removiendo instancias
     * @param unlabeledFilePath path del archivo a partir del cual se predice
     * @param attributesToRemove posicion de los atributos a remover
     * @param pathModel path del modelo con el que se va a predecir
     * @param labeled instancias donde se va a cargar el valor predecido
     * @param indiceAttPred indice donde se va a cargar el valor predecido en labeled
     * @param posNombre posicion del atributo nombre
     * @param newPath path donde se va a almacenar el archivo resultado
     * @param attributeIndex indice del atributo sobre el cual se van a eliminar instancias
     * @param nominalIndices valores nominales que se van a remover de las instancias
     * @return Instances instancias predecidas
     */
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
