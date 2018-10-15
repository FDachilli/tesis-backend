package com.tesis.grupos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RolesGrupos {
	
	/**
     * Arma grupos balanceados a partir de una lista de participantes y un tamaño
     * @param participantes json con la lista de participantes
     * @param size tamaño de los grupos
     * @return List<Grupo> grupos armados
     */
	public List<Grupo> armarGrupo (String participantes ,int size) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		List<Integrante> participantesRoles = mapper.readValue(participantes, new TypeReference<List<Integrante>>(){});
		List<Grupo> grupos = new ArrayList<>();
		boolean termino = false;
		while (!termino) {
			List<String> rolesFaltantes = new ArrayList<String>() {{
			    add("coordinador");
			    add("implementador");
			    add("cerebro");
			    add("colaborador");
			    add("finalizador");
			    add("investigador");
			    add("monitor");
			    add("especialista");
			    add("impulsor");
			}};;
			List<Integrante> integrantes = new ArrayList<>();
			boolean completo_grupo = false;
			ListIterator<Integrante> iter = participantesRoles.listIterator();
			while(!completo_grupo && iter.hasNext()) {
				Integrante participante = iter.next();
				//Primero se intenta armar grupos con aquellos participantes que más roles cumplan
				if (!participante.getRolPrincipal().equals(participante.getRolSecundario()) && rolesFaltantes.contains(participante.getRolPrincipal()) && rolesFaltantes.contains(participante.getRolSecundario())){
					integrantes.add(participante);	
					rolesFaltantes.remove(participante.getRolPrincipal());
					rolesFaltantes.remove(participante.getRolSecundario());
					iter.remove();
				}
				if (rolesFaltantes.isEmpty() || integrantes.size() == size) {
					completo_grupo = true;
				}
				if (participantesRoles.isEmpty()) {
					termino = true;
				}
			}
			iter = participantesRoles.listIterator();
			while(!completo_grupo && iter.hasNext()) {
				Integrante participante = iter.next();
				//Despues se intenta insertar integrantes que cumplan con al menos un rol
				if (rolesFaltantes.contains(participante.getRolPrincipal()) || rolesFaltantes.contains(participante.getRolSecundario())){
					integrantes.add(participante);
					if (rolesFaltantes.contains(participante.getRolPrincipal()) || participante.getRolPrincipal().equals(participante.getRolSecundario())) {
						rolesFaltantes.remove(participante.getRolPrincipal());
					}else {
						rolesFaltantes.remove(participante.getRolSecundario());
					}		
					iter.remove();
				}
				if (rolesFaltantes.isEmpty() || integrantes.size() == size) {
					completo_grupo = true;
				}
				if (participantesRoles.isEmpty()) {
					termino = true;
				}
			}
			if (integrantes.size() == 1) {
				for(Grupo grupo: grupos) {
					if (grupo.getIntegrantes().size()< size) {
						grupo.getIntegrantes().add(integrantes.get(0));
						break;
					}
				}
			}else {
				Grupo grupo = new Grupo(integrantes, rolesFaltantes, rolesFaltantes.isEmpty());
				grupos.add(grupo);
			}
			
		}
		return grupos;
	}

}
