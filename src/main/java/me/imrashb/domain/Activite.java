package me.imrashb.domain;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Activite {

    private String nom;
    private String modeEnseignement;
    private HeureActivite heure;

    public String toString() {
        return this.nom +" "+ heure;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Activite) {
            Activite a = (Activite) obj;

            if(this.nom.equalsIgnoreCase(a.nom) && this.modeEnseignement.equalsIgnoreCase(a.modeEnseignement) && this.heure.equals(a.heure)) {
                return true;
            }
        }
        return false;
    }

}
