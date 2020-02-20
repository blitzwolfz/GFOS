/*
d * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nsj.gfos.award.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.nsj.gfos.award.handlers.JsonHandler;
import com.nsj.gfos.award.handlers.PasswordHandler;
import com.nsj.gfos.award.handlers.QueryHandler;
import com.nsj.gfos.award.handlers.SessionHandler;
import com.nsj.gfos.award.dataWrappers.Mitarbeiter;
import org.codehaus.jackson.map.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("mitarbeiter")
public class MitarbeiterResource {
    
    @GET
    @Path("getAll{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllMitarbeiter(@PathParam("param") String param) {
    	if(param.split("=").length != 2)    		
    		return JsonHandler.fehler("Parameter ist falsch formatiert.");
    	String auth = param.split("=")[1];
    	if(!SessionHandler.checkSessionID(auth))
    		return JsonHandler.fehler("SessionID ist ungültig.");
    	//TODO checkRights
        ResultSet rs = null;
        try {
            rs = QueryHandler.query("SELECT * FROM mitarbeiter;");
            ArrayList<Mitarbeiter> allEmpl = new ArrayList<Mitarbeiter>();
            ObjectMapper om = new ObjectMapper();
            while(rs.next()) {
                try {
                    Mitarbeiter m  = createMitarbeiterFromQuery(rs);
                    allEmpl.add(m);
                } catch (Exception e) {
                	return JsonHandler.fehler(e.toString());
                }       
            }
            try {
                return om.writeValueAsString(allEmpl);
            } catch (Exception e) {
            	return JsonHandler.fehler(e.toString());
            }          
        } catch (Exception e) {
        	return JsonHandler.fehler(e.toString());
        }            
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("add{attributes}")
    public String addMitarbeiter(@PathParam("attributes") String query) {
    	query = query.substring(1);
    	String[] attributes = query.split("&");
    	if(attributes.length != 13)
    		return JsonHandler.fehler("Falsche Anzahl an Parametern.");
    	for(String attribute: attributes) {
    		if(attribute.split("=").length != 2)
    			return JsonHandler.fehler("Parameter sind falsch formatiert.");
    	}
    	String auth = attributes[0].split("=")[1];
    	if(!SessionHandler.checkSessionID(auth))
    		return JsonHandler.fehler("SessionID ist ungültig.");
    	//TODO checkRights
    	String pn = "\"" + attributes[1].split("=")[1] + "\"";
    	String name = "\"" + attributes[2].split("=")[1] + "\"";
    	String vorname = "\"" + attributes[3].split("=")[1] + "\"";
    	String erreichbar = attributes[4].split("=")[1];
    	String arbeitskonto = attributes[5].split("=")[1];
    	String email = "\"" + attributes[6].split("=")[1] + "\"";
    	String passwort = "\"" + PasswordHandler.getHash(attributes[7].split("=")[1]) + "\"";
    	String status = "\"" + attributes[8].split("=")[1] + "\"";
    	String gda = "\"" + attributes[9].split("=")[1] + "\"";
    	String rechteklasse = "\"" + attributes[10].split("=")[1] + "\"";
    	String abteilung = "\"" + attributes[11].split("=")[1] + "\"";
    	String vertreter = "\"" + attributes[12].split("=")[1] + "\"";
    	String sqlStmt = "INSERT INTO gfos.mitarbeiter VALUES("+pn+","+name+","+vorname+","+erreichbar+","+arbeitskonto+","+email+","+passwort+","+status+","+gda+","+rechteklasse+","+abteilung+","+vertreter+");";
    	if(checkIfMitarbeiterExists(pn))
			return JsonHandler.fehler("Personalnummer wurde bereits verwendet.");
    	try {
    		int rs = QueryHandler.update(sqlStmt);
			if(rs == 0)
				return JsonHandler.fehler("Fehler!");			
			return JsonHandler.erfolg("Mitarbeiter wurde erfolgreich hinzugefügt.");
		} catch (SQLException e) {
			return JsonHandler.fehler(e.toString());
		}
    }
    //http://localhost:8080/award/api/mitarbeiter/add:auth=123456789012&pn=000000000001&n=Sommerfeld&vn=Nils&er=0&ak=20&em=n.s@e.de&pw=1234&s=Abwesend&gda=Feierabend&rk=Admin&ab=IT-Sicherheit&ve=000000000000
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get{params}")    
    public String getMitarbeiter(@PathParam("params") String query) {
    	query = query.substring(1);    	
    	String[] params = query.split("&");
    	if(params.length != 2)
    		return JsonHandler.fehler("Falsche Anzahl an Parametern.");
    	if(params[0].split("=").length != 2 || params[1].split("=").length != 2)
    		return JsonHandler.fehler("Falsche Formatierung der Parameter.");
    	String auth = params[0].split("=")[1];
    	String pn = params[1].split("=")[1];
    	if(pn.length() != 12)
    		return JsonHandler.fehler("Ungültige Personalnummer.");
    	//TODO checkRights
    	if(!SessionHandler.checkSessionID(auth))
    		return JsonHandler.fehler("SessionID ist ungültig.");
    	String sqlStmt = "SELECT * FROM gfos.mitarbeiter WHERE Personalnummer = \"" + pn + "\";";
    	try {
			ResultSet rs = QueryHandler.query(sqlStmt);
			if(!rs.next())
				return JsonHandler.fehler("Leere Rückgabe der Datenbank.");
			Mitarbeiter m = createMitarbeiterFromQuery(rs);
			return JsonHandler.createJsonFromMitarbeiter(m);
		} catch (SQLException e) {
			return JsonHandler.fehler(e.toString());
		}
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("remove{params}")
    public String removeMitarbeiter(@PathParam("params") String query) {
    	query = query.substring(1);
    	String[] params = query.split("&");
    	if(params.length != 2)
    		return JsonHandler.fehler("Falsche Anzahl an Parametern.");
    	if(params[0].split("=").length != 2 || params[1].split("=").length != 2)
    		return JsonHandler.fehler("Falsche Formatierung der Parameter.");
    	String auth = params[0].split("=")[1];
    	String pn = params[1].split("=")[1];
    	if(pn.length() != 12)
    		return JsonHandler.fehler("Ungültige Personalnummer.");    	    	
    	if(!SessionHandler.checkSessionID(auth))
    		return JsonHandler.fehler("SessionID ist ungültig.");
    	//TODO check if pn == sessionID.pn
    	//TODO checkRights
    	//TODO CLeanup References in other tables.
    	String sqlStmt = "DELETE FROM gfos.mitarbeiter WHERE Personalnummer = \"" + pn + "\";";
    	try {
			int rs = QueryHandler.update(sqlStmt);
			if(rs == 0)
				return JsonHandler.fehler("Personalnummer existiert nicht.");
			if(checkIfMitarbeiterExists("\"" + pn + "\""))
				return JsonHandler.fehler("Mitarbeiter konnte aufgrund eines Fehlers nicht gelöscht werden.");
			return JsonHandler.erfolg("Mitarbeiter wurde erfolgreich gelöscht.");
		} catch (SQLException e) {
			return JsonHandler.fehler(e.toString());
		}
    }
    
    private boolean checkIfMitarbeiterExists(String pn) {
    	String sqlStmt = "SELECT * FROM gfos.mitarbeiter WHERE Personalnummer = " + pn + ";";
    	try {
			ResultSet rs = QueryHandler.query(sqlStmt);
			return rs.next();
		} catch (SQLException e) {			
			return true;
		}
    }
    
    private Mitarbeiter createMitarbeiterFromQuery(ResultSet rs) throws SQLException{
        Mitarbeiter m = new Mitarbeiter();
        m.setPersonalnummer(rs.getString("Personalnummer"));
        m.setName(rs.getString("Name"));
        m.setVorname(rs.getString("Vorname"));        
        m.setErreichbar((rs.getString("erreichbar").equals("1")) ? true : false);
        m.setArbeitskonto(Integer.parseInt(rs.getString("Arbeitskonto")));
        m.setEmail(rs.getString("EMail"));
        m.setStatus(rs.getString("Status"));
        m.setRechteklasse(rs.getString("Rechteklasse"));
        m.setAbteilung(rs.getString("Abteilung"));
        m.setVertreter(rs.getString("Vertreter"));
        m.setPasswort(rs.getString("Passwort"));
        m.setGrundDAbw(rs.getString("gda"));
        return m;
    }
       
}
