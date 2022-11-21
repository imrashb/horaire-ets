package me.imrashb.domain;

public enum ModeEnseignement {

    COMODAL("Comodal"),
    PRESENTIEL("Présentiel"),
    DISTANCE("Distance"),
    HYBRIDE("Hybride");

    private String nom;

    ModeEnseignement(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return this.nom;
    }

}
