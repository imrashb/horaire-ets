package me.imrashb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Groupe implements Comparable<Groupe> {

    public static final char SEPARATEUR_SIGLE_NUM_GROUPE = '-';
    private String numeroGroupe;
    private List<Activite> activites;
    private Cours cours;

    public String toString() {
        return cours.getSigle() + SEPARATEUR_SIGLE_NUM_GROUPE + numeroGroupe;
    }

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this);
        sb.append(" / ");

        for (Activite a : activites) {
            sb.append(a.toString());
            sb.append(" / ");
        }
        return sb.toString();
    }

    public void addActivite(Activite activite) {
        this.activites.add(activite);
    }

    public boolean overlapsWith(Groupe g) {
        for (Activite a : activites) {
            for (Activite a2 : g.activites) {
                if (a.getHoraire().overlapsWith(a2.getHoraire())) return true;
            }
        }
        return false;
    }

    public boolean isDuring(Set<Jour> jours) {
        for (Activite a : activites) {
            if (jours.contains(a.getHoraire().getJour())) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroGroupe, activites);
    }

    @Override
    public int compareTo(@NotNull Groupe o) {
        return this.toString().compareTo(o.toString());
    }
}
