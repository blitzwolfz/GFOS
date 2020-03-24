package com.nsj.gfos.award.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.nsj.gfos.award.handlers.JsonHandler;
import com.nsj.gfos.award.handlers.PasswordHandler;
import com.nsj.gfos.award.handlers.QueryHandler;
import com.nsj.gfos.award.handlers.RightHandler;
import com.nsj.gfos.award.handlers.SessionHandler;

import com.nsj.gfos.award.gUtils.Utils;

import com.nsj.gfos.award.dataWrappers.Mitarbeiter;
import org.codehaus.jackson.map.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Die Klasse <i>MitarbeiterResource</i> ist eine Resource der Api und wird über
 * den Pfad /api/mitarbeiter angesprochen. ie ist dafür da, die Anfragen des
 * Clients bezüglich der Mitarbeiter zu verwalten.
 * 
 * @author Schnuels
 */
@Path("mitarbeiter")
public class MitarbeiterResource {

	/**
	 * Die Methode <i>getAllMitarbeiter</i> verwaltet die GET-Anfrage an /getAll und
	 * gibt alle auf der Datenbank existierenden Mitarbeiter zurück.
	 * 
	 * @param param - Die Parameter der Anfrage bestehend aus der SessionID(auth)
	 * @return String - Im Falle eines Fehlers eine entsprechende Fehlermeldung und
	 *         bei Erfolg die Mitarbeiter in einer Liste als JSON-Objekt formatiert.
	 */
	@GET
	@Path("getAll{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllMitarbeiter(@PathParam("param") String param) {
		if (param.split("=").length != 2)
			return JsonHandler.fehler("Parameter ist falsch formatiert.");
		String auth = param.split("=")[1];
		if (!SessionHandler.checkSessionID(auth))
			return JsonHandler.fehler("SessionID ist ungültig.");
		if (!RightHandler.checkPermission(auth, "getAllMitarbeiter"))
			return JsonHandler.fehler("Keine Genehmigung für diese Aktion erhalten.");
		ResultSet rs = null;
		try {
			rs = QueryHandler.query("SELECT * FROM gfos.mitarbeiter;");
			ArrayList<Mitarbeiter> allEmpl = new ArrayList<Mitarbeiter>();
			ObjectMapper om = new ObjectMapper();
			while (rs.next()) {
				try {
					Mitarbeiter m = QueryHandler.createMitarbeiterFromQuery(rs,
							new String[] { "Personalnummer", "Name", "Vorname", "erreichbar", "Arbeitskonto", "EMail",
									"Status", "Rechteklasse", "Abteilung", "Vertreter", "gda" });
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

	/**
	 * Die Methode <i>addMitarbeiter</i> verwaltet die GET-Anfrage an /add und fügt
	 * einen neuen Mitarbeiter zur Datenbank hinzu.
	 * 
	 * @param param - Die Parameter der Anfrage bestehend aus SessionID(auth), Name,
	 *              Vorname, Email, Passwort, Rechteklasse, Abteilung und Vertreter
	 * @return String - Im Falle eines Fehlers eine entsprechende Fehlermeldung und
	 *         bei Erfolg die Meldung 'Der Mitarbeiter wurde erfolgreich
	 *         hinzugefügt'.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add{attributes}")
	public String addMitarbeiter(@PathParam("attributes") String query) {
		query = query.substring(1);
		String[] attributes = query.split("&");
		if (attributes.length != 8)
			return JsonHandler.fehler("Falsche Anzahl an Parametern.");
		for (String attribute : attributes) {
			if (attribute.split("=").length != 2)
				return JsonHandler.fehler("Parameter sind falsch formatiert.");
		}
		String auth = attributes[0].split("=")[1];
		if (!SessionHandler.checkSessionID(auth))
			return JsonHandler.fehler("SessionID ist ungültig.");
		String name = "\"" + attributes[1].split("=")[1] + "\"";
		String vorname = "\"" + attributes[2].split("=")[1] + "\"";
		String email = "\"" + attributes[3].split("=")[1] + "\"";
		String passwort = "\"" + PasswordHandler.getHash(attributes[4].split("=")[1]) + "\"";
		String rechteklasse = "\"" + attributes[5].split("=")[1] + "\"";
		String abteilung = "\"" + attributes[6].split("=")[1] + "\"";
		String vertreter = "\"" + attributes[7].split("=")[1] + "\"";
		String pn = Utils.createMitarbeiterID();
		if (rechteklasse.equals("root"))
			return JsonHandler.fehler("Root Account kann nicht erstellt werden.");
		if (!RightHandler.checkPermission(auth, (rechteklasse.equals("admin")) ? "addAdmin" : "addMitarbeiter"))
			return JsonHandler.fehler("Keine Genehmigung für diese Aktion erhalten.");
		String sqlStmt = "INSERT INTO gfos.mitarbeiter VALUES(\"" + pn + "\", " + name + ", " + vorname + ", 0, 0,"
				+ email + ", " + passwort + ", \"Abwesend\", \"-\" , " + rechteklasse + ", " + abteilung + ", "
				+ vertreter + ");";
		if (Utils.checkIfMitarbeiterExists(pn))
			return JsonHandler.fehler("Personalnummer wurde bereits verwendet.");
		try {
			int rs = QueryHandler.update(sqlStmt);
			if (rs == 0 || rs == -1)
				return JsonHandler.fehler("Fehler!");
			return JsonHandler.erfolg("Mitarbeiter wurde erfolgreich hinzugefügt.");
		} catch (SQLException e) {
			return JsonHandler.fehler(e.toString());
		}
	}

	/**
	 * Die Methode <i>getMitarbeiter</i> verwaltet die GET-Anfrage an /get und gibt
	 * den angefragten Mitarbeiter zurück.
	 * 
	 * @param param - Die Parameter der Anfrage bestehend aus der SessionID(auth)
	 *              und der Personalnummer des gesuchten Mitarbeiters
	 * @return String - Im Falle eines Fehlers eine entsprechende Fehlermeldung und
	 *         bei Erfolg dem Mitarbeiter als JSON-Objekt formatiert.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("get{params}")
	public String getMitarbeiter(@PathParam("params") String query) {
		query = query.substring(1);
		String[] params = query.split("&");
		if (params.length != 2)
			return JsonHandler.fehler("Falsche Anzahl an Parametern.");
		if (params[0].split("=").length != 2 || params[1].split("=").length != 2)
			return JsonHandler.fehler("Falsche Formatierung der Parameter.");
		String auth = params[0].split("=")[1];
		String pn = params[1].split("=")[1];
		if (pn.length() != 12)
			return JsonHandler.fehler("Ungültige Personalnummer.");
		if (!Utils.checkIfMitarbeiterExists(pn))
			return JsonHandler.fehler("Mitarbeiter existiert nicht.");
		String action = RightHandler.getGetAction(auth, pn);
		if (!RightHandler.checkPermission(auth, action))
			return JsonHandler.fehler("Keine Genehmigung für diese Aktion erhalten.");
		if (!SessionHandler.checkSessionID(auth))
			return JsonHandler.fehler("SessionID ist ungültig.");
		String columns = RightHandler.getColumns(action);
		String sqlStmt = "SELECT " + columns + " FROM gfos.mitarbeiter WHERE Personalnummer = \"" + pn + "\";";
		try {
			ResultSet rs = QueryHandler.query(sqlStmt);
			if (!rs.next())
				return JsonHandler.fehler("Leere Rückgabe der Datenbank.");
			Mitarbeiter m = QueryHandler.createMitarbeiterFromQuery(rs, columns.split(", "));
			return JsonHandler.createJsonFromMitarbeiter(m);
		} catch (SQLException e) {
			return JsonHandler.fehler(e.toString());
		}
	}

	/**
	 * Die Methode <i>removeMitarbeiter</i> verwaltet die GET-Anfrage an /remove und
	 * entfernt einen Mitarbeiter aus der Datenbank.
	 * 
	 * @param param - Die Parameter der Anfrage bestehend aus der SessionID(auth)
	 *              und der Personalnummer des Mitarbeiters
	 * @return String - Im Falle eines Fehlers eine entsprechende Fehlermeldung und
	 *         bei Erfolg die Erfolgsmeldung 'Mitarbeiter wurde erfolgreich
	 *         gelöscht'.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("remove{params}")
	public String removeMitarbeiter(@PathParam("params") String query) {
		query = query.substring(1);
		String[] params = query.split("&");
		if (params.length != 2)
			return JsonHandler.fehler("Falsche Anzahl an Parametern.");
		if (params[0].split("=").length != 2 || params[1].split("=").length != 2)
			return JsonHandler.fehler("Falsche Formatierung der Parameter.");
		String auth = params[0].split("=")[1];
		String pn = params[1].split("=")[1];
		if (pn.length() != 12 || !Utils.checkIfMitarbeiterExists(pn))
			return JsonHandler.fehler("Ungültige Personalnummer.");
		if (RightHandler.getRightClassFromPersonalnummer(pn).equals("root"))
			return JsonHandler.fehler("Root Account kann nicht entfernt werden.");
		if (!SessionHandler.checkSessionID(auth))
			return JsonHandler.fehler("SessionID ist ungültig.");
		if (Utils.checkSelfOperation(auth, pn))
			return JsonHandler.fehler("Ein Account kann nicht sich selbst löschen.");
		if (!RightHandler.checkPermission(auth,
				(RightHandler.getRightClassFromPersonalnummer(pn).equals("admin") ? "removeAdmin"
						: "removeMitarbeiter")))
			return JsonHandler.fehler("Keine Genehmigung für diese Aktion erhalten.");
		String sqlStmt = "DELETE FROM gfos.mitarbeiter WHERE Personalnummer = \"" + pn + "\";";
		try {
			int rs = QueryHandler.update(sqlStmt);
			if (rs == 0)
				return JsonHandler.fehler("Personalnummer existiert nicht.");
			if (Utils.checkIfMitarbeiterExists(pn))
				return JsonHandler.fehler(Utils.checkReferencesInDatabase(pn));
			return JsonHandler.erfolg("Mitarbeiter wurde erfolgreich gelöscht.");
		} catch (SQLException e) {
			return JsonHandler.fehler(e.toString());
		}
	}

	/**
	 * Die Methode <i>alterAttribute</i> verwaltet die GET-Anfrage an /alter und
	 * ändert die Werte einzelner Attribute eines Mitarbeiters.
	 * 
	 * @param param - Die Parameter der Anfrage bestehend aus der SessionID(auth),
	 *              der Personalnummer des Mitarbeiters und einer beliebigen
	 *              Kombination der Attribute Name, Vorname, erreichbar, Email,
	 *              Arbeitskonto, Status, Rechteklasse, Grund der Abwesenheit,
	 *              Abteilung und Vertreter.
	 * @return String - Im Falle eines Fehlers eine entsprechende Fehlermeldung und
	 *         bei Erfolg die Erfolgsmeldung 'Werte wurden erfolgreich verändert'.
	 */
	@GET
	@Path("alter{params}")
	@Produces(MediaType.APPLICATION_JSON)
	public String alterAttribute(@PathParam("params") String query) {
		query = query.substring(1);
		String[] params = query.split("&");
		String[] validParams = { "n", "vn", "er", "em", "ak", "s", "rk", "gda", "ab", "ve" };
		if (params.length < 3)
			return JsonHandler.fehler("Zu wenige Parameter.");
		if (params[0].split("=").length != 2 || !params[0].split("=")[0].equals("auth"))
			return JsonHandler.fehler("SessionID benötigt.");
		if (!SessionHandler.checkSessionID(params[0].split("=")[1]))
			return JsonHandler.fehler("Ungültige SessionID.");
		if (params[1].split("=").length != 2 || !params[1].split("=")[0].equals("pn"))
			return JsonHandler.fehler("Keine Personalnummer angegeben.");
		String pn = params[1].split("=")[1];
		if (params[1].split("=")[1].length() != 12 || !Utils.checkIfMitarbeiterExists(pn))
			return JsonHandler.fehler("Ungültige Personalnummer angegeben.");
		String values = "";
		for (int i = 2; i < params.length; i++) {
			if (params[i].split("=").length != 2)
				return JsonHandler.fehler("Parameter falsch formatiert.");
			if (!Arrays.asList(validParams).contains(params[i].split("=")[0]))
				return JsonHandler.fehler("Kein valider Parameter.");
			String action = RightHandler.getAlterAction(params[0].split("=")[1], pn);
			if (!RightHandler.checkPermission(params[0].split("=")[1], action))
				return JsonHandler.fehler("Keine Genehmigung für diese Aktion erhalten.");
			values += Utils.getColumnName(params[i].split("=")[0]) + " = "
					+ Utils.getFormattedValue(params[i].split("=")) + ", ";
		}
		String sqlStmt = "UPDATE gfos.mitarbeiter SET " + values.substring(0, values.length() - 2)
				+ " WHERE Personalnummer = \"" + pn + "\";";
		try {
			int rs = QueryHandler.update(sqlStmt);
			if (rs == 0)
				return JsonHandler.fehler("Die Veränderungen konnten nicht durchgeführt werden.");
			return JsonHandler.erfolg("Werte wurden erfolgreich verändert.");
		} catch (SQLException e) {
			return JsonHandler.fehler(e.toString()); 
		}
	}

	/**
	 * Die Methode <i>alterPassword</i> verwaltet die GET-Anfrage an /alterPassword
	 * und verändert das Passwort eines Mitarbeiters.
	 * 
	 * @param param - Die Parameter der Anfrage bestehend aus der SessionID(auth),
	 *              dem alten Passwort und dem neuen Passwort des Mitarbeiters
	 * @return String - Im Falle eines Fehlers eine entsprechende Fehlermeldung und
	 *         bei Erfolg die Erfolgsmeldung 'Passwort wurde erfolgreich verändert'.
	 */
	@GET
	@Path("alterPassword{params}")
	@Produces(MediaType.APPLICATION_JSON)
	public String alterPassword(@PathParam("params") String query) {
		query = query.substring(1);
		String[] params = query.split("&");
		if (params.length != 3)
			return JsonHandler.fehler("Falsche Anzahl an Parametern.");
		for (String att : params) {
			if (att.split("=").length != 2)
				return JsonHandler.fehler("Parameter sind falsch formatiert.");
		}
		String auth = params[0].split("=")[1];
		String oldPassword = params[1].split("=")[1];
		String newPassword = params[2].split("=")[1];
		if (!SessionHandler.checkSessionID(auth))
			return JsonHandler.fehler("Ungültige SessionID angegeben.");
		String personalnummer = Utils.getPersonalnummerFromSessionID(auth);
		if (!PasswordHandler.checkPassword(oldPassword, personalnummer))
			return JsonHandler.fehler("Falsches altes Passwort eingegeben.");
		if (oldPassword.equals(newPassword))
			return JsonHandler.fehler("Das neue Passwort darf nicht dem alten entsprechen.");
		try {
			String sqlStmt = "UPDATE gfos.mitarbeiter SET Passwort = \"" + PasswordHandler.getHash(newPassword)
					+ "\" WHERE Personalnummer = \"" + personalnummer + "\";";
			int result = QueryHandler.update(sqlStmt);
			if (result == 0)
				return JsonHandler.fehler("Die Veränderung konnte aufgrund eines Fehlers nicht ausgeführt werden.");
			return JsonHandler.erfolg("Passwort wurde erfolgreich verändert.");
		} catch (SQLException e) {
			return JsonHandler.fehler(e.toString());
		}
	}
}
