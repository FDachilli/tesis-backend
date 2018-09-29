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


    public AbstractClassifier loadModel(String fileName) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
            Object tmp = in.readObject();
            AbstractClassifier classifier = (AbstractClassifier) tmp;
            in.close();
            return classifier;
        } catch (Exception e) {
            //TODO
            System.out.println("Problem found when reading: " + fileName);
        }
        return null;
    }

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

    public static Attribute classTipoRolAttribute() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("social");
        labels.add("mental");
        labels.add("accion");

        Attribute attClassRol = new Attribute("class_tipo_rol", labels);
        return attClassRol;
    }

    public static Attribute classTipoRolCompanerosAttribute() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("social");
        labels.add("mental");
        labels.add("accion");

        Attribute attClassRol = new Attribute("class_tipo_rol_companeros", labels);
        return attClassRol;
    }

    public static Instances removeAttributes (Instances arff, String attsToRemove) throws Exception {

        String[] options = new String[2];
        options[0] = "-R";                                    // "range"
        options[1] = attsToRemove;
        Remove remove = new Remove();                         // new instance of filter
        remove.setOptions(options);
        remove.setInputFormat(arff);
        return Filter.useFilter(arff, remove);

    }

    public static Instances applyDiscretizeConductas (Instances arff, String pos_conductas) throws Exception{
        //Discretize conductas IPA
        Discretize discretizeFilter = new Discretize();
        discretizeFilter.setAttributeIndices(pos_conductas);
        discretizeFilter.setUseBinNumbers(true);
        discretizeFilter.setBins(6);
        discretizeFilter.setInputFormat(arff);
        return Filter.useFilter(arff,discretizeFilter);
    }

    public static Instances applyDiscretizeReacciones (Instances arff, String pos_reacciones) throws Exception{
        //Discretize conductas IPA
        Discretize discretizeFilter = new Discretize();
        discretizeFilter.setAttributeIndices(pos_reacciones);
        discretizeFilter.setUseBinNumbers(true);
        discretizeFilter.setBins(4);
        discretizeFilter.setInputFormat(arff);
        return Filter.useFilter(arff,discretizeFilter);
    }

    public static Instances applyResample(Instances arff) throws Exception{
        Resample resampleFilter = new Resample();
        resampleFilter.setInputFormat(arff);
        return Filter.useFilter(arff,resampleFilter);
    }

    public static List<Classifier> getClassiffiers() throws Exception {
        List <Classifier> clasificadores = new ArrayList<>();
        clasificadores.add(new J48());
        clasificadores.add(new SMO());
        clasificadores.add(new NaiveBayes());
        clasificadores.add(new BayesNet());
        clasificadores.add(new REPTree());
        clasificadores.add(new PART());
        clasificadores.add(new JRip());
        clasificadores.add(new KStar());
        clasificadores.add(new LMT());
        clasificadores.add(new IBk());
        clasificadores.add(new DecisionTable());
        clasificadores.add(new LogitBoost());

        MultiClassClassifier mcModel = new MultiClassClassifier();
        String optionsMC[] = {
                "-M","0",
                "-R","2.0",
                "-S","1",
                "-W","weka.classifiers.functions.SMO",
                "--",
                "-C","1",
                "-L","0.001",
                "-P","1.0e-12",
                "-M",
                "-N", "0",
                "-V","-1",
                "-W","1",
                "-K", "weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0"
        };
        mcModel.setOptions(optionsMC);
        clasificadores.add(mcModel);
        return clasificadores;
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
