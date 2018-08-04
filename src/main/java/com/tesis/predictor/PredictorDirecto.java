package com.tesis.predictor;

import com.tesis.commons.Constants;
import org.apache.commons.io.FileUtils;
import org.weka.Weka;
import com.tesis.organizador.Organizador;
import com.tesis.organizador.OrganizadorPrediccion;
import com.tesis.parser.entrenamiento.DirectoParserEntrenamiento;
import com.tesis.parser.prediccion.ParserPrediccionDirecto;
import com.tesis.predictor.grupo.PredictorDirectoGrupo;
import com.tesis.weka.WekaRoles;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
import java.util.ArrayList;

public class PredictorDirecto extends Predictor{

    public void predecirDirecto (String file, String modelPred, boolean total) throws Exception {
    	ParserPrediccionDirecto parserPrediccionDirecto = new ParserPrediccionDirecto();
        if (!total) {
    		parserPrediccionDirecto.parseJsonParcial(file);
        }else {
        	parserPrediccionDirecto.parseJsonTotal(file);
        }
        OrganizadorPrediccion organizadorPrediccion = new OrganizadorPrediccion();
		organizadorPrediccion.organizar_carpeta(Constants.TEMP_PRED_FOLDER_TO_ORG, Constants.TEMP_PRED_FOLDER_TO_ORG + "resumen.arff");
        model = modelPred;
        pathGrupo = "C:\\Users\\franc\\Dropbox\\tesis-backend\\ResumenGrupoDirecto.arff";
    	//TODO sacar el path de grupos del organizador
        Instances prediccionDirecta = predecir(Constants.TEMP_PRED_FOLDER_TO_ORG + "resumen.arff",
               "1-3, 5-5", "C:\\Users\\franc\\Dropbox\\tesis-backend\\modelos\\procesamientoDirecto\\" + model + Constants.DAT_FILE, "2");
        System.out.println(prediccionDirecta.toString());
        FileUtils.deleteDirectory(new File(Constants.TEMP_PRED_FOLDER_TO_ORG));
    }

    public Instances prepareArff(Instances arff, String attributesToRemove) throws Exception {
        //TODO ver despues cuando no vengan los arff armados. Vamos a tener que leer los atributos y armar el arff

        arff = WekaRoles.removeAttributes(arff, attributesToRemove);

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(WekaRoles.classRolAttribute());
        attributes.add(new Attribute(Weka.NOMBRE, (ArrayList<String>) null));
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

        Instances sentencesDataset = new Instances(arff, 0);

        for (int i = 0; i < arff.numInstances(); i++) {

            Instance instance = arff.instance(i);
            int instanceIndex = 1;
            String rol = "?";

            String nombre = instance.stringValue(instanceIndex++);
            String rol_companeros = "?";
            instanceIndex++;
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

            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).addStringValue(nombre);
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
            
            if (values[2] == -1.0)
                newInstance.setMissing(sentencesDataset.attribute(2));

            sentencesDataset.add(newInstance);

        }
        
        PredictorDirectoGrupo predictorDirectoGrupo = new PredictorDirectoGrupo();

        return predictorDirectoGrupo.predecir(pathGrupo, "2-4, 6-6",
        		"C:\\Users\\franc\\Dropbox\\tesis-backend\\modelos\\procesamientoDirectoGrupo\\" + model + Constants.DAT_FILE, sentencesDataset, 2, "3");
    }


	
}
