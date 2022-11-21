package me.imrashb.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public enum Jour {
    DIMANCHE("Dimanche", 0),
    LUNDI("Lundi", 1),
    MARDI("Mardi", 2),
    MERCREDI("Mercredi", 3),
    JEUDI("Jeudi", 4),
    VENDREDI("Vendredi", 5),
    SAMEDI("Samedi", 6);

    @JsonIgnore
    private final int id;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String nom;

    Jour(String nom, int id) {
        this.nom = nom;
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public int getId() {
        return this.id;
    }
}
