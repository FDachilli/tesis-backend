package com.tesis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;

import com.tesis.commons.Constants;
import com.tesis.commons.IpaClasiffier;
import com.tesis.commons.JsonUtil;
import com.tesis.grupos.Grupo;
import com.tesis.grupos.RolesGrupos;
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
	@Path("/predecirDirecto")
	public String predecirDirecto(@QueryParam("conversation") String conversation, @QueryParam("model") String model) throws Exception {
        PredictorDirecto predictorDirecto = new PredictorDirecto();
        String prediccion =  predictorDirecto.predecirDirecto(conversation, model, false);
		return prediccion;
	}
	
	@GET
	@Path("/armarGrupos")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Grupo> armarGrupos(@QueryParam("participantes") String participantes, @QueryParam("size") int size) throws Exception {
		RolesGrupos rolesGrupos = new RolesGrupos();
		List<Grupo> grupos = rolesGrupos.armarGrupo(participantes, size);
		return grupos;
	}
	
	@GET
	@Path("/predecirDirectoTotal")
	public String predecirDirectoTotal(@QueryParam("conversation") String conversation, @QueryParam("model") String model) throws Exception {
        PredictorDirecto predictorDirecto = new PredictorDirecto();
        String prediccion =  predictorDirecto.predecirDirecto(conversation, model, true);
		return prediccion;
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
