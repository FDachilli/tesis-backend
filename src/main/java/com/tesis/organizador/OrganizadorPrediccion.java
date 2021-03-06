package com.tesis.organizador;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


import com.tesis.commons.Constants;

public class OrganizadorPrediccion {

    HashMap<String,Instance> integrantes = new HashMap<>();
    HashMap<String,Integer> cant_comentarios = new HashMap<>();
    int tam_inicial;
    Instances instances;
    String nombre_arff;
    Instances ultimate;
    HashMap<Integer,Integer> mapeo_tipo_rol = new HashMap<>();
    HashMap<Integer,Integer> mapeo_symlog = new HashMap<>();
    HashMap<String,Integer[]> porcentaje_horario = new HashMap<>();

    public int CANT_REAC = 4;
    public int CANT_COND_IPA = 12;
    public int POS_CRUDO_CONDUCTA = 0;
    public int POS_CRUDO_REACCION = 1;
    public int POS_CRUDO_AREA = 2;
    public int POS_CRUDO_NOMBRE = 3;
    public int POS_CRUDO_FECHA = 4;
    public int POS_INF_CRUDO_CONDUCTA_IPA = 5;
    public int POS_SUP_CRUDO_CONDUCTA_IPA = 16;
    public int POS_INF_CRUDO_REACCION_IPA = 17;
    public int POS_SUP_CRUDO_REACCION_IPA = 20;
    public int POS_INF_CRUDO_AREA_IPA = 21;
    public int POS_SUP_CRUDO_AREA_IPA = 22;
    public int POS_INF_CRUDO_HORARIO = 23;
    public int POS_SUP_CRUDO_HORARIO = 25;
    public int POS_MAPEO_SYMLOG_DOMINANTE = 26;
    public int POS_MAPEO_SYMLOG_SUMISO = 27;
    public int POS_MAPEO_SYMLOG_AMISTOSO = 28;
    public int POS_MAPEO_SYMLOG_NO_AMISTOSO = 29;
    public int POS_MAPEO_SYMLOG_TAREA = 30;
    public int POS_MAPEO_SYMLOG_SOCIO_EMOCIONAL = 31;


    public OrganizadorPrediccion(){};

