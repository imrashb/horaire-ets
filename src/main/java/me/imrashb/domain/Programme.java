package me.imrashb.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Programme {


    SEG("seg", "Enseignements généraux"),
    LOG("7084", "Génie logiciel"),
    CUT("5730", "Cheminement universitaire en technologie"),
    CTN("7625", "Génie de la construction"),
    ELE("7694", "Génie électrique"),
    MEC("7684", "Génie mécanique"),
    GOL("6556", "Génie des opérations et de la logistique"),
    GPA("6557", "Génie de la production automatisée"),
    TI("7086", "Génie en technologie de l'information");

    private String id;
    private String nom;

    Programme(String id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public String getId() {
        return this.id;
    }

    public String getNom() {
        return this.nom;
    }

}