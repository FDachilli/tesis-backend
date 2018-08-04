package com.tesis.parser.entrenamiento;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesis.commons.Constants;
import com.tesis.commons.Util;
import com.tesis.hangouts.*;
import com.tesis.weka.WekaRoles;

import org.json.JSONException;
import org.preprocessDataset.FreelingAnalyzer;
import org.weka.Weka;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FasesParserEntrenamiento extends ParserEntrenamiento{

    public void parseJson(String fileName) throws ParseException, JSONException, FileNotFoundException {

        ObjectMapper mapper = new ObjectMapper();
        FreelingAnalyzer freelingAnalyzer = FreelingAnalyzer.getInstance("C:\\Users\\franc\\Dropbox\\tesis-backend\\");

        HashMap<String, List<Double>> rolesAutodefinidos = getRolesAutodefinidos("roles_autopercepcion_2016.json");
        List<String> files_feedbacks = new ArrayList<>();
        files_feedbacks.add("feedbacktp1_2016.json");
        files_feedbacks.add("feedbacktp2_2016.json");
        files_feedbacks.add("feedbacktp3_2016.json");
        files_feedbacks.add("feedbacktp4_2016.json");
        files_feedbacks.add("feedbacktp5_2016.json");
        HashMap<String,List<String>> rolesCompaneros = getRolesCompaneros(files_feedbacks);


        HangoutsJSON hangoutsJSON;
        try {
            hangoutsJSON = mapper.readValue(new File(fileName), HangoutsJSON.class);

            List<ConversationStateRoot> conversationStateRoots = hangoutsJSON.getConversationStatesRoot();



            Hashtable<String, String> names = getNamesParticipantes(conversationStateRoots);
            List<String> listIdConversacionesIgnoradas = getConversacionesIgnoradas(conversationStateRoots, rolesAutodefinidos);

            for (ConversationStateRoot conversationStateRoot : conversationStateRoots) {
                if (!listIdConversacionesIgnoradas.contains(conversationStateRoot.getConversationId().getId())&& conversationStateRoot.getConversationState().getConversation().getName()!=null
                        && conversationStateRoot.getConversationState().getConversation().getName().contains("PE16")){
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
                            String name = getFileName(conversationStateRoot, "-" + System.currentTimeMillis());
                            saveRolArff( name, fileContent, lista_atributos);
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

                       /* if (rolesCompañeros.containsKey(event.getSenderId().getGaiaId())){
                            atributos.setRolesCompaneros(rolesCompañeros.get(event.getSenderId().getGaiaId()));
                        }*/

                        if (rolesCompaneros.containsKey(event.getSenderId().getGaiaId())){
                            atributos.setRolCompaneros(getRolPorGrupoCompanero(rolesCompaneros.get(event.getSenderId().getGaiaId()), conversationStateRoot.getConversationState().getConversation().getName()));
                        }


                        if (rolesAutodefinidos.containsKey(event.getSenderId().getGaiaId())) {
                            List<Double> lista_roles_autodefinidos = rolesAutodefinidos.get(event.getSenderId().getGaiaId());
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

                    String newFileName = getFileName(conversationStateRoot, "");

                    if (!newFileName.equals("")){
                        saveRolArff (newFileName, fileContent, lista_atributos);
                    }

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
        String resultfile = ipaClasiffier.parseConductaDirecto(fileName);
        agregarAtributosFase2 (resultfile, lista_atributos);
        agregarAtributosFase3 (resultfile, lista_atributos);
    }


    private void agregarAtributosFase2(String resultfile, List<Atributos> lista_atributos) {

        Instances dataset = Weka.loadDataset(resultfile);
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(WekaRoles.classTipoRolAttribute());
        attributes.add(Weka.classConductaAttribute());
        // Atributo class_reaccion
        Attribute attClassReaccion = Weka.classReaccionAttribute();
        // Atributo class_area
        Attribute attClassArea = Weka.classAreaAttribute();
        attributes.add(attClassReaccion);
        attributes.add(attClassArea);
        Attribute attNombre = new Attribute(Weka.NOMBRE, (ArrayList<String>) null);
        attributes.add(attNombre);

        //Attribute attMensaje = new Attribute(Weka.MENSAJE, (ArrayList<String>) null);
        Attribute attFecha = new Attribute("fecha","yyyy-MM-dd HH:mm:ss");

       // attributes.add(attMensaje);

        //Atributos roles
        attributes.add(attFecha);
        attributes.add(WekaRoles.classTipoRolCompanerosAttribute());
      //  attributes.add(new Attribute("diferenciadehoras"));
       // attributes.addAll(getRolesAttributes());
        /*TODO: Se le podria cambiar en vez de companeros por secundario?
                Ya habia un atr. con un nombre muy parecido.
         */


       // attributes.addAll(WekaRoles.getRolesCompanerosAttributes());

        //Atributos freeling
        //attributes.addAll(getFreelingAttributes());

        Instances sentencesDataset = new Instances("chat", attributes, 0);


        for (int i = 0; i < dataset.numInstances(); i++) {

            Instance instance = dataset.instance(i);
            int instanceIndex = 0;
            String conducta = instance.stringValue(instanceIndex++);
            String classReaction = Constants.reacciones.get(Integer.parseInt(conducta));
            String classArea = Constants.areas.get(Integer.parseInt(conducta));
            String nombre = instance.stringValue(instanceIndex++);
        //    String mensaje = instance.stringValue(instanceIndex++);

            int valuesIndex = 0;
            double[] values = new double[attributes.size()];
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(lista_atributos.get(i).getRol()!= null ? Constants.tipos_rol.get(lista_atributos.get(i).getRol()) : "social");
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(conducta);
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(classReaction);
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(classArea);
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).addStringValue(nombre);
        //    values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).addStringValue(mensaje);


            try {
                values[valuesIndex++] = sentencesDataset.attribute("fecha").parseDate(lista_atributos.get(i).getFecha());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String tipo_rol_companeros = "";
            if (lista_atributos.get(i).getRolCompaneros()== null){
                tipo_rol_companeros = "social";
            }else {
                if (lista_atributos.get(i).getRolCompaneros().equals("")) {
                    tipo_rol_companeros = Constants.tipos_rol.get(lista_atributos.get(i).getRol());
                } else {
                    tipo_rol_companeros = Constants.tipos_rol.get(lista_atributos.get(i).getRolCompaneros());
                }
            }

            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(tipo_rol_companeros);
           // values[valuesIndex++] = Double.parseDouble(lista_atributos.get(i).getDiferenciaHoras());

         /*   values = getRolesAutodefinidosValues(values, valuesIndex, lista_atributos.get(i).getRolesAutodefinidos());
            valuesIndex = valuesIndex + 9;

            /*values = getRolesCompanerosValues(values, valuesIndex, lista_atributos.get(i).getRolesCompaneros());
            valuesIndex = valuesIndex + 9;*/

           // values = getFreelingValues(values, valuesIndex, instance, instanceIndex);

            Instance newInstance = new DenseInstance(1.0, values);

            sentencesDataset.add(newInstance);

        }
        Weka.saveDataset(sentencesDataset, Constants.FASES_LABELED_FOLDER + Constants.FASE_DOS_FOLDER + String.valueOf(System.currentTimeMillis()) + "-roles" + Constants.ARFF_FILE);

    }

    private void agregarAtributosFase3(String resultfile, List<Atributos> lista_atributos) {

        Instances dataset = Weka.loadDataset(resultfile);
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(WekaRoles.classRolAttribute());
        attributes.add(WekaRoles.classTipoRolAttribute());
        attributes.add(Weka.classConductaAttribute());
        // Atributo class_reaccion
        Attribute attClassReaccion = Weka.classReaccionAttribute();
        // Atributo class_area
        Attribute attClassArea = Weka.classAreaAttribute();
        attributes.add(attClassReaccion);
        attributes.add(attClassArea);
        Attribute attNombre = new Attribute(Weka.NOMBRE, (ArrayList<String>) null);
        attributes.add(attNombre);

       // Attribute attMensaje = new Attribute(Weka.MENSAJE, (ArrayList<String>) null);
        Attribute attFecha = new Attribute("fecha","yyyy-MM-dd HH:mm:ss");

        //attributes.add(attMensaje);

        //Atributos roles
        attributes.add(attFecha);
        attributes.add(WekaRoles.classTipoRolCompanerosAttribute());
        attributes.add(WekaRoles.classRolCompanerosAttribute());
      //  attributes.add(new Attribute("diferenciadehoras"));
       // attributes.addAll(getRolesAttributes());
        /*TODO: Se le podria cambiar en vez de companeros por secundario?
                Ya habia un atr. con un nombre muy parecido.
         */

       // attributes.addAll(WekaRoles.getRolesCompanerosAttributes());

        //Atributos freeling
       // attributes.addAll(getFreelingAttributes());

        Instances sentencesDataset = new Instances("chat", attributes, 0);


        for (int i = 0; i < dataset.numInstances(); i++) {

            Instance instance = dataset.instance(i);
            int instanceIndex = 0;
            String conducta = instance.stringValue(instanceIndex++);
            String classReaction = Constants.reacciones.get(Integer.parseInt(conducta));
            String classArea = Constants.areas.get(Integer.parseInt(conducta));
            String nombre = instance.stringValue(instanceIndex++);
           // String mensaje = instance.stringValue(instanceIndex++);

            int valuesIndex = 0;
            double[] values = new double[attributes.size()];
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(lista_atributos.get(i).getRol()!= null ? lista_atributos.get(i).getRol() : "coordinador");
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(lista_atributos.get(i).getRol()!= null ? Constants.tipos_rol.get(lista_atributos.get(i).getRol()) : "social");
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(conducta);
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(classReaction);
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(classArea);
            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).addStringValue(nombre);
         //   values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).addStringValue(mensaje);


            try {
                values[valuesIndex++] = sentencesDataset.attribute("fecha").parseDate(lista_atributos.get(i).getFecha());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String tipo_rol_companeros = "";
            if (lista_atributos.get(i).getRolCompaneros()== null){
                tipo_rol_companeros = "social";
            }else {
                if (lista_atributos.get(i).getRolCompaneros().equals("")) {
                    tipo_rol_companeros = Constants.tipos_rol.get(lista_atributos.get(i).getRol());
                } else {
                    tipo_rol_companeros = Constants.tipos_rol.get(lista_atributos.get(i).getRolCompaneros());
                }
            }

            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(tipo_rol_companeros);

            String rol_companeros;
            if (lista_atributos.get(i).getRolCompaneros()== null){
                rol_companeros = "coordinador";
            }else {
                if (lista_atributos.get(i).getRolCompaneros().equals("")) {
                    rol_companeros = lista_atributos.get(i).getRol();
                } else {
                    rol_companeros = lista_atributos.get(i).getRolCompaneros();
                }
            }

            values[valuesIndex] = sentencesDataset.attribute(valuesIndex++).indexOfValue(rol_companeros);

            //   values[valuesIndex++] = Double.parseDouble(lista_atributos.get(i).getDiferenciaHoras());

        /*    values = getRolesAutodefinidosValues(values, valuesIndex, lista_atributos.get(i).getRolesAutodefinidos());
            valuesIndex = valuesIndex + 9;

           /* values = getRolesCompanerosValues(values, valuesIndex, lista_atributos.get(i).getRolesCompaneros());
            valuesIndex = valuesIndex + 9;

            values = getFreelingValues(values, valuesIndex, instance, instanceIndex);*/

            Instance newInstance = new DenseInstance(1.0, values);

            sentencesDataset.add(newInstance);

        }
        Weka.saveDataset(sentencesDataset, Constants.FASES_LABELED_FOLDER + Constants.FASE_TRES_FOLDER + String.valueOf(System.currentTimeMillis()) + "-roles" + Constants.ARFF_FILE);

    }

}
