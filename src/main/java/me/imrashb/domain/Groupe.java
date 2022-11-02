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

        StringBuilder sb = new StringBuilder();

        for(Activite a : activites) {
            sb.append(a.toString()+" ");
        }

        return cours.getSigle()+"-"+ numeroGroupe +" "+sb.toString();
    }

    public void addActivite(Activite activite) {
        this.activites.add(activite);
    }

    public boolean overlapsWith(Groupe g) {
        for(Activite a : activites) {
            for(Activite a2 : g.activites) {
                if(a.getHeure().overlapsWith(a2.getHeure())) return true;
            }
        }
        return false;
    }

}
