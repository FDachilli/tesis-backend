package com.tesis.commons;

/**
 * Created by Joaking on 1/9/2018.
 */
import java.io.File;
import java.util.Hashtable;

/**
 * Contiene las constantes utilizadas en todo el código
 * @author martinmineo
 *
 */
public class Constants {
    public static final String DATASETS_FOLDER = "datasets" + File.separator;
    public static final String HANGOUTS_FOLDER = DATASETS_FOLDER + "hangouts" + File.separator;
    public static final String MODELS_FOLDER =  "modelos" + File.separator;
    public static final String RESULTS_FOLDER =  "resultados" + File.separator;
    public static final String PREDICTIONS_FOLDER =  "predicciones" + File.separator;
    public static final String DIRECTO_LABELED_FOLDER = "results" + File.separator + "labeled" + File.separator + "Directo" + File.separator;
    public static final String MODELS_DIRECTO_FOLDER = "procesamientoDirecto" + File.separator + MODELS_FOLDER;
    public static final String MODELS_DIRECTO_FILTROS_FOLDER = "results" + File.separator + "resample_discretize" + File.separator + "procesamientoDirecto" + File.separator + MODELS_FOLDER;
    public static final String RESULTS_DIRECTO_FOLDER = "results" + File.separator + "procesamientoDirecto" + File.separator + RESULTS_FOLDER;
    public static final String RESULTS_DIRECTO_FILTROS_FOLDER = "results" + File.separator  + "resample_discretize" + File.separator + "procesamientoDirecto" + File.separator + RESULTS_FOLDER;
    public static final String PREDICTIONS_DIRECTO_FOLDER = "results" + File.separator + "procesamientoDirecto" + File.separator + PREDICTIONS_FOLDER;
    public static final String FASES_LABELED_FOLDER = "results" + File.separator + "labeled" + File.separator + "Fases" + File.separator;
    public static final String FASES_FOLDER = "results" + File.separator + "procesamientoFases" + File.separator;
    public static final String FASES_FILTROS_FOLDER = "results" + File.separator + "resample_discretize" + File.separator + "procesamientoFases" + File.separator;
    public static final String FASE_DOS_FOLDER = "Fase2" + File.separator;
    public static final String FASE_TRES_FOLDER = "Fase3" + File.separator;
    public static final String DIRECTO_GRUPOS_LABELED_FOLDER = "results" + File.separator + "labeled" + File.separator + "Grupos_Directo" + File.separator;
    public static final String FASES_GRUPOS_LABELED_FOLDER = "results" + File.separator + "labeled" + File.separator + "Grupos_Fases" + File.separator;

    public static final String ARFF_FILE = ".arff";
    public static final String JSON_FILE = ".json";
    public static final String XLSX_FILE = ".xlsx";
    public static final String TXT_FILE = ".txt";
    public static final String DAT_FILE = ".dat";


    public static final String AREA_SOCIO_EMOCIONAL = "socio-emocional";
    public static final String AREA_TAREA = "tarea";
    public static final String REACCION_POSITIVA = "positiva";
    public static final String REACCION_NEGATIVA = "negativa";
    public static final String REACCION_RESPUESTA = "respuesta";
    public static final String REACCION_PREGUNTA = "pregunta";

    public static final String ROL_INVESTIGADOR = "investigador";
    public static final String ROL_COORDINADOR = "coordinador";

    public static final Hashtable<Integer,String> areas = new Hashtable<Integer,String>() {{
        put(1,      AREA_SOCIO_EMOCIONAL);
        put(2,      AREA_SOCIO_EMOCIONAL);
        put(3,     AREA_SOCIO_EMOCIONAL);
        put(4, AREA_TAREA);
        put(5,    AREA_TAREA);
        put(6,    AREA_TAREA);
        put(7,    AREA_TAREA);
        put(8,    AREA_TAREA);
        put(9,    AREA_TAREA);
        put(10,    AREA_SOCIO_EMOCIONAL);
        put(11,    AREA_SOCIO_EMOCIONAL);
        put(12,    AREA_SOCIO_EMOCIONAL);
    }};

    public static final Hashtable<Integer,String> reacciones = new Hashtable<Integer,String>() {{
        put(1,      REACCION_POSITIVA);
        put(2,      REACCION_POSITIVA);
        put(3,     REACCION_POSITIVA);
        put(4, REACCION_RESPUESTA);
        put(5,    REACCION_RESPUESTA);
        put(6,    REACCION_RESPUESTA);
        put(7,    REACCION_PREGUNTA);
        put(8,    REACCION_PREGUNTA);
        put(9,    REACCION_PREGUNTA);
        put(10,    REACCION_NEGATIVA);
        put(11,    REACCION_NEGATIVA);
        put(12,    REACCION_NEGATIVA);
    }};

    public static final Hashtable<Integer,String> descripcion_roles = new Hashtable<Integer,String>() {{
        put(0,      "finalizador");
        put(1,      "impulsor");
        put(2,     "cerebro");
        put(3, "colaborador");
        put(4,    "especialista");
        put(5,    "implementador");
        put(6,    "monitor");
        put(7,    "investigador");
        put(8,    "coordinador");

    }};

    public static final Hashtable<String, String> tipos_rol = new Hashtable<String, String>(){{
        put("finalizador", "accion");
        put("impulsor", "accion");
        put("cerebro", "mental");
        put("colaborador", "social");
        put("especialista", "mental");
        put("implementador", "accion");
        put("monitor", "mental");
        put("investigador", "social");
        put("coordinador", "social");
    }};
}
