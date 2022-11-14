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

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.toString());
        sb.append(" / ");

        for(Activite a : activites) {
            sb.append(a.toString());
            sb.append(" / ");
        }
        return sb.toString();
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

    public boolean isDuring(Set<Jour> jours) {
        for(Activite a : activites) {
            if(jours.contains(a.getHoraire().getJour())) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroGroupe, activites);
    }

}
