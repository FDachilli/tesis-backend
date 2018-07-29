package com.tesis.predictor;

import com.tesis.commons.Constants;
import org.apache.commons.io.FileUtils;
import com.tesis.organizador.Organizador;
import com.tesis.parser.FasesParser;

import java.io.File;

public class PredictorFases {

    public void predecirFases (String filePath, String modelFase2, String modelFase3) throws Exception {

        if (filePath.contains(Constants.JSON_FILE)) {
            //Limpio directorio para despues no procesar archivos viejos
            FileUtils.cleanDirectory(new File(Constants.FASES_LABELED_FOLDER + Constants.FASE_DOS_FOLDER));
            FileUtils.cleanDirectory(new File(Constants.FASES_LABELED_FOLDER + Constants.FASE_TRES_FOLDER));
            //Lee el json y divide las conversaciones dejando los resultados en "results\labeled\Directo"
            FasesParser parser = new FasesParser();
            parser.parseJson(filePath);
            Organizador organizador = new Organizador();
            organizador.orgainzar_carpeta(Constants.FASES_LABELED_FOLDER + Constants.FASE_DOS_FOLDER, "./ResumenFase2.arff");
            //organizador.orgainzar_carpeta(Constants.FASES_LABELED_FOLDER + Constants.FASE_DOS_FOLDER, "./ResumenFase3.arff");
        }

        PredictorFase2 predictorFase2 = new PredictorFase2();
        FaseResultados fase2Results = predictorFase2.predecirFase2("./ResumenFase2.arff", modelFase2);
        PredictorFase3 predictorFase3 = new PredictorFase3();
        FaseResultados fase3Results = predictorFase3.predecirFase3(fase2Results.getPath(), modelFase3);
        System.out.println (fase3Results.getLabeledInstances());
    }

}
