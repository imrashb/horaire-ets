package me.imrashb.domain;


public enum Jour {
    LUNDI("Lundi"),
    MARDI("Mardi"),
    MERCREDI("Mercredi"),
    JEUDI("Jeudi"),
    VENDREDI("Vendredi"),
    SAMEDI("Samedi"),
    DIMANCHE("Dimanche"),
    UNDEFINED("Indéfini");

    private String nom;

    Jour(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return this.nom;
    }
}
