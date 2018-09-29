package com.tesis;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.tesis.commons.JsonUtil;
import com.tesis.grupos.Grupo;
import com.tesis.grupos.RolesGrupos;
import com.tesis.predictor.PredictorDirecto;
import com.tesis.predictor.PredictorFases;

@Path("roles")
public class RolesService {
		
	@GET
	@Path("/predecirDirecto")
	public String predecirDirecto(@QueryParam("conversation") String conversation, @QueryParam("model") String model) throws Exception {
        PredictorDirecto predictorDirecto = new PredictorDirecto();
        String prediccion =  predictorDirecto.predecirDirecto(conversation, model);
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
	@Path("/predecirFases")
	public String predecirFases(@QueryParam("conversation") String conversation, @QueryParam("models") String models) throws Exception {
	
		String model1 = JsonUtil.getStringValue(models, "model1");
		String model2 = JsonUtil.getStringValue(models, "model2");
		String model3 = JsonUtil.getStringValue(models, "model3");
        PredictorFases predictorFases = new PredictorFases();
		return predictorFases.predecirFases(conversation, model1,model2, model3);
	}
	
	@GET
	@Path("/predecirFasesCompuesto")
	public String predecirFasesCompuesto(@QueryParam("conversation") String conversation, @QueryParam("models") String models) throws Exception {
	
		String model1 = JsonUtil.getStringValue(models, "model1");
		String model2 = JsonUtil.getStringValue(models, "model2");
		String model3 = JsonUtil.getStringValue(models, "model3");
		String model4 = JsonUtil.getStringValue(models, "model4");
		String model5 = JsonUtil.getStringValue(models, "model5");
        PredictorFases predictorFases = new PredictorFases();
		return predictorFases.predecirFasesCompuesto(conversation, model1,model2, model3, model4, model5);
	}
	
}
