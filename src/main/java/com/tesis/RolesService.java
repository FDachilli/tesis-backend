package com.tesis;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("roles")
public class RolesService {
	
	@GET
	@Path("/mensaje")
	public String sayHello() {
		return "<h1>Anda el servicio maven</h1>";
	}

	
	@GET
	@Path("/mensajeHola")
	public String sayHello1() {
		return "<h1>Anda el servicio maven, y sigue andando</h1>";
	}
	
}
