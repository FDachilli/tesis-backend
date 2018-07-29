package com.tesis.predictor;

import com.tesis.commons.Constants;
import org.weka.Weka;
import com.tesis.weka.WekaRoles;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

public class PredictorFase2 extends Predictor{


    public FaseResultados predecirFase2 (String filePath, String clasificador) throws Exception {

            FaseResultados results = new FaseResultados();
            String resultPath = Constants.FASES_FOLDER + Constants.FASE_DOS_FOLDER + Constants.PREDICTIONS_FOLDER +  String.valueOf(System.currentTimeMillis()) + "-" + clasificador + Constants.ARFF_FILE;
            results.setPath(resultPath);
            results.setLabeledInstances(predecir(filePath,
                    resultPath
                    ,"2-4, 6-17, 27-47,66-68", Constants.FASES_FOLDER + Constants.FASE_DOS_FOLDER + Constants.MODELS_FOLDER + clasificador + Constants.DAT_FILE, "2").toString());
            return results;
    }

    public Instances prepareArff(Instances arff, String attributesToRemove) throws Exception {
        //TODO ver despues cuando no vengan los arff armados. Vamos a tener que leer los atributos y armar el arff

        arff = WekaRoles.removeAttributes(arff, attributesToRemove);

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(WekaRoles.classTipoRolAttribute());
        attributes.add(new Attribute(Weka.NOMBRE, (ArrayList<String>) null));
        //attributes.addAll(WekaRoles.getRolesCompanerosAttributes());

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

        attributes.add(new Attribute("cant_mensajes"));

        Instances sentencesDataset = new Instances(arff, 0);

        for (int i = 0; i < arff.numInstances(); i++) {

            Instance instance = arff.instance(i);
            int instanceIndex = 1;
            String tipo_rol = "?";

            String nombre = instance.stringValue(instanceIndex++);
            Double finalizador_companeros = instance.value(instanceIndex++);
            Double impulsor_companeros = instance.value(instanceIndex++);
            Double cerebro_companeros = instance.value(instanceIndex++);
            Double colaborador_companeros = instance.value(instanceIndex++);
            Double especialista_companeros = instance.value(instanceIndex++);
            Double implementador_companeros = instance.value(instanceIndex++);
            Double monitor_companeros = instance.value(instanceIndex++);
            Double investigador_companeros = instance.value(instanceIndex++);
            Double coordinador_companeros = instance.value(instanceIndex++);

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

            Double cant_mensajes = instance.value(instanceIndex);


            int valuesIndex = 0;
            double[] values = new double[sentencesDataset.numAttributes()];
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(tipo_rol);

            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).addStringValue(nombre);
            values[valuesIndex++] = finalizador_companeros;
            values[valuesIndex++] = impulsor_companeros;
            values[valuesIndex++] = cerebro_companeros;
            values[valuesIndex++] = colaborador_companeros;
            values[valuesIndex++] = especialista_companeros;
            values[valuesIndex++] = implementador_companeros;
            values[valuesIndex++] = monitor_companeros;
            values[valuesIndex++] = investigador_companeros;
            values[valuesIndex++] = coordinador_companeros;

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
            values[valuesIndex]= cant_mensajes;

            Instance newInstance = new DenseInstance(1.0, values);
            if (values[0] == -1.0)
                newInstance.setMissing(sentencesDataset.attribute(0));

            sentencesDataset.add(newInstance);

        }
        return sentencesDataset;
    }

}
