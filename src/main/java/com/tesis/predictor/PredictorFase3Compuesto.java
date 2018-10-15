package com.tesis.predictor;

import java.io.File;

import org.weka.Weka;

import com.tesis.commons.Constants;
import com.tesis.predictor.grupo.PredictorFase3CompuestoGrupo;
import com.tesis.weka.WekaRoles;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class PredictorFase3Compuesto extends Predictor{
	
	private Instances readyDataset;
	private String model2;
	private String model3;
	
	public PredictorFase3Compuesto () {
		super();
		readyDataset = null;
	}
	
	/**
     * Predice fase 3 compuesto
     * @param resultFase2 resultado de la prediccion de la fase 2
     * @param modelc1 modelo del clasificador 1 a utilizar para predecir
     * @param modelc2 modelo del clasificador 2 a utilizar para predecir
     * @param modelc3 modelo del clasificador 3 a utilizar para predecir
     * @param folderName nombre de la carpeta temporal asignada al proceso
     * @return Instances con predicciones
     */
   public Instances predecirFase3Compuesto (Instances resultFase2, String modelc1, String modelc2, String modelc3, String folderName) throws Exception {
	    model = modelc1;
	    model2 = modelc2;
	    model3 = modelc3;
    	String fase2TempPath = folderName + "fase2" + Constants.ARFF_FILE;
    	Weka.saveDataset(resultFase2, fase2TempPath);
    	Instances clas1 = predecir(fase2TempPath,"", System.getProperty("user.dir") + File.separator + Constants.CLAS1_MODELS + modelc1 + Constants.DAT_FILE, "3", folderName, "2", "2,3");
    	Instances clas2 = predecir(fase2TempPath,"", System.getProperty("user.dir") + File.separator + Constants.CLAS2_MODELS + modelc2 + Constants.DAT_FILE, "3", folderName, "2", "1,3");
    	Instances clas3 = predecir(fase2TempPath,"", System.getProperty("user.dir") + File.separator + Constants.CLAS3_MODELS + modelc3 + Constants.DAT_FILE, "3", folderName, "2", "1,2");
        return WekaRoles.mergeInstances(clas1, clas2, clas3);

    }

   /**
    * Prepara el archivo arff para predecir la fase 3 compuesto
    * @param arff instancias para preparar
    * @param attributesToRemove posicion de los atributos a remover
    * @param folderName nombre de la carpeta temporal del proceso actual
    * @return Instances instancias preparadas para predecir
    */
   public Instances prepareArff(Instances arff, String attributesToRemove, String folderName) throws Exception {

	   if (readyDataset == null) {
	       if (!attributesToRemove.isEmpty())
	           arff = WekaRoles.removeAttributes(arff, attributesToRemove);
	
	       Instances sentencesDataset = new Instances(arff, 0);
	       sentencesDataset.insertAttributeAt(WekaRoles.classRolAttribute(), 0);
	       sentencesDataset.insertAttributeAt(WekaRoles.classRolCompanerosAttribute(), 4);
	
	       for (int i = 0; i < arff.numInstances(); i++) {
	
	           Instance instance = arff.instance(i);
	           int instanceIndex = 0;
	           String rol = "?";
	           String tipo_rol = instance.stringValue(instanceIndex++);
	           String nombre = instance.stringValue(instanceIndex++);
	           String tipo_rol_companeros = instance.stringValue(instanceIndex++);
	           String rol_companeros = "?";
	           Double C1 = instance.value(instanceIndex++);
	           Double C2 = instance.value(instanceIndex++);
	           Double C3 = instance.value(instanceIndex++);
	           Double C4 = instance.value(instanceIndex++);
	           Double C5 = instance.value(instanceIndex++);
	           Double C6 = instance.value(instanceIndex++);
	           Double C7 = instance.value(instanceIndex++);
	           Double C8 = instance.value(instanceIndex++);
	           Double C9 = instance.value(instanceIndex++);
	           Double C10 = instance.value(instanceIndex++);
	           Double C11 = instance.value(instanceIndex++);
	           Double C12 = instance.value(instanceIndex++);
	
	           Double R1 = instance.value(instanceIndex++);
	           Double R2 = instance.value(instanceIndex++);
	           Double R3 = instance.value(instanceIndex++);
	           Double R4 = instance.value(instanceIndex++);
	
	           Double A1 = instance.value(instanceIndex++);
	           Double A2 = instance.value(instanceIndex++);
	
	           Double horario1 = instance.value(instanceIndex++);
	           Double horario2 = instance.value(instanceIndex++);
	           Double horario3 = instance.value(instanceIndex++);
	           
	           Double dominante = instance.value(instanceIndex++);
	           Double sumiso = instance.value(instanceIndex++);
	           Double amistoso = instance.value(instanceIndex++);
	           Double no_amistoso = instance.value(instanceIndex++);
	           Double tarea_symlog = instance.value(instanceIndex++);
	           Double socio_emocional_symlog = instance.value(instanceIndex++);
	
	           Double cant_mensajes = instance.value(instanceIndex);
	
	
	           int valuesIndex = 0;
	           double[] values = new double[sentencesDataset.numAttributes()];
	           values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(rol);
	           values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(tipo_rol);
	           values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).addStringValue(nombre);
	           values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(tipo_rol_companeros);
	           values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(rol_companeros);
	
	           values[valuesIndex++] = C1;
	           values[valuesIndex++] = C2;
	           values[valuesIndex++] = C3;
	           values[valuesIndex++] = C4;
	           values[valuesIndex++] = C5;
	           values[valuesIndex++] = C6;
	           values[valuesIndex++] = C7;
	           values[valuesIndex++] = C8;
	           values[valuesIndex++] = C9;
	           values[valuesIndex++] = C10;
	           values[valuesIndex++] = C11;
	           values[valuesIndex++] = C12;
	
	           values[valuesIndex++] = R1;
	           values[valuesIndex++] = R2;
	           values[valuesIndex++] = R3;
	           values[valuesIndex++] = R4;
	
	           values[valuesIndex++] = A1;
	           values[valuesIndex++] = A2;
	
	           values[valuesIndex++] = horario1;
	           values[valuesIndex++] = horario2;
	           values[valuesIndex++] = horario3;
	
	           values[valuesIndex++] = dominante;
	           values[valuesIndex++] = sumiso;
	           values[valuesIndex++] = amistoso;
	           values[valuesIndex++] = no_amistoso;
	           values[valuesIndex++] = tarea_symlog;
	           values[valuesIndex++] = socio_emocional_symlog;
	           
	           values[valuesIndex]= cant_mensajes;
	
	           Instance newInstance = new DenseInstance(1.0, values);
	           if (values[0] == -1.0)
	               newInstance.setMissing(sentencesDataset.attribute(0));
	           if (values[4] == -1.0)
	               newInstance.setMissing(sentencesDataset.attribute(4));
	
	           sentencesDataset.add(newInstance);
	           
	
	       }
	       
	       PredictorFase3CompuestoGrupo predictorFase3Grupo = new PredictorFase3CompuestoGrupo();
	       readyDataset = predictorFase3Grupo.predecir(folderName, model, model2, model3, sentencesDataset);
	   }
       return readyDataset;
   }

}
