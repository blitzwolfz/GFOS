package com.nsj.gfos.award.dataWrappers;

/**
 * Die Klasse <i>Mitarbeiter</i> ist lediglich dazu da, die Tabelle
 * 'mitarbeiter' der Datenbank in einem Java Objekt zusammenzufassen.
 * 
 * @author Schnuels
 */
public class Mitarbeiter {

    private String personalnummer = null;
    private String name = null;
    private String vorname = null;
    private boolean erreichbar = false;
    private int arbeitskonto = -1;
    private String email = null;
    private String status = null;
    private String rechteklasse = null;
    private String abteilung = null;
    private String vertreter = null;
    private String passwort = null;
    private String grundDAbw = null;

    public Mitarbeiter() {

    }

    public String getPersonalnummer() {
        return personalnummer;
    }

    public void setPersonalnummer(String personalnummer) {
        this.personalnummer = personalnummer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public boolean isErreichbar() {
        return erreichbar;
    }

    public void setErreichbar(boolean erreichbar) {
        this.erreichbar = erreichbar;
    }

    public int getArbeitskonto() {
        return arbeitskonto;
    }

    public void setArbeitskonto(int arbeitskonto) {
        this.arbeitskonto = arbeitskonto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRechteklasse() {
        return rechteklasse;
    }

    public void setRechteklasse(String rechteklasse) {
        this.rechteklasse = rechteklasse;
    }

    public String getAbteilung() {
        return abteilung;
    }

    public void setAbteilung(String abteilung) {
        this.abteilung = abteilung;
    }

    public String getVertreter() {
        return vertreter;
    }

    public void setVertreter(String vertreter) {
        this.vertreter = vertreter;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public String getGrundDAbw() {
        return grundDAbw;
    }

    public void setGrundDAbw(String grundDAbw) {
        this.grundDAbw = grundDAbw;
    }

}
