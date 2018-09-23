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


    //Dado un arff, obtengo el promedio de cada uno de los integrantes
    //Y se lo guarda en un nuevo arff.
    public OrganizadorPrediccion(){};

    public void organizar_carpeta(String direccion, String resultFilePath) throws IOException {
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
                        organizar_arff(f.getPath());
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

            organizar_arff(direccion);
            guardarArff(direccion + "dataOrganizada" + File.separator);
            path_archivos_resumidos.add(direccion + "dataOrganizada" + File.separator+ nombre_arff);
            nombre_arff = resultFilePath;
            obtenerConjuntosArff(path_archivos_resumidos.get(0));
            guardarArff("");

        }
    }

    //Dado un arff obtiene los datos que se precisan y los guarda.
    public void obtenerConjuntosArff(String direccion) throws IOException {
        //Obtengo el .arff
        BufferedReader reader =
                new BufferedReader(new FileReader(direccion));
        ArffLoader.ArffReader arffReader = new ArffLoader.ArffReader(reader);

        instances = arffReader.getData();
    }

    private static long diferenciaDeHoras(Date date1, Date date2) {
        return (date1.getTime() - date2.getTime());
    }

    private static long diferenciaDeMinutos(Date date1, Date date2){
        return (date1.getTime() - date2.getTime());
    }

    private static Date getDate(String d) throws ParseException {
        //2015-11-07 20:20:23
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (d.startsWith("\'"))
            return dateFormat.parse(d.substring(1,d.length()-2));
        else
            return dateFormat.parse(d);
    }

    //A partir de un arff le agrego las columnas de roles de sus companeros y
    //el promedio de comentarios IPA, area y reaccion.
    public void organizar_arff(String direccion) throws IOException {

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


            promediar_comentarios(POS_INF_CRUDO_CONDUCTA_IPA, POS_SUP_CRUDO_CONDUCTA_IPA);
            promediar_comentarios(POS_INF_CRUDO_REACCION_IPA, POS_SUP_CRUDO_REACCION_IPA);
            promediar_comentarios(POS_INF_CRUDO_AREA_IPA, POS_SUP_CRUDO_AREA_IPA);
            promediar_comentarios(POS_INF_CRUDO_HORARIO, POS_SUP_CRUDO_HORARIO);
            promediar_comentarios(POS_MAPEO_SYMLOG_DOMINANTE, POS_MAPEO_SYMLOG_SOCIO_EMOCIONAL);

            //Agrego la de cantidad de mensajes.
            for (String s : cant_comentarios.keySet()) {
                integrantes.get(s).setValue(instances.numAttributes() - 1, cant_comentarios.get(s));
            }
            promediar_comentarios_grupo();
            instances.clear();
            instances.addAll(integrantes.values());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

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

    //Retorna el valor entero identificador del area.
    public Double getDoubleArea(String area){
        if (area.equals("socio-emocional"))
            return 0.0;
        else
            return 1.0;
    }

    //Retorna el valor entero identificador de la reaccion.
    public Double getDoubleReaccion(String reaccion){
        if (reaccion.equals("positiva"))
            return 0.0;
        else if (reaccion.equals("negativa"))
            return 1.0;
        else if (reaccion.equals("pregunta"))
            return 2.0;
        else return 3.0;
    }

    public void promediar_comentarios_grupo(){
        int tot = 0;
        int pos_cant_com = instances.numAttributes()-1;
        for (Instance instance : integrantes.values()){
            tot += instance.value(pos_cant_com);
        }

        for (Instance instance : integrantes.values()){
            instance.setValue(pos_cant_com,((double)((double)instance.value(pos_cant_com)/(double)tot)));
        }
    }

    public void promediar_comentarios(int lim_inf, int lim_sup){
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

    //Saco el promedio de comentarios por integrante.
    public void promedioComentariosIpa(){

        //Para evitar ConcurrentModificationException.
        HashMap<String,ArrayList<Double>> auxiliar = new HashMap<>();

        //Saco el promedio de los comentarios IPA.
        for (Instance integrante : integrantes.values()){

            //Almaceno cada promedio en la posicion del arreglo.
            //Por lo tanto en la integrante.setValue(47) = arr.get(11)
            ArrayList<Double> arr = new ArrayList<>();

            //Calculo los promedios
            for (int i=1; i<=12; i++){
                arr.add(i-1,
                        integrante.value(integrante.numAttributes()-i)/cant_comentarios.get(integrante.toString(3)));
            }

            //Guardo los promedios de cada uno
            auxiliar.put(integrante.toString(3),arr);
        }

        //Le asigno los respectivos valores a cada integrante
        for (String integrante : auxiliar.keySet()){
            Instance instance = integrantes.get(integrante);
            for (int i=1; i<=12; i++)
                instance.setValue(instance.numAttributes()-i,
                        auxiliar.get(integrante).get(i-1));
            integrantes.put(integrante,instance);
        }

    }


    public void agregarTipoRol(){
        for (Instance integrante: integrantes.values()){
            //Los roles van desde 9 a 16
            for (int i=8; i<=16; i++) {
                int pos_mapeo = mapeo_tipo_rol.get(i);
                //Sumo los valores de los roles.
                integrante.setValue(pos_mapeo, integrante.value(pos_mapeo)+integrante.value(i));
            }
        }
    }

    //Este metodo agrega los roles de los companeros presentes en el grupo
    public void agregarRolCompaneros(){

        //Variable auxiliar para evitar Cocurrent.
        ArrayList<Instance> aux = new ArrayList<>();

        for (Instance integrante : integrantes.values()){

            //Seteo en 0 el contador de los roles de companeros
            for (int i = tam_inicial; i<tam_inicial+9; i++)
                integrante.setValue(i,0);

            //Busco y agrego los roles de los companeros
            for (Instance inst_comp : integrantes.values()){
                if (!integrante.equals(inst_comp)){
                    for (int i = 9; i<=17; i++){
                        //Tam inicial == al tamano antes de agregar los nuevos attr.
                        //En 7 arranca el rol del integrante.
                        integrante.setValue(tam_inicial-9+i ,integrante.value(tam_inicial-9+i)+inst_comp.value(i));
                    }
                }
            }
            aux.add(integrante);
        }

        //Agrego el integrante con los promedios
        for (Instance i : aux){
            integrantes.put(i.toString(POS_CRUDO_NOMBRE),i);
        }
    }

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
