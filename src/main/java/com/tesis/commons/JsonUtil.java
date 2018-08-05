package com.tesis.commons;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	public static String getStringValue(String info, String label) throws Exception {
		try {
			JsonNode node = mapper.readTree(info);
			String retorno = node.get(label).asText();
			return retorno;
		} catch (IOException e) {
			throw new Exception("Error al parsear el Json");
		}
		
		
	}

}
