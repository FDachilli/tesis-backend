package com.tesis.predictor;

import com.tesis.weka.WekaRoles;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemoveWithValues;

public abstract class PredictorAbstracto {
	
	 protected WekaRoles wekaRoles = new WekaRoles();
	 
	 public abstract Instances prepareArff(Instances arff, String attributesToRemove, String folderName) throws Exception;
	 
	  
	    
    /**
     * Elimina instancias del dataset
     * @param dataset Instances Dataset del que se van a eliminar las instancias
     * @param attributeIndex String Ã�ndice del atributo a eliminar
     * @param nominalIndices String Valor nominal que se quiere eliminar 
     * @return Instances Nuevo dataset con las instancias que se quieren conservar
     * @throws Exception 
     */
    protected Instances removeInstances(Instances dataset, String attributeIndex, String nominalIndices) throws Exception {
        
        Instances newDataset = null;
        RemoveWithValues removeWithValuesFilter = new RemoveWithValues();
        removeWithValuesFilter.setAttributeIndex(attributeIndex);
        removeWithValuesFilter.setNominalIndices(nominalIndices);
        removeWithValuesFilter.setInputFormat(dataset);
        newDataset = Filter.useFilter(dataset, removeWithValuesFilter);
       
        return newDataset;
    }

}
