package com.tesis;

import java.io.FileNotFoundException;
import java.text.ParseException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.json.JSONException;

import com.tesis.commons.IpaClasiffier;
import com.tesis.predictor.PredictorDirecto;

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
	
	@GET
	@Path("/predecirDirecto")
	public String predecirDirecto() throws Exception {
		//TODO tener un path temporal con el archivo y despues borrarlo (cuando mandan una sola conversacion, sino recibis el archivo completo)
		
		String[] namesClasificadores = {"J48"};
		String prediccion="";
        PredictorDirecto predictorDirecto = new PredictorDirecto();
        for (int j = 0; j < namesClasificadores.length; j++) {
            predictorDirecto.predecirDirecto("C:\\Users\\franc\\Dropbox\\tesis-backend\\ResumenDirecto.arff", namesClasificadores[j]);
        }
		
		return "Predicio directo";
	}
	
}
