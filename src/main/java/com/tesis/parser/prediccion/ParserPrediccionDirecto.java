package com.tesis.parser.prediccion;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.weka.Weka;

import com.tesis.commons.Constants;
import com.tesis.hangouts.Atributos;
import com.tesis.weka.WekaRoles;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class ParserPrediccionDirecto extends ParserPrediccion{
	
	public void agregarAtributos(String pathfile, List<Atributos> lista_atributos) throws IOException {

        Instances dataset = Weka.loadDataset(pathfile);
        ArrayList<Attribute> attributes = new ArrayList<>();
        //attributes.add(WekaRoles.classRolAttribute());
        attributes.add(Weka.classConductaAttribute());
        // Atributo class_reaccion
        Attribute attClassReaccion = Weka.classReaccionAttribute();
        // Atributo class_area
        Attribute attClassArea = Weka.classAreaAttribute();
        attributes.add(attClassReaccion);
        attributes.add(attClassArea);
        Attribute attNombre = new Attribute(Weka.NOMBRE, (ArrayList<String>) null);
        attributes.add(attNombre);
        Attribute attFecha = new Attribute("fecha","yyyy-MM-dd HH:mm:ss");

        attributes.add(attFecha);

        Instances sentencesDataset = new Instances("chat", attributes, 0);


        for (int i = 0; i < dataset.numInstances(); i++) {
            Instance instance = dataset.instance(i);
            int instanceIndex = 0;
            String conducta = instance.stringValue(instanceIndex++);
            String classReaction = Constants.reacciones.get(Integer.parseInt(conducta));
            String classArea = Constants.areas.get(Integer.parseInt(conducta));
            String nombre = instance.stringValue(instanceIndex++);
            int valuesIndex = 0;
            double[] values = new double[attributes.size()];
            //values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue("?");
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(conducta);
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(classReaction);
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(classArea);
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).addStringValue(nombre);

            try {
                values[valuesIndex++] = sentencesDataset.attribute("fecha").parseDate(lista_atributos.get(i).getFecha());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            Instance newInstance = new DenseInstance(1.0, values);
            if (values[0] == -1.0)
                newInstance.setMissing(sentencesDataset.attribute(0));

            sentencesDataset.add(newInstance);

        }
        Weka.saveDataset(sentencesDataset, Constants.TEMP_PRED_FOLDER_TO_ORG + System.currentTimeMillis() + "-roles" + Constants.ARFF_FILE);
    }

}
