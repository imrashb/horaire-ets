package me.imrashb.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

public enum Jour {
    LUNDI("Lundi"),
    MARDI("Mardi"),
    MERCREDI("Mercredi"),
    JEUDI("Jeudi"),
    VENDREDI("Vendredi"),
    SAMEDI("Samedi"),
    DIMANCHE("Dimanche");

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String nom;

    Jour(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return this.nom;
    }
}
