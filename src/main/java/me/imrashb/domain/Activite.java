package me.imrashb.domain;

import lombok.Data;

@Data
public class Activite {

    private String nom;
    private ModeEnseignement modeEnseignement;
    private HoraireActivite horaire;

    public Activite(String nom, String modeEnseignement, HoraireActivite horaire) {
        this.horaire = horaire;
        this.nom = nom.trim();
        this.modeEnseignement = stringToModeEnseignement(modeEnseignement);
    }

    private ModeEnseignement stringToModeEnseignement(String modeEnseignement) {
        switch (modeEnseignement.trim()) {
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
        return this.nom + " " + horaire;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Activite) {
            Activite a = (Activite) obj;

            return this.nom.equalsIgnoreCase(a.nom) && this.modeEnseignement.equals(a.modeEnseignement) && this.horaire.equals(a.horaire);
        }
        return false;
    }

}
