package me.imrashb;

import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Groupe {

    private String id;
    private List<Activite> activites;
    private Cours cours;

    public String toString() {

        StringBuilder sb = new StringBuilder();

        for(Activite a : activites) {
            sb.append(a.toString()+" ");
        }

        return cours.getId()+"-"+id+" "+sb.toString();
    }

    public void addActivite(Activite activite) {
        this.activites.add(activite);
    }

    public boolean overlapsWith(Groupe g) {
        for(Activite a : activites) {
            for(Activite a2 : g.activites) {
                if(a.getSchedule().overlapsWith(a2.getSchedule())) return true;
            }
        }
        return false;
    }

}
