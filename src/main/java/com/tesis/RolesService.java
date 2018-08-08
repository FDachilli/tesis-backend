package com.tesis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;

import com.tesis.commons.Constants;
import com.tesis.commons.IpaClasiffier;
import com.tesis.commons.JsonUtil;
import com.tesis.organizador.OrganizadorPrediccion;
import com.tesis.parser.prediccion.ParserPrediccion;
import com.tesis.predictor.PredictorDirecto;
import com.tesis.predictor.PredictorFase2;
import com.tesis.predictor.PredictorFase3;
import com.tesis.predictor.PredictorFases;
import com.tesis.predictor.grupo.PredictorDirectoGrupo;

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
	public String sayHello1(@QueryParam("foo") String foo) throws ParseException, IOException {
		ParserPrediccion parserPrediccionDirecto = new ParserPrediccion();
		parserPrediccionDirecto.parseJsonParcial(foo);
		OrganizadorPrediccion organizadorPrediccion = new OrganizadorPrediccion();
		organizadorPrediccion.organizar_carpeta(Constants.TEMP_PRED_FOLDER_TO_ORG, Constants.TEMP_PRED_FOLDER_TO_ORG + "resumen.arff");
		return "Anda el servicio maven, y sigue andando";
	}
	
	@GET
	@Path("/predecirDirecto")
	public String predecirDirecto(@QueryParam("conversation") String conversation, @QueryParam("model") String model) throws Exception {
        PredictorDirecto predictorDirecto = new PredictorDirecto();
        String prediccion =  predictorDirecto.predecirDirecto(conversation, model, false);
		return prediccion;
	}
	
	@GET
	@Path("/predecirDirectoTotal")
	public String predecirDirectoTotal(@QueryParam("conversation") String conversation, @QueryParam("model") String model) throws Exception {
        PredictorDirecto predictorDirecto = new PredictorDirecto();
        String prediccion =  predictorDirecto.predecirDirecto(conversation, model, true);
		return prediccion;
	}
	
	
	@GET
	@Path("/predecirFase2")
	public String predecirFase2() throws Exception {
		//TODO tener un path temporal con el archivo y despues borrarlo (cuando mandan una sola conversacion, sino recibis el archivo completo)
		
		String[] namesClasificadores = {"J48"};
		String prediccion="";
        PredictorFase2 predictorDirectoGrupo = new PredictorFase2();
        for (int j = 0; j < namesClasificadores.length; j++) {
        //	predictorDirectoGrupo.predecirFase2("C:\\Users\\franc\\Dropbox\\tesis-backend\\ResumenFase2.arff", namesClasificadores[j]);
        }
		
		return "Predijo fase 2";
	}
	
	@GET
	@Path("/predecirFases")
	public String predecirFases(@QueryParam("conversation") String conversation, @QueryParam("models") String models) throws Exception {
	
		String model2 = JsonUtil.getStringValue(models, "model2");
		String model3 = JsonUtil.getStringValue(models, "model3");
        PredictorFases predictorFases = new PredictorFases();
		return predictorFases.predecirFases(conversation, model2, model3, false);
	}
	
}
