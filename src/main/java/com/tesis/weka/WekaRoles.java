package com.tesis.weka;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.meta.MultiClassClassifier;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.REPTree;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.Resample;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.weka.Weka;

public class WekaRoles {

	/**
     * Carga el modelo deseado
     * @param fileName path del archivo que contiene el modelo
     * @return AbstractClassifier
     */
    public AbstractClassifier loadModel(String fileName) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
            Object tmp = in.readObject();
            AbstractClassifier classifier = (AbstractClassifier) tmp;
            in.close();
            return classifier;
        } catch (Exception e) {
            System.out.println("Problem found when reading: " + fileName);
            return null;
        }
      
    }

    /**
     * Crea el atributo rol
     * @return Attribute
     */
    public static Attribute classRolAttribute() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("finalizador");
        labels.add("impulsor");
        labels.add("cerebro");
        labels.add("colaborador");
        labels.add("especialista");
        labels.add("implementador");
        labels.add("monitor");
        labels.add("investigador");
        labels.add("coordinador");
        Attribute attClassRol = new Attribute("class_rol", labels);
        return attClassRol;
    }

    /**
     * Crea el atributo rol companeros
     * @return Attribute
     */
    public static Attribute classRolCompanerosAttribute() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("finalizador");
        labels.add("impulsor");
        labels.add("cerebro");
        labels.add("colaborador");
        labels.add("especialista");
        labels.add("implementador");
        labels.add("monitor");
        labels.add("investigador");
        labels.add("coordinador");
        Attribute attClassRol = new Attribute("class_rol_companeros", labels);
        return attClassRol;
    }
    
    /**
     * Crea los atributos symlog
     * @return lista de Attribute con los atributos symlog
     */
    public static ArrayList<Attribute> getSymlogAttributes() {

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("dominante_symlog"));
        attributes.add(new Attribute("sumiso_symlog"));
        attributes.add(new Attribute("amistoso_symlog"));
        attributes.add(new Attribute("no_amistoso_symlog"));
        attributes.add(new Attribute("tarea_symlog"));
        attributes.add(new Attribute("socio_emocional_symlog"));
        return attributes;
    }

    /**
     * Crea el atributo tipo rol
     * @return Attribute
     */
    public static Attribute classTipoRolAttribute() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("social");
        labels.add("mental");
        labels.add("accion");

        Attribute attClassRol = new Attribute("class_tipo_rol", labels);
        return attClassRol;
    }

    /**
     * Crea el atributo tipo rol companeros
     * @return Attribute
     */
    public static Attribute classTipoRolCompanerosAttribute() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("social");
        labels.add("mental");
        labels.add("accion");

        Attribute attClassRol = new Attribute("class_tipo_rol_companeros", labels);
        return attClassRol;
    }

    /**
     * Remueve los atributos que no son de interes
     * @param arff archivo para aplicar filtro
     * @param attsToRemove posicion de los atributos a aplicar el filtro
     * @return Instances instancias con el filtro aplicado
     */
    public static Instances removeAttributes (Instances arff, String attsToRemove) throws Exception {

        String[] options = new String[2];
        options[0] = "-R";                                    // "range"
        options[1] = attsToRemove;
        Remove remove = new Remove();                         // new instance of filter
        remove.setOptions(options);
        remove.setInputFormat(arff);
        return Filter.useFilter(arff, remove);

    }

    
    /**
     * Mezcla las instancias de los archivos pasados como parámetro
     * @param clasificacion1 Instances Dataset
     * @param clasificacion2 Instances Dataset
     * @param clasificacion3 Instances Dataset
     * * @return Instances Merge Dataset
     */
    public static Instances mergeInstances(Instances clasificacion1, Instances clasificacion2, Instances clasificacion3) throws Exception {

        Instances merged = null;
        if (clasificacion1 != null)
            merged = clasificacion1;
        if (clasificacion2 != null)
            merged = merge(merged, clasificacion2);
        if (clasificacion3 != null)
            merged = merge(merged, clasificacion3);

        return merged;
    }

    /**
     * Mezcla las instancias pasadas como parámetro
     * @param data1 Instances Dataset
     * @param data2 Instances Dataset
     * @return Instances Dataset
     */
    private static Instances merge(Instances data1, Instances data2) throws Exception {
        int asize = data1.numAttributes();
        boolean strings_pos[] = new boolean[asize];
        for(int i=0; i<asize; i++)
        {
            Attribute att = data1.attribute(i);
            strings_pos[i] = ((att.type() == Attribute.STRING) || (att.type() == Attribute.NOMINAL));
        }

        Instances dest = new Instances(data1);
        dest.setRelationName(data1.relationName() + "+" + data2.relationName());

        DataSource source = new DataSource(data2);
        Instances instances = source.getStructure();

        Instance instance = null;
        while (source.hasMoreElements(instances)) {
            instance = source.nextElement(instances);
            dest.add(instance);
            for(int i=0; i<asize; i++) {
                if(strings_pos[i]) {
                    dest.instance(dest.numInstances()-1)
                            .setValue(i,instance.stringValue(i));
                }
            }
        }
        return dest;
    }

}
