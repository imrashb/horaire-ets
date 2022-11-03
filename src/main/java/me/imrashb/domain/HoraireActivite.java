package me.imrashb.domain;

import lombok.*;
import org.apache.commons.lang3.*;

@Data
@NoArgsConstructor
public class HoraireActivite {

    private int heureDepart;
    private int heureFin;
    private Jour jour;

    public HoraireActivite(int heureDepart, int minDepart, int heureFin, int minFin, String jour) {
        this.heureDepart = heureDepart*100 + minDepart;
        this.heureFin = heureFin*100 + minFin;
        this.jour = stringToJour(jour);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof HoraireActivite) {
            HoraireActivite sch = (HoraireActivite) obj;
            if(sch.heureDepart == this.heureDepart && sch.heureFin == this.heureFin && sch.jour == this.jour) {
                return true;
            }
        }
        return false;
    }

    public boolean overlapsWith(HoraireActivite schedule) {


        if(schedule.jour != this.jour) return false;

        if(this.heureDepart <=schedule.heureDepart && schedule.heureDepart < this.heureFin) {
            return true;
        }

        if(this.heureDepart <= schedule.heureFin && schedule.heureFin <= this.heureFin) {
            return true;
        }

        return false;
    }


    private Jour stringToJour(String jour) {
        switch(jour.toLowerCase()) {
            case "lun":
                return Jour.LUNDI;
            case "mar":
                return Jour.MARDI;
            case "mer":
                return Jour.MERCREDI;
            case "jeu":
                return Jour.JEUDI;
            case "ven":
                return Jour.VENDREDI;
            case "sam":
                return Jour.SAMEDI;
            case "dim":
                return Jour.DIMANCHE;
            default:
                return null;
        }
    }

    public String toString() {
        String heureDepart = StringUtils.rightPad(this.heureDepart/100+"", 2, "0")+":"+StringUtils.rightPad(this.heureDepart%100+"", 2, "0");
        String heureFin = StringUtils.rightPad(this.heureFin/100+"", 2, "0")+":"+StringUtils.rightPad(this.heureFin%100+"", 2, "0");
        return "("+this.jour.getNom()+" de "+heureDepart+" à "+(this.heureFin/100)+":"+(this.heureFin%100)+")";
    }

}
