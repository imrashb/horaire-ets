package me.imrashb.domain;

import lombok.*;

@Data
public class Activite {

    private String nom;
    private ModeEnseignement modeEnseignement;
    private HeureActivite heure;

    public Activite(String nom, String modeEnseignement, HeureActivite heure) {
        this.heure = heure;
        this.nom = nom.trim();
        this.modeEnseignement = stringToModeEnseignement(modeEnseignement);
    }

    private ModeEnseignement stringToModeEnseignement(String modeEnseignement) {
        switch(modeEnseignement.trim()) {
            case "P":
                return ModeEnseignement.PRESENTIEL;
            case "D":
                return ModeEnseignement.DISTANCE;
            case "C":
                return ModeEnseignement.COMODAL;
            case "H":
                return ModeEnseignement.HYBRIDE;
            default:
                return null;
        }
    }

    public String toString() {
        return this.nom +" "+ heure;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Activite) {
            Activite a = (Activite) obj;

            if(this.nom.equalsIgnoreCase(a.nom) && this.modeEnseignement.equals(a.modeEnseignement) && this.heure.equals(a.heure)) {
                return true;
            }
        }
        return false;
    }

}
