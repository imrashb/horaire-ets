package me.imrashb.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Activite {

    private String nom;
    private ModeEnseignement modeEnseignement;
    private HoraireActivite horaire;

    private List<String> charges = new ArrayList<>();

    private List<String> locaux = new ArrayList<>();

    public Activite(String nom, String modeEnseignement, HoraireActivite horaire) {
        this.horaire = horaire;
        this.nom = toPrettyNomActivite(nom.trim());
        this.modeEnseignement = convertToModeEnseignement(modeEnseignement);
    }

    public void addCharge(String charge) {
        this.charges.add(charge);
    }

    public void addLocal(String local) {
        this.locaux.add(local);
    }

    private ModeEnseignement convertToModeEnseignement(String modeEnseignement) {
        if (modeEnseignement == null) return null;
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

    private String toPrettyNomActivite(String nomActivite) {

        switch (nomActivite) {
            case "C":
                return "Cours";
            default:
                return nomActivite;
        }

    }

    public String toString() {
        return this.nom + " " + horaire;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Activite) {
            Activite a = (Activite) obj;
            return this.nom.equalsIgnoreCase(a.nom) && (this.modeEnseignement == a.modeEnseignement) && this.horaire.equals(a.horaire);
        }
        return false;
    }

}
