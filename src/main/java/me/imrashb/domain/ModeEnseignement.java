package me.imrashb.domain;

import lombok.Data;

public enum ModeEnseignement {

    COMODAL("Comodal"),
    PRESENTIEL("Présentiel"),
    DISTANCE("Distance"),
    HYBRIDE("Hybride");

    private String nom;

    public String getNom() {
        return this.nom;
    }
    ModeEnseignement(String nom) {
        this.nom = nom;
    }

}
