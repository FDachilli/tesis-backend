package com.tesis.commons;


import org.json.JSONException;
import org.processDataset.DirectProcessing;
import org.processDataset.PhasesProcessingSingleClassifier;
import org.processDataset.ProcessDataset;
import org.weka.*;
import java.io.FileNotFoundException;
import java.text.ParseException;

public class IpaClasiffier {

    public String parseConductaDirecto (String file) throws ParseException, FileNotFoundException, JSONException {

        Weka weka = new WekaSMO(10, 1, 3);
        ProcessDataset process = new DirectProcessing(weka, true, true);
        //TODO path como properties o constant
        return process.classify(file, "", "C:\\Users\\franc\\Dropbox\\tesis-backend\\");

    }


    public String parseConductaFases (String file) throws ParseException, FileNotFoundException, JSONException{
        Weka weka = new WekaDecisionTable(10, 1, 3);
        PhasesProcessingSingleClassifier process = new PhasesProcessingSingleClassifier(weka, true, true);
        return process.classify(file, "");
    }


}
