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

}
