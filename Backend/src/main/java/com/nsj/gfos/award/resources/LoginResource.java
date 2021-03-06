package com.nsj.gfos.award.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.nsj.gfos.award.gUtils.Utils;
import com.nsj.gfos.award.handlers.JsonHandler;
import com.nsj.gfos.award.handlers.PasswordHandler;
import com.nsj.gfos.award.handlers.SessionHandler;

/**
 * Die Klasse LoginResource ist eine Resource der Api und wird über den Pfad
 * /api/login angesprochen. Sie ist dafür da, einen Client an den Webservice
 * anzumelden.
 * 
 * @author Schnuels
 */
@Path("login:{auth}&{email}&{password}")
public class LoginResource {

	/**
	 * Die Methode <i>login</i> wird bei einer Anfrage an die Resource aufgerufen
	 * und prüft verschiedenste Fehler und die Richtigkeit der Anmeldedaten, bevor
	 * sie über den <i>SessionHandler</i> eine neue Session für den Mitarbeiter
	 * erstellt.
	 * 
	 * @param entAuth - der Teil der URl der die SessionID enthält
	 * @param entEm - der Teil der URL der die Email enthält
	 * @param entPw - der Teil der URL der das Passwort enthält
	 * @return String - über den <i>JsonHandler</i> formatierter Fehler oder die
	 *         Rückgabe von <i>SessionHandler.createSession(...)</i>
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String login(@PathParam("auth") String entAuth, @PathParam("email") String entEm, @PathParam("password") String entPw) {
		String[] auth = entAuth.split("=");
		String[] em = entEm.split("=");
		String[] pw = entPw.split("=");
		if (auth.length != 2 || em.length != 2 || pw.length != 2)
			return JsonHandler.fehler("Falsche Parameter Syntax.");
		if (auth[1].length() != 12)
			return JsonHandler.fehler("Parameter sind falsch formatiert.");
		String pn = Utils.getPersonalnummerFromEmail(em[1]);
		if (!PasswordHandler.checkPassword(pw[1], pn))
			return JsonHandler.fehler("Passwort oder Email falsch oder Benutzer existiert nicht.");
		if (SessionHandler.checkIfUserIsConnected(pn))
			return JsonHandler.fehler("Dieser Benutzer ist bereits angemeldet.");
		if (!SessionHandler.changeStatus(pn, "Online"))
			return JsonHandler.fehler("Status konnte aufgrund eines Fehlers nicht geändert werden.");
		if (!SessionHandler.changeErreichbar(pn, 1))
			return JsonHandler.fehler("Erreichbarkeit konnte aufgrund eines Fehlers nicht geändert werden.");
		return SessionHandler.createSession(new String[] { auth[1], pn });
	}

}
