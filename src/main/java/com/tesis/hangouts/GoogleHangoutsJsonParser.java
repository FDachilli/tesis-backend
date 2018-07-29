package com.tesis.hangouts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesis.commons.Constants;
import com.tesis.weka.WekaRoles;

import org.apache.poi.ss.usermodel.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.preprocessDataset.FreelingAnalyzer;
import org.weka.Weka;
import weka.core.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;



/**
 * Convierte un JSON de Google hangouts a archivos ARFF
 *
 */
public class GoogleHangoutsJsonParser {

    private WekaRoles wekaRoles = new WekaRoles();
    HashMap<String, String> rol_principal = new HashMap<>();

    /**
     * Crea el encabezado de los archivos ARFF
     * @return
     */
    private String getARFFHeader() {
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
    private void saveToFile(String fileName, String fileContent) {

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

    /*Metodo que calcula la diferencia de las horas que han pasado entre dos fechas en java
     */
    public static String diferenciaHorasDias(Date startDate,Date endDate) throws ParseException {

        long secs = (endDate.getTime() - startDate.getTime()) / 1000;
        int hours = (int) (secs / 3600);
        secs = secs % 3600;
        int mins = (int) (secs / 60);
        secs = secs % 60;

        //Se devuelve solo la hora para poder procesarlo en weka
        return String.valueOf(hours);
        //+ ":" + mins + ":" + secs;
    }

    public HashMap<String,List<Double>> getRolesCompaneros(String inputFile) throws JSONException, FileNotFoundException {
        HashMap<String, List<Double>> roles = new HashMap<>();
        JSONObject obj = new JSONObject(new JSONTokener(new FileInputStream(inputFile)));
        JSONArray rows = obj.getJSONArray("rows");
        for (int i = 1; i < rows.length(); i++)
        {
            //Empiezo en 1 para no tomar la fila del encabezado que no tiene datos para utilizar
            JSONArray cell = rows.getJSONObject(i).getJSONArray("cell");
            ArrayList<Double> rol = new ArrayList<>();
            for (int j = 2; j <= 10; j++ ) {
                if (cell.getString(j).isEmpty()){
                    rol.add(0.0);
                }else {
                    rol.add(cell.getDouble(j));
                }
            }
            roles.put(cell.getString(1), rol);
        }
        return roles;
    }

    public HashMap<String, List<Double>> getRolesAutodefinidos(String inputFile) throws JSONException, FileNotFoundException {
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

    public ArrayList<Double> parsearRolesAutodefinidos (JSONArray cell) throws JSONException {
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



    /**
     * Parséa el archivo JSON pasado por parámetro, genera un archivo ARFF para cada conversación y guarda cada archivo ARFF por separado
     * @param fileName Nombre del archivo a parsear
     * @return Devuelve un String con la lista de todos los archivos generados separados por coma
     */
  /*  public void parseJson(String fileName) throws ParseException, JSONException, FileNotFoundException {

        ObjectMapper mapper = new ObjectMapper();
        FreelingAnalyzer freelingAnalyzer = FreelingAnalyzer.getInstance();

        HashMap<String, List<Double>> roles_autodefinidos = getRolesAutodefinidos("roles.json");
        HashMap<String, List<Double>> roles_compañeros = getRolesCompañeros("roles-companeros.json");

        HangoutsJSON hangoutsJSON;
        try {
            hangoutsJSON = mapper.readValue(new File(fileName), HangoutsJSON.class);

            List<ConversationStateRoot> conversationStateRoots = hangoutsJSON.getConversationStatesRoot();
            List<String> listIdConversacionesIgnoradas = new ArrayList<>();


            Hashtable<String, String> names = new Hashtable<String, String>();
            for (ConversationStateRoot conversationStateRoot : conversationStateRoots) {
                for (ParticipantData participants : conversationStateRoot.getConversationState().getConversation().getParticipantDataList()) {
                    String name = participants.getFallbackName() == null ? participants.getParticipantId().getGaiaId() : participants.getFallbackName();
                    if (participants.getParticipantId().getGaiaId().equals("100251222409268081279") || roles_autodefinidos.containsKey(participants.getParticipantId().getGaiaId())) {
                        //SI NO TENEMOS LOS ROLES DEL USUARIO IGNORAMOS LA CONVERSACION, EXCEPTO CUANDO ES P.E, QUE TIENE ESE ID
                        if (!names.containsKey(participants.getParticipantId().getGaiaId()))
                            names.put(participants.getParticipantId().getGaiaId(), name);
                    } else {
                        listIdConversacionesIgnoradas.add(conversationStateRoot.getConversationId().getId());
                        break;
                    }
                }
            }


            for (ConversationStateRoot conversationStateRoot : conversationStateRoots) {
                if (!listIdConversacionesIgnoradas.contains(conversationStateRoot.getConversationId().getId())){
                    //SOLO AGREGAMOS LAS CONVERSACIONES COMPLETAS
                    Boolean first = true;
                    Date dateFirst = null;
                    String fileContent;
                    fileContent = getARFFHeader();
                    List<Atributos> lista_atributos = new ArrayList<>();
                    Date lastDate = null;
                    for (Event event : conversationStateRoot.getConversationState().getEvents()) {
                        Atributos atributos = new Atributos();
                        ChatMessage chatMessage;
                        MessageContent messageContent;



                        //El timestamp estaba en segundos por lo tanto hay que
                        //Dividirlo por 1000 para que quede en ms y de el valor correct
                        Date date = new Date(event.getTimeStamp() / 1000);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        // (3) create a new String using the date format we want
                        String stringDate = formatter.format(date);

                        //Hay chats que contienen varios chats adentro, entonces para separalos pregunto si la fecha del ultimo mensaje es mayor que la del actual
                        if (lastDate == null || lastDate.compareTo(date) < 1){
                            lastDate = date;
                        }else{
                            saveRolArff( Constants.HANGOUTS_FOLDER + String.valueOf(System.currentTimeMillis()) + Constants.ARFF_FILE, fileContent, lista_atributos);
                            fileContent = getARFFHeader();
                            first = true;
                            dateFirst = null;
                            lista_atributos = new ArrayList<>();
                            lastDate = date;
                        }


                        if (first) {
                            first = false;
                            dateFirst = date;
                        }


                        //Seteo fecha
                        atributos.setFecha(stringDate);
                        //Seteo diferencia de horas
                        atributos.setDiferenciaHoras(diferenciaHorasDias(dateFirst, date));

                        if (roles_compañeros.containsKey(event.getSenderId().getGaiaId())){
                            atributos.setRolesCompaneros(roles_compañeros.get(event.getSenderId().getGaiaId()));
                        }

                        if (roles_autodefinidos.containsKey(event.getSenderId().getGaiaId())) {
                            List<Double> lista_roles_autodefinidos = roles_autodefinidos.get(event.getSenderId().getGaiaId());
                            atributos.setRolesAutodefinidos(lista_roles_autodefinidos);

                        }

                        if (rol_principal.containsKey(event.getSenderId().getGaiaId())){
                            atributos.setRol(rol_principal.get(event.getSenderId().getGaiaId()));
                        }else{
                            //A P.E le asignamos el rol coordinador
                            atributos.setRol(Constants.ROL_COORDINADOR);
                        }


                        if (event.getChatMessage() != null) {
                            chatMessage = event.getChatMessage();
                            if (chatMessage.getMessageContent() != null) {
                                messageContent = chatMessage.getMessageContent();
                                if (messageContent.getSegments() != null) {
                                    for (Segment segment : messageContent.getSegments()) {
                                        ArrayList<String> sentences = new ArrayList<>();
                                        if (segment.getText()!= null) {
                                            sentences = freelingAnalyzer.getSentences(segment.getText());
                                        }
                                        for (int i = 0; i < sentences.size(); i++) {
                                            //Para tener en cuenta el split que se hace al preprocesar los datos en el clasificador ipa
                                            lista_atributos.add(atributos);
                                        }
                                        fileContent += "?,'" + Util.addEscapeChar(names.get(event.getSenderId().getGaiaId())) + "','" + Util.addEscapeChar(segment.getText()) + "'"+ "\n";
                                    }
                                }
                            }
                        }
                    }

                    String newFileName = "";
                    if (conversationStateRoot.getConversationState().getConversation().getName() != null) {
                        newFileName = Constants.HANGOUTS_FOLDER + conversationStateRoot.getConversationState().getConversation().getName() + Constants.ARFF_FILE;
                    } else
                        newFileName = Constants.HANGOUTS_FOLDER + conversationStateRoot.getConversationId().getId() + Constants.ARFF_FILE;


                    saveRolArff (newFileName, fileContent, lista_atributos);
                }

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void saveRolArff (String fileName, String fileContent, List<Atributos> lista_atributos) throws FileNotFoundException, ParseException {
        saveToFile(fileName, fileContent);
        System.out.println("Clasificando: " + fileName);
        IpaClasiffier ipaClasiffier = new IpaClasiffier();
        String resultfile = ipaClasiffier.parseConductaDirecto(fileName);
        agregarAtributos (resultfile, lista_atributos);
    }


    private void agregarAtributos(String resultfile, List<Atributos> lista_atributos) {

        Instances dataset = Weka.loadDataset(resultfile);
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(wekaRoles.classRolAttribute());
        attributes.add(Weka.classConductaAttribute());
        // Atributo class_reaccion
        Attribute attClassReaccion = Weka.classReaccionAttribute();
        // Atributo class_area
        Attribute attClassArea = Weka.classAreaAttribute();
        attributes.add(attClassReaccion);
        attributes.add(attClassArea);
        Attribute attNombre = new Attribute(Weka.NOMBRE, (ArrayList<String>) null);
        attributes.add(attNombre);

        Attribute attMensaje = new Attribute(Weka.MENSAJE, (ArrayList<String>) null);
        Attribute attFecha = new Attribute("fecha","yyyy-MM-dd HH:mm:ss");

        attributes.add(attMensaje);

        //Atributos roles
        attributes.add(attFecha);
        attributes.add(new Attribute("diferenciadehoras"));
        attributes.add(new Attribute("finalizador"));
        attributes.add(new Attribute("impulsor"));
        attributes.add(new Attribute("cerebro"));
        attributes.add(new Attribute("colaborador"));
        attributes.add(new Attribute("especialista"));
        attributes.add(new Attribute("implementador"));
        attributes.add(new Attribute("monitor"));
        attributes.add(new Attribute("investigador"));
        attributes.add(new Attribute("coordinador"));
        /*TODO: Se le podria cambiar en vez de companeros por secundario?
                Ya habia un atr. con un nombre muy parecido.


        attributes.add(new Attribute("finalizador_companeros"));
        attributes.add(new Attribute("impulsor_companeros"));
        attributes.add(new Attribute("cerebro_companeros"));
        attributes.add(new Attribute("colaborador_companeros"));
        attributes.add(new Attribute("especialista_companeros"));
        attributes.add(new Attribute("implementador_companeros"));
        attributes.add(new Attribute("monitor_companeros"));
        attributes.add(new Attribute("investigador_companeros"));
        attributes.add(new Attribute("coordinador_companeros"));

        //Atributos freeling
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


        Instances sentencesDataset = new Instances("chat", attributes, 0);


        for (int i = 0; i < dataset.numInstances(); i++) {

            Instance instance = dataset.instance(i);
            int instanceIndex = 0;
            String conducta = instance.stringValue(instanceIndex++);
            // String classReaction = instance.stringValue(instanceIndex++);
            //String classArea = instance.stringValue(instanceIndex++);
            String classReaction = Constants.reacciones.get(Integer.parseInt(conducta));
            String classArea = Constants.areas.get(Integer.parseInt(conducta));
            String nombre = instance.stringValue(instanceIndex++);
            String mensaje = instance.stringValue(instanceIndex++);
            Double adjetivos = instance.value(instanceIndex++);
            Double adverbios = instance.value(instanceIndex++);
            Double determinantes = instance.value(instanceIndex++);
            Double nombres = instance.value(instanceIndex++);
            Double verbos = instance.value(instanceIndex++);
            Double pronombres = instance.value(instanceIndex++);
            Double conjunciones = instance.value(instanceIndex++);
            Double intersecciones = instance.value(instanceIndex++);
            Double preposiciones = instance.value(instanceIndex++);
            Double puntuaciones = instance.value(instanceIndex++);
            Double numerales = instance.value(instanceIndex++);
            Double num_fechas = instance.value(instanceIndex++);

            int valuesIndex = 0;
            double[] values = new double[attributes.size()];
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(lista_atributos.get(i).getRol()!= null ? lista_atributos.get(i).getRol() : "colaborador");
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(conducta);
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(classReaction);
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(classArea);
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).addStringValue(nombre);
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).addStringValue(mensaje);


            try {
                values[valuesIndex++] = sentencesDataset.attribute("fecha").parseDate(lista_atributos.get(i).getFecha());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            values[valuesIndex++] = Double.parseDouble(lista_atributos.get(i).getDiferenciaHoras());


            if (lista_atributos.get(i).getRolesAutodefinidos() != null) {
                List<Double> roles_autodefinidos = lista_atributos.get(i).getRolesAutodefinidos();
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

            if (lista_atributos.get(i).getRolesCompaneros()!= null) {
                List<Double> roles_companeros = lista_atributos.get(i).getRolesCompaneros();
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


            // Atributo adjectives
            values[valuesIndex++] = adjetivos;
            // Atributo adverbs
            values[valuesIndex++] = adverbios;
            // Atributo determinants
            values[valuesIndex++] = determinantes;
            // Atributo names
            values[valuesIndex++] = nombres;
            // Atributo verbs
            values[valuesIndex++] = verbos;
            // Atributo pronouns
            values[valuesIndex++] = pronombres;
            // Atributo conjuctions
            values[valuesIndex++] = conjunciones;
            // Atributo interjections
            values[valuesIndex++] = intersecciones;
            // Atributo prepositions
            values[valuesIndex++] = preposiciones;
            // Atributo punctuation
            values[valuesIndex++] = puntuaciones;
            // Atributo numerals
            values[valuesIndex++] = numerales;
            // Atributo dates_times
            values[valuesIndex] = num_fechas;


            Instance newInstance = new DenseInstance(1.0, values);

            sentencesDataset.add(newInstance);

        }
        Weka.saveDataset(sentencesDataset, resultfile.substring(0, resultfile.lastIndexOf(Constants.ARFF_FILE)) + "-roles" + Constants.ARFF_FILE);

    }

*/

}