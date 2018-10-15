package com.tesis.predictor.grupo;

import org.weka.Weka;

import com.tesis.predictor.PredictorAbstracto;
import com.tesis.weka.WekaRoles;

import weka.classifiers.Classifier;
import weka.core.Instances;

public abstract class PredictorGrupo extends PredictorAbstracto{
	
	/**
     * Predice los roles y tipos de rol de grupo
     * @param unlabeledFilePath path del archivo a partir del cual se predice
     * @param attributesToRemove posicion de los atributos a remover
     * @param pathModel path del modelo con el que se va a predecir
     * @param labeled instancias donde se va a cargar el valor predecido
     * @param indiceAttPred indice donde se va a cargar el valor predecido en labeled
     * @param posNombre posicion del atributo nombre
     * @param newPath path donde se va a almacenar el archivo resultado
     * @return Instances instancias predecidas
     */
    public Instances predecir(String unlabeledFilePath, String attributesToRemove, String pathModel, Instances labeled, int indiceAttPred, String posNombre, String newPath) throws Exception {
    
        return predecir(unlabeledFilePath, attributesToRemove, pathModel, labeled, indiceAttPred, posNombre, newPath, "", "");

    }
    
    /**
     * Predice los roles y tipos de rol de grupo removiendo instancias
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
