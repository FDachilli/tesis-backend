package com.tesis.grupos;

import java.util.List;

public class Grupo {
	
	private List<Integrante> integrantes;
	private List<String> rolesFaltantes;
	//Indica si el grupo contiene todos los roles
	private boolean completo;
	
	
	
	public Grupo(List<Integrante> integrantes, List<String> rolesFaltantes, boolean completo) {
		this.integrantes = integrantes;
		this.rolesFaltantes = rolesFaltantes;
		this.completo = completo;
	}

	public List<Integrante> getIntegrantes() {
		return integrantes;
	}
	
	public void setIntegrantes(List<Integrante> integrantes) {
		this.integrantes = integrantes;
	}
	
	public List<String> getRolesFaltantes() {
		return rolesFaltantes;
	}
	
	public void setRolesFaltantes(List<String> rolesFaltantes) {
		this.rolesFaltantes = rolesFaltantes;
	}
	
	public boolean isCompleto() {
		return completo;
	}
	
	public void setCompleto(boolean completo) {
		this.completo = completo;
	}
	
	

}
