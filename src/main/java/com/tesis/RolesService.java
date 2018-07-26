package com.tesis;

import java.io.FileNotFoundException;
import java.text.ParseException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.json.JSONException;

import com.tesis.commons.IpaClasiffier;

@Path("roles")
public class RolesService {
	
	@GET
	@Path("/mensaje")
	public String sayHello() {
		IpaClasiffier ipaClassifier = new IpaClasiffier();
		try {
			return ipaClassifier.parseConductaDirecto("C:\\Users\\franc\\Dropbox\\tesis-backend\\prueba.arff");
		} catch (FileNotFoundException | JSONException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error al clasificar";
	}

	
	@GET
	@Path("/mensajeHola")
	public String sayHello1() {
		return "<h1>Anda el servicio maven, y sigue andando</h1>";
	}
	
}