    /**
     * Organiza la carpeta pasada por parametro obteniendo un resumen de la misma
     * @param direccion Path donde se encuentran los archivos a organizar
     * @param resultFilePath Path donde se almacena el resumen
     */
    public void organizarCarpeta(String direccion, String resultFilePath) throws IOException {
        File fileAOrganizar = new File(direccion);
        ArrayList<String> path_archivos_resumidos = new ArrayList<>();
        if (fileAOrganizar.isDirectory()) {
            ArrayList<File> directorios = new ArrayList<>(Arrays.asList(fileAOrganizar.listFiles()));
            ArrayList<File> archivos_originales = new ArrayList<>();

            for (File f : directorios)
                if (!f.isDirectory() && f.getName().endsWith("-roles.arff")) {
                    archivos_originales.add(f);
                }


            //Se hace de cada grupo un solo arff resumido con los promedios de cada integrante.
            //Solo me interesa aquellos que tengan los roles
            for (File f : archivos_originales) {
                if (f.isFile())
                    try {
                        organizarArff(f.getPath());
                        guardarArff(direccion + "dataOrganizada" + File.separator);
                        path_archivos_resumidos.add(direccion + "dataOrganizada" + File.separator + nombre_arff);

                        //Limpio variables
                        instances.clear();
                        integrantes.clear();
                        cant_comentarios.clear();
                        tam_inicial = 0;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            nombre_arff = resultFilePath;
            //Creo el arff con el primer archivo.
            obtenerConjuntosArff(path_archivos_resumidos.get(0));
            guardarArff("");

            //Hago el append del resto
            for (int i = 1; i < path_archivos_resumidos.size(); i++)
                try {
                    obtenerConjuntosArff(path_archivos_resumidos.get(i));
                    appendArff("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }else{

            organizarArff(direccion);
            guardarArff(direccion + "dataOrganizada" + File.separator);
            path_archivos_resumidos.add(direccion + "dataOrganizada" + File.separator+ nombre_arff);
            nombre_arff = resultFilePath;
            obtenerConjuntosArff(path_archivos_resumidos.get(0));
            guardarArff("");

        }
    }

    /**
     * Dado un arff obtiene los datos que se precisan y los guarda.
     * @param direccion Path donde se encuentran el archivo
     */
    public void obtenerConjuntosArff(String direccion) throws IOException {
        //Obtengo el .arff
        BufferedReader reader =
                new BufferedReader(new FileReader(direccion));
        ArffLoader.ArffReader arffReader = new ArffLoader.ArffReader(reader);

        instances = arffReader.getData();
    }

    /**
     * Obtiene la diferencia de horas entre dos fechas
     * @param date1 fecha inicio
     * @param date2 fecha fin
     * @return long con la diferencia de horas
     */
    private static long diferenciaDeHoras(Date date1, Date date2) {
        return (date1.getTime() - date2.getTime());
    }

    /**
     * Obtiene la diferencia de minutos entre dos fechas
     * @param date1 fecha inicio
     * @param date2 fecha fin
     * @return long con la diferencia de horas
     */
    private static long diferenciaDeMinutos(Date date1, Date date2){
        return (date1.getTime() - date2.getTime());
    }

    /**
     * Parsea un string a date
     * @param d string que contiene la fecha a parsear
     * @return Date con la hora
     */
    private static Date getDate(String d) throws ParseException {
        //2015-11-07 20:20:23
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (d.startsWith("\'"))
            return dateFormat.parse(d.substring(1,d.length()-2));
        else
            return dateFormat.parse(d);
    }

    /**
     * A partir de un arff se agregan los valores de los promedios de interes
     * @param direccion string que contiene el archivo a organizar
     */
    public void organizarArff(String direccion) throws IOException {

        String nombre;
        Double conducta;
        Double reaccion;
        Double area;
        Double pos_cond = 0.0;
        Double pos_area = 0.0;
        Double pos_reaccion = 0.0;
        Double pos_hora = 0.0;
        Date fecha_inicial;
        Date fecha_final;
        Date fecha;
        long rangoHoras;
        long rangoMinutos = 0;
        long diffHoras;
        long diffMinutos;
        //Obtengo el nombre del .arff
        int idx = direccion.replaceAll("\\\\", "/").lastIndexOf("/");
        nombre_arff = (idx >= 0 ? direccion.substring(idx + 1) : direccion);


        //Obtengo el .arff
        BufferedReader reader =
                new BufferedReader(new FileReader(direccion));
        ArffLoader.ArffReader arffReader = new ArffLoader.ArffReader(reader);

        //Inicializo variables
        instances = arffReader.getData();
        tam_inicial = instances.numAttributes();

        //Agrego columna de conductas
        //Atributos indice [46-57]
        for (int i=1; i<=12; i++){
            Attribute attribute = new Attribute("C"+i);
            instances.insertAttributeAt(attribute,instances.numAttributes());
        }

        //Agrego columna de reaccion
        //Atributos indice [58-61]
        for (int i = 1; i<=4; i++){
            Attribute attribute = new Attribute("R"+i);
            instances.insertAttributeAt(attribute,instances.numAttributes());
        }

        //Agrego columna de area
        //Atributos indice [62-63]
        for (int i = 1; i<=2; i++){
            Attribute attribute = new Attribute("A"+i);
            instances.insertAttributeAt(attribute,instances.numAttributes());
        }

        //Agrego columna de mayor hora de participacion
        //1-25, 25-75, 75-100
        //Inicio, Intermedio, Final
        for (int i = 1; i<=3; i++){
            Attribute attribute = new Attribute("Horario"+i);
            instances.insertAttributeAt(attribute, instances.numAttributes());
        }

        agregarAtributosSymlog();

        //Agrego columna de cant. de mensajes
        //Atributo indice [instances.numAttributes()-1]. Siempre ultima columna.
        Attribute attrib = new Attribute("cant_mensajes");
        instances.insertAttributeAt(attrib,instances.numAttributes());
        try {
            //TODO: Normalmente pasa que uno hace un comentario 24hs despues de que termino
            //      Y esto lo toma. Por lo tanto, habria que hacer
            //      En la franja horaria donde estan los 98% de los mensajes
            //      Bueno esa es mi franja. Los de despues son outliers.

            fecha_inicial = getDate(instances.get(0).stringValue(POS_CRUDO_FECHA));
            fecha_final = getDate(instances.get(instances.size() - 1).stringValue(POS_CRUDO_FECHA));
            rangoHoras = diferenciaDeHoras(fecha_final, fecha_inicial);


            for (Instance instance : instances) {
                conducta = Double.parseDouble(instance.stringValue(POS_CRUDO_CONDUCTA));  //Conducta IPA[1..12]
                reaccion = getDoubleReaccion(instance.stringValue(POS_CRUDO_REACCION));   //Reaccion Pos,Neg,Preg,Resp [1..4]
                area = getDoubleArea(instance.stringValue(POS_CRUDO_AREA));       //Area Soc-Emocional,Tarea [1..2]
                nombre = instance.toString(POS_CRUDO_NOMBRE);  //Nombre
                fecha = getDate(instance.toString(POS_CRUDO_FECHA));

                //Deduzco la columna de la conducta.
                // 9 = cantRolesComp.
                if (conducta==0)
                    System.out.println("12");
                pos_cond = tam_inicial + conducta -1;
                // 12 = Conductas IPA
                pos_reaccion = tam_inicial + CANT_COND_IPA + reaccion;
                // Reacciones
                pos_area = tam_inicial + CANT_COND_IPA + CANT_REAC + area;
                //Son 3 + cant. mnsj.

                if (rangoHoras > 0) {
                    diffHoras = diferenciaDeHoras(fecha, fecha_inicial);
                    double div = ((double)diffHoras/(double) rangoHoras);

                    if (div < 0.25)
                        pos_hora = Double.valueOf(POS_INF_CRUDO_HORARIO);
                    else if (div > 0.75)
                        pos_hora = Double.valueOf(POS_INF_CRUDO_HORARIO+2);
                    else
                        pos_hora = Double.valueOf(POS_INF_CRUDO_HORARIO+1);

                }
                else{
                    diffMinutos = diferenciaDeMinutos(fecha, fecha_inicial);

                    double div = ((double)diffMinutos/(double) rangoMinutos);

                    if (div < 0.25)
                        pos_hora = Double.valueOf(POS_INF_CRUDO_HORARIO);
                    else if (div > 0.75)
                        pos_hora = Double.valueOf(POS_INF_CRUDO_HORARIO+2);
                    else
                        pos_hora = Double.valueOf(POS_INF_CRUDO_HORARIO+1);

                }

                if (integrantes.containsKey(nombre)) {
                    Instance aux = integrantes.get(nombre);
                    //Aumento la cantidad de comentarios de los distintos tipos
                    aux.setValue(pos_cond.intValue(), aux.value(pos_cond.intValue()) + 1);
                    aux.setValue(pos_reaccion.intValue(), aux.value(pos_reaccion.intValue()) + 1);
                    aux.setValue(pos_area.intValue(), aux.value(pos_area.intValue()) + 1);
                    aux.setValue(pos_hora.intValue(), aux.value(pos_hora.intValue()) + 1);
                    integrantes.put(nombre, aux);
                    cant_comentarios.put(nombre, cant_comentarios.get(nombre) + 1);
                } else {
                    //Seteo el valor de contador en 0
                    for (int i = tam_inicial; i <= instances.numAttributes() - 1; i++) {
                        instance.setValue(i, 0.0);
                    }
                    instance.setValue(pos_cond.intValue(), 1);
                    instance.setValue(pos_reaccion.intValue(), 1);
                    instance.setValue(pos_area.intValue(), 1);
                    instance.setValue(pos_hora.intValue(), 1);
                    integrantes.put(nombre, instance);
                    cant_comentarios.put(nombre, 1);
                }
            }

            agregarTipoConducta();


            promediarComentarios(POS_INF_CRUDO_CONDUCTA_IPA, POS_SUP_CRUDO_CONDUCTA_IPA);
            promediarComentarios(POS_INF_CRUDO_REACCION_IPA, POS_SUP_CRUDO_REACCION_IPA);
            promediarComentarios(POS_INF_CRUDO_AREA_IPA, POS_SUP_CRUDO_AREA_IPA);
            promediarComentarios(POS_INF_CRUDO_HORARIO, POS_SUP_CRUDO_HORARIO);
            promediarComentarios(POS_MAPEO_SYMLOG_DOMINANTE, POS_MAPEO_SYMLOG_SOCIO_EMOCIONAL);

            //Agrego la de cantidad de mensajes.
            for (String s : cant_comentarios.keySet()) {
                integrantes.get(s).setValue(instances.numAttributes() - 1, cant_comentarios.get(s));
            }
            promediarComentariosGrupo();
            instances.clear();
            instances.addAll(integrantes.values());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Agrega los atributos SYMLOG a las instancias
     */
    public void agregarAtributosSymlog(){
        Attribute attribute = null;
        attribute = new Attribute("dominante_symlog");
        instances.insertAttributeAt(attribute,instances.numAttributes());
        attribute = new Attribute("sumiso_symlog");
        instances.insertAttributeAt(attribute,instances.numAttributes());
        attribute = new Attribute("amistoso_symlog");
        instances.insertAttributeAt(attribute,instances.numAttributes());
        attribute = new Attribute("no_amistoso_symlog");
        instances.insertAttributeAt(attribute,instances.numAttributes());
        attribute = new Attribute("tarea_symlog");
        instances.insertAttributeAt(attribute,instances.numAttributes());
        attribute = new Attribute("socio_emocional_symlog");
        instances.insertAttributeAt(attribute,instances.numAttributes());
    }


    /**
     * Mapea los valores de las conductas IPA a los atributos SYMLOG
     */
    public void agregarTipoConducta(){
        //Mapeo de conducta SYM .
        //Symlog - IPA
        //# Dominante # Negativo + # Pregunta ()
        //# Sumiso # Positivo + # Responde ()
        //# Amistoso # Positivo
        //# No Amistoso # Negativo
        // # Tarea # Pregunta + # Responde
        // Socioemocional #Positivo + # Negativo
        //De reaccion se saca Pos,Neg,Preg,Resp [0..3]
        for (Instance integrante: integrantes.values()){
            for (int i=POS_INF_CRUDO_REACCION_IPA; i<=POS_SUP_CRUDO_REACCION_IPA; i++) {
                if ( i== POS_INF_CRUDO_REACCION_IPA) {
                    //Positiva
                    integrante.setValue(POS_MAPEO_SYMLOG_AMISTOSO, integrante.value(POS_MAPEO_SYMLOG_AMISTOSO) + integrante.value(i));
                    integrante.setValue(POS_MAPEO_SYMLOG_SUMISO, integrante.value(POS_MAPEO_SYMLOG_SUMISO) + integrante.value(i));
                    integrante.setValue(POS_MAPEO_SYMLOG_SOCIO_EMOCIONAL, integrante.value(POS_MAPEO_SYMLOG_SOCIO_EMOCIONAL) + integrante.value(i));

                }
                if ( i== POS_INF_CRUDO_REACCION_IPA + 1 ){
                    //Negativa
                    integrante.setValue(POS_MAPEO_SYMLOG_DOMINANTE, integrante.value(POS_MAPEO_SYMLOG_DOMINANTE) + integrante.value(i));
                    integrante.setValue(POS_MAPEO_SYMLOG_NO_AMISTOSO, integrante.value(POS_MAPEO_SYMLOG_NO_AMISTOSO) + integrante.value(i));
                    integrante.setValue(POS_MAPEO_SYMLOG_SOCIO_EMOCIONAL, integrante.value(POS_MAPEO_SYMLOG_SOCIO_EMOCIONAL) + integrante.value(i));
                }
                if ( i== POS_INF_CRUDO_REACCION_IPA + 2 ){
                    //Pregunta
                    integrante.setValue(POS_MAPEO_SYMLOG_DOMINANTE, integrante.value(POS_MAPEO_SYMLOG_DOMINANTE) + integrante.value(i));
                    integrante.setValue(POS_MAPEO_SYMLOG_TAREA, integrante.value(POS_MAPEO_SYMLOG_TAREA) + integrante.value(i));
                }

                if ( i== POS_INF_CRUDO_REACCION_IPA + 3 ){
                    //Respuesta
                    integrante.setValue(POS_MAPEO_SYMLOG_SUMISO, integrante.value(POS_MAPEO_SYMLOG_SUMISO) + integrante.value(i));
                    integrante.setValue(POS_MAPEO_SYMLOG_TAREA, integrante.value(POS_MAPEO_SYMLOG_TAREA) + integrante.value(i));
                }
            }
        }


    }

    /**
     * Retorna el valor entero identificador del area.
     */
    public Double getDoubleArea(String area){
        if (area.equals("socio-emocional"))
            return 0.0;
        else
            return 1.0;
    }

    /**
     * Retorna el valor entero identificador de la reaccion.
     * @param reaccion string que contiene la accion a retornar
     * @return Double con valor de la accion
     */
    public Double getDoubleReaccion(String reaccion){
        if (reaccion.equals("positiva"))
            return 0.0;
        else if (reaccion.equals("negativa"))
            return 1.0;
        else if (reaccion.equals("pregunta"))
            return 2.0;
        else return 3.0;
    }

    /**
     * Promedia los mensajes de los integrantes de cada grupo
     */
    public void promediarComentariosGrupo(){
        int tot = 0;
        int pos_cant_com = instances.numAttributes()-1;
        for (Instance instance : integrantes.values()){
            tot += instance.value(pos_cant_com);
        }

        for (Instance instance : integrantes.values()){
            instance.setValue(pos_cant_com,((double)((double)instance.value(pos_cant_com)/(double)tot)));
        }
    }

    /**
     * Promedia los atributos que se encuantran entre los limites pasados por parametro
     * @param lim_inf limite inferior del atributo
     * @param lim_sup limite superior del atributo
     */
    public void promediarComentarios(int lim_inf, int lim_sup){
        //Para evitar ConcurrentModificationException.
        HashMap<String,ArrayList<Double>> auxiliar = new HashMap<>();

        //Saco el promedio de los comentarios IPA.
        for (Instance integrante : integrantes.values()){

            //Almaceno cada promedio en la posicion del arreglo.
            //Por lo tanto en la integrante.setValue(47) = arr.get(11)
            ArrayList<Double> arr = new ArrayList<>();

            //Calculo los promedios
            for (int i=lim_inf; i<=lim_sup; i++)
                arr.add(i-lim_inf,
                        integrante.value(i)/cant_comentarios.get(integrante.toString(POS_CRUDO_NOMBRE)));


            //Guardo los promedios de cada uno
            auxiliar.put(integrante.toString(POS_CRUDO_NOMBRE),arr);
        }

        //Le asigno los respectivos valores a cada integrante
        for (String integrante : auxiliar.keySet()){
            Instance instance = integrantes.get(integrante);
            for (int i=lim_inf; i<=lim_sup; i++)
                instance.setValue(i, auxiliar.get(integrante).get(i-lim_inf));
            integrantes.put(integrante,instance);
        }
    }

    /**
     * Aumenta las posiciones de los atributos segun el enfoque para el cual se resume la carpeta
     */
    public void aumentarIndices(){
        POS_CRUDO_CONDUCTA += 1;
        POS_CRUDO_REACCION += 1;
        POS_CRUDO_AREA += 1;
        POS_CRUDO_NOMBRE += 1;
        POS_CRUDO_FECHA += 1;
        POS_INF_CRUDO_CONDUCTA_IPA += 1;
        POS_SUP_CRUDO_CONDUCTA_IPA += 1;
        POS_INF_CRUDO_REACCION_IPA += 1;
        POS_SUP_CRUDO_REACCION_IPA += 1;
        POS_INF_CRUDO_AREA_IPA += 1;
        POS_SUP_CRUDO_AREA_IPA += 1;
        POS_INF_CRUDO_HORARIO += 1;
        POS_SUP_CRUDO_HORARIO += 1;
        POS_MAPEO_SYMLOG_DOMINANTE += 1;
        POS_MAPEO_SYMLOG_SUMISO += 1;
        POS_MAPEO_SYMLOG_AMISTOSO += 1;
        POS_MAPEO_SYMLOG_NO_AMISTOSO += 1;
        POS_MAPEO_SYMLOG_TAREA += 1;
        POS_MAPEO_SYMLOG_SOCIO_EMOCIONAL += 1;
    }

    /**
     * Aumenta las posiciones de los atributos calculados segun el enfoque para el cual se resume la carpeta
     */
    public void aumentarIndicesCalculados(){
        POS_INF_CRUDO_CONDUCTA_IPA += 1;
        POS_SUP_CRUDO_CONDUCTA_IPA += 1;
        POS_INF_CRUDO_REACCION_IPA += 1;
        POS_SUP_CRUDO_REACCION_IPA += 1;
        POS_INF_CRUDO_AREA_IPA += 1;
        POS_SUP_CRUDO_AREA_IPA += 1;
        POS_INF_CRUDO_HORARIO += 1;
        POS_SUP_CRUDO_HORARIO += 1;
        POS_MAPEO_SYMLOG_DOMINANTE += 1;
        POS_MAPEO_SYMLOG_SUMISO += 1;
        POS_MAPEO_SYMLOG_AMISTOSO += 1;
        POS_MAPEO_SYMLOG_NO_AMISTOSO += 1;
        POS_MAPEO_SYMLOG_TAREA += 1;
        POS_MAPEO_SYMLOG_SOCIO_EMOCIONAL += 1;
    }

    /**
     * Decrementa las posiciones de los atributos calculados segun el enfoque para el cual se resume la carpeta
     */
    public void decrementarIndicesCalculados(){
        POS_INF_CRUDO_CONDUCTA_IPA -= 1;
        POS_SUP_CRUDO_CONDUCTA_IPA -= 1;
        POS_INF_CRUDO_REACCION_IPA -= 1;
        POS_SUP_CRUDO_REACCION_IPA -= 1;
        POS_INF_CRUDO_AREA_IPA -= 1;
        POS_SUP_CRUDO_AREA_IPA -= 1;
        POS_INF_CRUDO_HORARIO -= 1;
        POS_SUP_CRUDO_HORARIO -= 1;
        POS_MAPEO_SYMLOG_DOMINANTE -= 1;
        POS_MAPEO_SYMLOG_SUMISO -= 1;
        POS_MAPEO_SYMLOG_AMISTOSO -= 1;
        POS_MAPEO_SYMLOG_NO_AMISTOSO -= 1;
        POS_MAPEO_SYMLOG_TAREA -= 1;
        POS_MAPEO_SYMLOG_SOCIO_EMOCIONAL -= 1;
    }


    /**
     * Decrementa las posiciones de los atributos segun el enfoque para el cual se resume la carpeta
     */
    public void decrementarIndices(){
        POS_CRUDO_CONDUCTA -= 1;
        POS_CRUDO_REACCION -= 1;
        POS_CRUDO_AREA -= 1;
        POS_CRUDO_NOMBRE -= 1;
        POS_CRUDO_FECHA -= 1;
        POS_INF_CRUDO_CONDUCTA_IPA -= 1;
        POS_SUP_CRUDO_CONDUCTA_IPA -= 1;
        POS_INF_CRUDO_REACCION_IPA -= 1;
        POS_SUP_CRUDO_REACCION_IPA -= 1;
        POS_INF_CRUDO_AREA_IPA -= 1;
        POS_SUP_CRUDO_AREA_IPA -= 1;
        POS_INF_CRUDO_HORARIO -= 1;
        POS_SUP_CRUDO_HORARIO -= 1;
        POS_MAPEO_SYMLOG_DOMINANTE -= 1;
        POS_MAPEO_SYMLOG_SUMISO -= 1;
        POS_MAPEO_SYMLOG_AMISTOSO -= 1;
        POS_MAPEO_SYMLOG_NO_AMISTOSO -= 1;
        POS_MAPEO_SYMLOG_TAREA -= 1;
        POS_MAPEO_SYMLOG_SOCIO_EMOCIONAL -= 1;
    }

    /**
     * Guarda en la direccion pasada por parametro las instancias del resumen
     * @param direccion path del archivo a almancenar
     */
    public void guardarArff(String direccion) throws IOException {
    	File directory = new File(direccion);
	 	if (!directory.exists()){
	 		directory.mkdirs();
	    }
        BufferedWriter writer = new BufferedWriter(new FileWriter(direccion+nombre_arff));
        System.out.println(nombre_arff);
        writer.write(instances.toString());
        writer.flush();
        writer.close();
    }
    
    /**
     * Concatena en el archivo otras instancias
     * @param direccion path del archivo donde se concatena
     */
    public void appendArff(String direccion) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(direccion+nombre_arff, true));

        writer.write("\n");
        for (Instance i : instances) {
            writer.write(i.toString());
            writer.write("\n");
            writer.flush();
        }
        writer.close();
    }

}
