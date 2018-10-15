package com.tesis.predictor;

import weka.core.Instances;

public class FasesCascadaResultados {

    private Instances classifier1Results;
    private Instances classifier2Results;
    private Instances classifier3Results;

    /**
     * Constructor
     * @param classifier1Results Instances Resultado de la clasificacion del clasificador 1
     * @param classifier2Results Instances Resultado de la clasificacion del clasificador 2
     * @param classifier3Results Instances Resultado de la clasificacion del clasificador 3
     */
    public FasesCascadaResultados(Instances classifier1Results, Instances classifier2Results, Instances classifier3Results) {

        this.classifier1Results = classifier1Results;
        this.classifier2Results = classifier2Results;
        this.classifier3Results = classifier3Results;
 
    }

    /**
     * Devuelve los resultados de la clasificaciÃ³n del clasificador 1
     * @return Instances Resultados de la clasificaciÃ³n del clasificador 1
     */
    public Instances getClassifier1Results() {
        return classifier1Results;
    }

    /**
     * Devuelve los resultados de la clasificaciÃ³n del clasificador 2
     * @return Instances Resultados de la clasificaciÃ³n del clasificador 2
     */
    public Instances getClassifier2Results() {
        return classifier2Results;
    }

    /**
     * Devuelve los resultados de la clasificaciÃ³n del clasificador 3
     * @return Instances Resultados de la clasificaciÃ³n del clasificador 3
     */
    public Instances getClassifier3Results() {
        return classifier3Results;
    }

}
