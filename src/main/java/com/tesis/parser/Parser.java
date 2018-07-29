package com.tesis.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.tesis.commons.Constants;
import com.tesis.commons.IpaClasiffier;
import com.tesis.hangouts.ConversationStateRoot;
import com.tesis.hangouts.ParticipantData;

import weka.core.Attribute;
import weka.core.Instance;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public abstract class Parser {

    protected HashMap<String, String> rol_principal = new HashMap<>();
    protected IpaClasiffier ipaClasiffier = new IpaClasiffier();

    /**
     * Crea el encabezado de los archivos ARFF
     * @return
     */
    protected String getARFFHeader() {
        String header;

        header = 	"@relation chat" + '\n' +
                '\n' +
                "@attribute Conducta {1,2,3,4,5,6,7,8,9,10,11,12}" + '\n' +
                "@attribute nombre string" + '\n' +
                "@attribute message string" + '\n' +
                '\n' +
                "@data" + '\n';
        return header;
    }


    /**
     * Guarda en un archivo el contenido pasado por parámetro
     * @param fileName Nombre del archivo donde se guardará la información
     * @param fileContent Contenido que se desea guardar
     */
    protected void saveToFile(String fileName, String fileContent) {

        File file = new File(fileName);
        if(!file.exists()) {
            try {
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(fileContent);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(fileContent);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static String getRolPorGrupoCompanero(List<String> rolesCompaneros, String grupo) {
        if (grupo.contains("TP1") || grupo.contains("LB1")) {
            //Ambos son del TP1
            return rolesCompaneros.get(0);
        } else {
            if (grupo.contains("TP2")|| grupo.contains("LT1")) {
                //Ambos son del TP2
                return rolesCompaneros.get(1);
            } else {
                if (grupo.contains("TP3")) {
                    return rolesCompaneros.get(2);
                } else {
                    if (grupo.contains("TP4")) {
                        return rolesCompaneros.get(3);
                    } else {
                        if (grupo.contains("TP5")) {
                            return rolesCompaneros.get(4);
                        }
                    }
                }
            }
        }
        return null;
    }

    /*Metodo que calcula la diferencia de las horas que han pasado entre dos fechas en java
     */
    public static String diferenciaHorasDias(Date startDate, Date endDate) throws ParseException {

        long secs = (endDate.getTime() - startDate.getTime()) / 1000;
        int hours = (int) (secs / 3600);
        secs = secs % 3600;
        int mins = (int) (secs / 60);
        secs = secs % 60;

        //Se devuelve solo la hora para poder procesarlo en weka
        return String.valueOf(hours);
        //+ ":" + mins + ":" + secs;
    }


    protected HashMap<String,List<String>> getRolesCompaneros(List<String> inputFiles) throws JSONException, FileNotFoundException {
        //indice 0 => tp1, indice 1 => tp2, indice 2 = tp3...
        HashMap<String, List<String>> roles_companeros = new HashMap<>();
        HashMap<String, String> roles_autodefinidos = getConjuntoRolesAutodefinidos("roles_autopercepcion_2016.json");
        for (String inputFile: inputFiles) {
            JSONObject obj = new JSONObject(new JSONTokener(new FileInputStream(inputFile)));
            JSONArray rows = obj.getJSONArray("rows");
            List<String> label_roles = new ArrayList<>();
            //Armo lista de roles
            JSONArray cell_label_roles = rows.getJSONObject(0).getJSONArray("cell");
            for (int j = 2; j <= 10; j++) {
                label_roles.add(cell_label_roles.getString(j));
            }

            for (int i = 1; i < rows.length(); i++) {
                //Empiezo en 1 para no tomar la fila del encabezado que no tiene datos para utilizar
                String rol = "";
                Integer maxRol = 0;
                JSONArray cell = rows.getJSONObject(i).getJSONArray("cell");
                System.out.println(cell);
                for (int j = 2; j <= 10; j++) {
                    if (!cell.getString(j).isEmpty()) {
                        if (cell.getInt(j)>maxRol){
                            maxRol = cell.getInt(j);
                            rol = label_roles.get(j-2).toLowerCase();
                        }else{
                            if (cell.getInt(j)==maxRol){
                                //Criterio de desempate, alguno que se haya definido como autopercepcion.
                                String autopercepcion = roles_autodefinidos.get(cell.getString(1));
                                if (autopercepcion.contains(label_roles.get(j-2))){
                                    maxRol = cell.getInt(j);
                                    rol = label_roles.get(j-2).toLowerCase();
                                }
                            }

                        }
                    }
                }
                if (roles_companeros.containsKey(cell.getString(1))){
                   List<String> aux = roles_companeros.get(cell.getString(1));
                   aux.add(rol);
                   roles_companeros.put(cell.getString(1), aux);
                }else {
                    List<String> roles = new ArrayList<>();
                    roles.add(rol);
                    roles_companeros.put(cell.getString(1), roles);
                }

            }
        }
        return roles_companeros;
    }

    protected HashMap<String, List<Double>> getRolesAutodefinidos(String inputFile) throws JSONException, FileNotFoundException {
        HashMap<String, List<Double>> roles = new HashMap<>();
        JSONObject obj = new JSONObject(new JSONTokener(new FileInputStream(inputFile)));
        JSONArray rows = obj.getJSONArray("rows");
        for (int i = 1; i < rows.length(); i++)
        {
            //Empiezo en 1 para no tomar la fila del encabezado que no tiene datos para utilizar
            JSONArray cell = rows.getJSONObject(i).getJSONArray("cell");
            ArrayList<Double> rol = parsearRolesAutodefinidos(cell);
            roles.put(cell.getString(1), rol);
            String [] auxRol = cell.getString(2).split(";");
            if (auxRol[0].contains("Investigador")) {
                rol_principal.put(cell.getString(1), Constants.ROL_INVESTIGADOR);
            }else{
                rol_principal.put(cell.getString(1), auxRol[0].toLowerCase());
            }

        }
        return roles;
    }

    protected HashMap<String, String> getConjuntoRolesAutodefinidos(String inputFile) throws JSONException, FileNotFoundException {
        HashMap<String, String> roles = new HashMap<>();
        JSONObject obj = new JSONObject(new JSONTokener(new FileInputStream(inputFile)));
        JSONArray rows = obj.getJSONArray("rows");
        for (int i = 1; i < rows.length(); i++)
        {
            //Empiezo en 1 para no tomar la fila del encabezado que no tiene datos para utilizar
            JSONArray cell = rows.getJSONObject(i).getJSONArray("cell");
            String rol = cell.getString(2);
            roles.put(cell.getString(1), rol);
        }
        return roles;
    }


    protected ArrayList<Double> parsearRolesAutodefinidos (JSONArray cell) throws JSONException {
        //Orden en la lista: Finalizador,Impulsor,Cerebro,Colaborador,Especialista,Implementador,Monitor,Investigador,Coordinador
        //Cell[0]=email, [1] id, [2] rol natural, [3] rol secundario
        ArrayList<Double> roles = new ArrayList<>();
        if (cell.getString(2).contains("Finalizador")) {
            roles.add(1.0);
        }else if (cell.getString(3).contains("Finalizador")) {
            roles.add(0.5);
        }else{
            roles.add(0.0);
        }
        if (cell.getString(2).contains("Impulsor")) {
            roles.add(1.0);
        }else if (cell.getString(3).contains("Impulsor")) {
            roles.add(0.5);
        }else{
            roles.add(0.0);
        }
        if (cell.getString(2).contains("Cerebro")) {
            roles.add(1.0);
        }else if (cell.getString(3).contains("Cerebro")) {
            roles.add(0.5);
        }else{
            roles.add(0.0);
        }
        if (cell.getString(2).contains("Colaborador")) {
            roles.add(1.0);
        }else if (cell.getString(3).contains("Colaborador")) {
            roles.add(0.5);
        }else{
            roles.add(0.0);
        }
        if (cell.getString(2).contains("Especialista")) {
            roles.add(1.0);
        }else if (cell.getString(3).contains("Especialista")) {
            roles.add(0.5);
        }else{
            roles.add(0.0);
        }
        if (cell.getString(2).contains("Implementador")) {
            roles.add(1.0);
        }else if (cell.getString(3).contains("Implementador")) {
            roles.add(0.5);
        }else{
            roles.add(0.0);
        }
        if (cell.getString(2).contains("Monitor")) {
            roles.add(1.0);
        }else if (cell.getString(3).contains("Monitor")) {
            roles.add(0.5);
        }else{
            roles.add(0.0);
        }
        if (cell.getString(2).contains("Investigador")) {
            roles.add(1.0);
        }else if (cell.getString(3).contains("Investigador")) {
            roles.add(0.5);
        }else{
            roles.add(0.0);
        }
        if (cell.getString(2).contains("Coordinador")) {
            roles.add(1.0);
        }else if (cell.getString(3).contains("Coordinador")) {
            roles.add(0.5);
        }else{
            roles.add(0.0);
        }
        return roles;
    }

    public abstract void parseJson(String fileName) throws ParseException, JSONException, FileNotFoundException;


    /**
     * Parséa el archivo JSON pasado por parámetro, genera un archivo ARFF para cada conversación y guarda cada archivo ARFF por separado
     * @return Devuelve un String con la lista de todos los archivos generados separados por coma
     */

    protected String getFileName (ConversationStateRoot conversationStateRoot, String concat){
        String fileName = "";
        if (conversationStateRoot.getConversationState().getConversation().getName() != null) {
            fileName = Constants.HANGOUTS_FOLDER + conversationStateRoot.getConversationState().getConversation().getName() + concat + Constants.ARFF_FILE;
        } else {
            fileName = "";
            //fileName = Constants.HANGOUTS_FOLDER + conversationStateRoot.getConversationId().getId() + Constants.ARFF_FILE;
        }
        return fileName;
    }


    protected Hashtable<String, String> getNamesParticipantes (List<ConversationStateRoot> conversationStateRoots) {
        Hashtable<String, String> names = new Hashtable<String, String>();
        for (ConversationStateRoot conversationStateRoot : conversationStateRoots) {
            for (ParticipantData participants : conversationStateRoot.getConversationState().getConversation().getParticipantDataList()) {
                String name = participants.getFallbackName() == null ? participants.getParticipantId().getGaiaId() : participants.getFallbackName();
                if (!names.containsKey(participants.getParticipantId().getGaiaId()))
                    names.put(participants.getParticipantId().getGaiaId(), name);
            }

        }
        return names;
    }

    protected List<String> getConversacionesIgnoradas(List<ConversationStateRoot> conversationStateRoots, HashMap<String, List<Double>> roles_autodefinidos){
        List<String> listIdConversacionesIgnoradas = new ArrayList<>();
        for (ConversationStateRoot conversationStateRoot : conversationStateRoots) {
            for (ParticipantData participants : conversationStateRoot.getConversationState().getConversation().getParticipantDataList()) {
                if (!(participants.getParticipantId().getGaiaId().equals("100251222409268081279") || roles_autodefinidos.containsKey(participants.getParticipantId().getGaiaId()))) {
                    listIdConversacionesIgnoradas.add(conversationStateRoot.getConversationId().getId());
                    break;
                }
            }
        }
        return listIdConversacionesIgnoradas;
    }


    protected static ArrayList<Attribute> getFreelingAttributes() {

        // Atributos de freeling
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();

        attributes.add(new Attribute("adjectives"));
        attributes.add(new Attribute("adverbs"));
        attributes.add(new Attribute("determinants"));
        attributes.add(new Attribute("names"));
        attributes.add(new Attribute("verbs"));
        attributes.add(new Attribute("pronouns"));
        attributes.add(new Attribute("conjuctions"));
        attributes.add(new Attribute("interjections"));
        attributes.add(new Attribute("prepositions"));
        attributes.add(new Attribute("punctuation"));
        attributes.add(new Attribute("numerals"));
        attributes.add(new Attribute("dates_times"));

        return attributes;
    }


    protected static ArrayList<Attribute> getRolesAttributes() {

        // Atributos de freeling
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();

        attributes.add(new Attribute("finalizador"));
        attributes.add(new Attribute("impulsor"));
        attributes.add(new Attribute("cerebro"));
        attributes.add(new Attribute("colaborador"));
        attributes.add(new Attribute("especialista"));
        attributes.add(new Attribute("implementador"));
        attributes.add(new Attribute("monitor"));
        attributes.add(new Attribute("investigador"));
        attributes.add(new Attribute("coordinador"));

        return attributes;
    }


    protected double[] getFreelingValues(double[] values, int index, Instance instance, int instanceIndex) {

        // Atributo adjectives
        values[index++] = instance.value(instanceIndex++);
        // Atributo adverbs
        values[index++] = instance.value(instanceIndex++);
        // Atributo determinants
        values[index++] = instance.value(instanceIndex++);
        // Atributo names
        values[index++] = instance.value(instanceIndex++);
        // Atributo verbs
        values[index++] = instance.value(instanceIndex++);
        // Atributo pronouns
        values[index++] = instance.value(instanceIndex++);
        // Atributo conjuctions
        values[index++] = instance.value(instanceIndex++);
        // Atributo interjections
        values[index++] = instance.value(instanceIndex++);
        // Atributo prepositions
        values[index++] = instance.value(instanceIndex++);
        // Atributo punctuation
        values[index++] = instance.value(instanceIndex++);
        // Atributo numerals
        values[index++] = instance.value(instanceIndex++);
        // Atributo dates_times
        values[index++] = instance.value(instanceIndex++);

        return values;
    }


    protected double[] getRolesAutodefinidosValues (double[] values, int valuesIndex, List<Double> roles_autodefinidos){
        if (roles_autodefinidos != null) {
            values[valuesIndex++] = roles_autodefinidos.get(0);
            values[valuesIndex++] = roles_autodefinidos.get(1);
            values[valuesIndex++] = roles_autodefinidos.get(2);
            values[valuesIndex++] = roles_autodefinidos.get(3);
            values[valuesIndex++] = roles_autodefinidos.get(4);
            values[valuesIndex++] = roles_autodefinidos.get(5);
            values[valuesIndex++] = roles_autodefinidos.get(6);
            values[valuesIndex++] = roles_autodefinidos.get(7);
            values[valuesIndex++] = roles_autodefinidos.get(8);
        }else{
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
        }

        return values;

    }

    /*protected double[] getRolesCompanerosValues (double[] values, int valuesIndex, List<Double> roles_companeros){
        if (roles_companeros!= null) {
            values[valuesIndex++] = roles_companeros.get(0);
            values[valuesIndex++] = roles_companeros.get(1);
            values[valuesIndex++] = roles_companeros.get(2);
            values[valuesIndex++] = roles_companeros.get(3);
            values[valuesIndex++] = roles_companeros.get(4);
            values[valuesIndex++] = roles_companeros.get(5);
            values[valuesIndex++] = roles_companeros.get(6);
            values[valuesIndex++] = roles_companeros.get(7);
            values[valuesIndex++] = roles_companeros.get(8);
        }else{
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
            values[valuesIndex++] = 0;
        }

        return values;
    }*/
}
