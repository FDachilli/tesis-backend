package com.tesis.predictor.grupo;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.weka.Weka;

import com.tesis.commons.Constants;
import com.tesis.organizador.Organizador;
import com.tesis.parser.DirectoParser;
import com.tesis.parser.GrupoDirectoParser;
import com.tesis.predictor.Predictor;
import com.tesis.weka.WekaRoles;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class PredictorDirectoGrupo extends PredictorGrupo {
	
	 public void predecirDirectoGrupo (String filePath, String model) throws Exception {

	        if (filePath.contains(Constants.JSON_FILE)) {
	            //Limpio directorio para despues no procesar archivos viejos
	            FileUtils.cleanDirectory(new File(Constants.DIRECTO_LABELED_FOLDER));
	            //Lee el json y divide las conversaciones dejando los resultados en "results\labeled\Directo"
	            GrupoDirectoParser parser = new GrupoDirectoParser();
	            parser.parseJson(filePath);
	            Organizador organizador = new Organizador();
	            organizador.orgainzar_carpeta(Constants.DIRECTO_LABELED_FOLDER, "./ResumenDirecto.arff");
	        }
	       /* else{
	            if (filePath.contains(Constants.ARFF_FILE)) {
	                IpaClasiffier ipaClasiffier = new IpaClasiffier();
	                DirectoParser parser = new DirectoParser();
	                //TENGO QUE HACER UNA METODO PARSE ARFF QUE ARME LA LISTA DE ATRIBUTOS.
	                //VOY A NECESITA UNA HASH CON LOS ROLES PERO CON EL NOMBRE COMO CLAVEEEEEEEEE, Y HACER TODO JUNTO EN ALGO CON EL ESTILO AGREGAR ATRIBUTOS
	               // parser.agregarAtributos(ipaClasiffier.parseConductaDirecto(filePath), );


	            }
	        }*/


	          /* Instances prediccionGrupo = predecir(filePath,
	                   Constants.PREDICTIONS_DIRECTO_FOLDER + String.valueOf(System.currentTimeMillis()) + "-" + model+ Constants.ARFF_FILE,
	                   "2-4, 6-6", "C:\\Users\\franc\\Dropbox\\tesis-backend\\modelos\\procesamientoDirectoGrupo\\" + model + Constants.DAT_FILE,
	                   "2");
	           System.out.println(prediccionGrupo.toString());*/

	    }

	@Override
	public Instances prepareArff(Instances arff, String attributesToRemove) throws Exception {
     
        arff = WekaRoles.removeAttributes(arff, attributesToRemove);

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(WekaRoles.classRolAttribute());
        attributes.add(new Attribute(Weka.NOMBRE, (ArrayList<String>) null));

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

            sentencesDataset.add(newInstance);

        }

        return sentencesDataset;
    }

}
