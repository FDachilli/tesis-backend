package com.tesis.predictor;

import com.tesis.weka.WekaRoles;

import weka.core.Instances;

public abstract class PredictorAbstracto {
	
	 protected WekaRoles wekaRoles = new WekaRoles();
	 
	 public abstract Instances prepareArff(Instances arff, String attributesToRemove, String folderName) throws Exception;

}
