package com.nsj.gfos.award.handlers;

import org.codehaus.jackson.map.ObjectMapper;

import com.nsj.gfos.award.dataWrappers.Mitarbeiter;
import com.nsj.gfos.award.dataWrappers.Arbeitsgruppe;

public class JsonHandler {

	public static String fehler(String msg) {
		return "{\"fehler\": \"" + msg + "\"}";
	}

	public static String erfolg(String msg) {
		return "{\"erfolg\": \"" + msg + "\"}";
	}

	public static String createJsonFromMitarbeiter(Mitarbeiter m) {
		ObjectMapper om = new ObjectMapper();
		try {
			return  om.writeValueAsString(m);
		} catch (Exception e) {			
			return fehler(e.toString());
		}
	}
	
	public static String createJsonFromArbeitsgruppe(Arbeitsgruppe a) {
		ObjectMapper om = new ObjectMapper();
		try {
			return  om.writeValueAsString(a);
		} catch (Exception e) {			
			return fehler(e.toString());
		}
	}

}
