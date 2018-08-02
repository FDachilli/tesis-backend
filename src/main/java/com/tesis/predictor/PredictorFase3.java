package com.tesis.predictor;

import com.tesis.commons.Constants;
import com.tesis.predictor.grupo.PredictorFase3Grupo;

import org.weka.Weka;
import com.tesis.weka.WekaRoles;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

public class PredictorFase3 extends Predictor{

    public FaseResultados predecirFase3 (String filePath, String modelPred) throws Exception {

    	model = modelPred;
		pathGrupo = "C:\\Users\\franc\\Dropbox\\tesis-backend\\ResumenGrupoFase3.arff";
        FaseResultados results = new FaseResultados();
        String resultPath = Constants.FASES_FOLDER + Constants.FASE_TRES_FOLDER + Constants.PREDICTIONS_FOLDER +  String.valueOf(System.currentTimeMillis()) + "-" + modelPred + Constants.ARFF_FILE;
        results.setPath(resultPath);
        results.setLabeledInstances(predecir(filePath,
                resultPath
                ,"", "C:\\Users\\franc\\Dropbox\\tesis-backend\\modelos\\procesamientoFase3\\" + model + Constants.DAT_FILE, "3").toString());
        return results;
    }

    public Instances prepareArff(Instances arff, String attributesToRemove) throws Exception {
        //TODO ver despues cuando no vengan los arff armados. Vamos a tener que leer los atributos y armar el arff

        if (!attributesToRemove.isEmpty())
            arff = WekaRoles.removeAttributes(arff, attributesToRemove);

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(WekaRoles.classRolAttribute());
        attributes.add(WekaRoles.classTipoRolAttribute());
        attributes.add(new Attribute(Weka.NOMBRE, (ArrayList<String>) null));
        attributes.add(WekaRoles.classTipoRolCompanerosAttribute());
        attributes.add(WekaRoles.classRolCompanerosAttribute());

        for (int i=1; i<=12; i++){
            attributes.add(new Attribute("C"+i));
        }

        for (int i = 1; i<=4; i++){
            attributes.add(new Attribute("R"+i));
        }

        for (int i = 1; i<=2; i++){
            attributes.add(new Attribute("A"+i));
        }

        for (int i = 1; i<=3; i++){
            attributes.add(new Attribute("Horario"+i));
        }
        
        attributes.addAll(WekaRoles.getSymlogAttributes());

        attributes.add(new Attribute("cant_mensajes"));

        Instances sentencesDataset = new Instances("chat", attributes, 0);

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
        
        PredictorFase3Grupo predictorFase3Grupo = new PredictorFase3Grupo();
        return predictorFase3Grupo.predecir(pathGrupo, "3-5, 7-7", "C:\\Users\\franc\\Dropbox\\tesis-backend\\modelos\\procesamientoFase3Grupo\\" + model + Constants.DAT_FILE, sentencesDataset, 4, "3");
    }

}
