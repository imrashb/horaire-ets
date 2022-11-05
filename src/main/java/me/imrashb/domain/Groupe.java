package me.imrashb.domain;

import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Groupe {

    private String numeroGroupe;

    private List<Activite> activites;

    private Cours cours;
    public String toString() {
        return cours.getSigle()+"-"+ numeroGroupe;
    }

    public void addActivite(Activite activite) {
        this.activites.add(activite);
    }

    public boolean overlapsWith(Groupe g) {
        for(Activite a : activites) {
            for(Activite a2 : g.activites) {
                if(a.getHoraire().overlapsWith(a2.getHoraire())) return true;
            }
        }
        return false;
    }

}
