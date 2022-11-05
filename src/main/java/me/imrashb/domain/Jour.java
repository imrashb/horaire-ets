package me.imrashb.domain;

import com.fasterxml.jackson.annotation.*;

public enum Jour {
    LUNDI("Lundi", 0),
    MARDI("Mardi", 1),
    MERCREDI("Mercredi", 2),
    JEUDI("Jeudi", 3),
    VENDREDI("Vendredi", 4),
    SAMEDI("Samedi", 5),
    DIMANCHE("Dimanche", 6);

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String nom;

    @JsonIgnore
    private final int id;

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
