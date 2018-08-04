package com.tesis.parser.prediccion;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.tesis.commons.Constants;

import com.tesis.commons.IpaClasiffier;
import com.tesis.commons.Util;
import com.tesis.hangouts.Atributos;
import com.tesis.hangouts.ChatMessage;
import com.tesis.hangouts.Conversation;
import com.tesis.hangouts.MessageContent;
import com.tesis.hangouts.Segment;
import com.tesis.weka.WekaRoles;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.preprocessDataset.FreelingAnalyzer;
import org.weka.Weka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.tesis.hangouts.ConversationStateRoot;
import com.tesis.hangouts.Event;
import com.tesis.hangouts.HangoutsJSON;
import com.tesis.hangouts.ParticipantData;

public abstract class ParserPrediccion {
	
	private static FreelingAnalyzer freelingAnalyzer = FreelingAnalyzer.getInstance("C:\\Users\\franc\\Dropbox\\tesis-backend\\");
	
	//TODO en comun con el comun
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
	
	public void parseJsonTotal(String fileName) throws ParseException {

    	ObjectMapper mapper = new ObjectMapper();
		HangoutsJSON hangoutsJSON;
		try {
			hangoutsJSON = mapper.readValue(new File(fileName), HangoutsJSON.class);
			
			List<ConversationStateRoot> conversationStateRoots = hangoutsJSON.getConversationStatesRoot();
			
			Hashtable<String, String> names = new Hashtable<String, String>();
			for (ConversationStateRoot conversationStateRoot : conversationStateRoots) {
				for (ParticipantData participants : conversationStateRoot.getConversationState().getConversation().getParticipantDataList()) {
					String name = participants.getFallbackName() == null ? participants.getParticipantId().getGaiaId(): participants.getFallbackName();
					if (!names.containsKey(participants.getParticipantId().getGaiaId()))
						names.put(participants.getParticipantId().getGaiaId(), name);
				}
			}
			for (ConversationStateRoot conversationStateRoot : conversationStateRoots) {
				List<Atributos> lista_atributos = new ArrayList<>();
				String fileContent;
				fileContent = getARFFHeader();
				for (Event event : conversationStateRoot.getConversationState().getEvents()) {
					ChatMessage chatMessage;
					MessageContent messageContent;
					Atributos atributos = new Atributos();
					//Fecha
				    Date date = new Date(event.getTimeStamp() / 1000);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String stringDate = formatter.format(date);
                    atributos.setFecha(stringDate);
                    
					if (event.getChatMessage() != null) {
						chatMessage = event.getChatMessage();
						if (chatMessage.getMessageContent() != null) {
							messageContent = chatMessage.getMessageContent();
							if (messageContent.getSegments() != null) {
								for(Segment segment : messageContent.getSegments()) {
									lista_atributos.add(atributos);
									fileContent += "?,'" + Util.addEscapeChar(names.get(event.getSenderId().getGaiaId())) + "','" + Util.addEscapeChar(segment.getText()) + "'" + '\n';
								}
							}
						}
					}
				}
				
				String newFileName;
				if (conversationStateRoot.getConversationState().getConversation().getName() != null) {
				    newFileName = Constants.TEMP_PRED_FOLDER_TO_ORG + conversationStateRoot.getConversationState().getConversation().getName() + Constants.ARFF_FILE;
				}
				else
				    newFileName	= Constants.TEMP_PRED_FOLDER_TO_ORG + conversationStateRoot.getConversationId().getId() + Constants.ARFF_FILE;
				
				saveRolArff(newFileName, fileContent, lista_atributos);
			}
		} catch (IOException e) {
			// TODO tratar excepcion
			e.printStackTrace();
		}
    }
	
	private String getStringFecha(Long timestamp) {
		Date date = new Date(timestamp / 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
	}
	
	public void addAttributesToList(List<Atributos> lista_atributos, String text, Atributos atributos) {
		 ArrayList<String> sentences = new ArrayList<>();
		 if (text != null) {
             sentences = freelingAnalyzer.getSentences(text);
         }
         for (int i = 0; i < sentences.size(); i++) {
             //Para tener en cuenta el split que se hace al preprocesar los datos en el clasificador ipa
             lista_atributos.add(atributos);
         }
	}
	
	public void parseJsonParcial(String conversation) throws ParseException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode messages = mapper.readTree(conversation);
		System.out.println(messages.asText());
		List<Atributos> lista_atributos = new ArrayList<>();
		String fileContent;
		fileContent = getARFFHeader();
		
		for (final JsonNode message : messages) {
			Atributos atributos = new Atributos();
			//Fecha
			Long timestamp = message.get("timestamp").asLong();
            String stringDate = getStringFecha(timestamp);
            atributos.setFecha(stringDate);
            String mensaje = Util.addEscapeChar(message.get("message").asText());
            addAttributesToList(lista_atributos, mensaje, atributos);
			fileContent += "?,'" + message.get("sender").asText() + "','" + mensaje + "'" + '\n';
	    }
		System.out.println(fileContent);
		String newFileName = Constants.TEMP_PRED_FOLDER_TO_ORG + System.currentTimeMillis() + Constants.ARFF_FILE;		
		saveRolArff(newFileName, fileContent, lista_atributos);
		
    }
	
	private void saveRolArff (String fileName, String fileContent, List<Atributos> lista_atributos) throws ParseException, IOException {
        saveToFile(fileName, fileContent);
        System.out.println("Clasificando: " + fileName);
        //TODO pasar el modelo
        IpaClasiffier ipaClasiffier = new IpaClasiffier();
        String pathIpa = ipaClasiffier.parseConductaDirecto(fileName);
        Weka.copyDataset(pathIpa, fileName);
        agregarAtributos (fileName, lista_atributos);
    }
	
	public abstract void agregarAtributos(String pathfile, List<Atributos> lista_atributos) throws IOException;
	
	 protected void saveToFile(String fileName, String fileContent) {
		 
		 	File directory = new File(Constants.TEMP_PRED_FOLDER_TO_ORG);
		 	if (! directory.exists()){
		 		directory.mkdirs();
		    }
		 
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

}
