package me.imrashb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
public class Groupe implements Comparable<Groupe> {

    public static final char SEPARATEUR_SIGLE_NUM_GROUPE = '-';
    private static Pattern patternLabsMultiples = Pattern.compile("^.*\\s([ABCD])$");
    private String numeroGroupe;
    private List<Activite> activites;
    private Cours cours;

    public String toString() {
        return cours.getSigle() + SEPARATEUR_SIGLE_NUM_GROUPE + numeroGroupe;
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

    @Override
    public int hashCode() {
        return Objects.hash(numeroGroupe, activites);
    }

    @Override
    public int compareTo(@NotNull Groupe o) {
        return this.toString().compareTo(o.toString());
    }

    public List<Groupe> createSubGroupes() {


        List<Groupe> sub = new ArrayList<>();

        // Récupère les groupes uniques (càd qui ne sont pas Labo A, Labo B, TP A, etc)
        List<Activite> uniqueActivites = new ArrayList<>();
        for (Activite a : activites) {
            if (!patternLabsMultiples.matcher(a.getNom()).find()) {
                uniqueActivites.add(a);
            }
        }
        // Retourne liste vide si les activités uniques supérieur ou égal à liste d'activité principal
        if (uniqueActivites.size() >= activites.size() - 1) return sub;

        // Création des sousgroupes
        for (Activite a : activites) {
            Matcher m = patternLabsMultiples.matcher(a.getNom());
            if (m.find()) {
                String numeroGroupe = this.numeroGroupe + m.group(1);
                List<Activite> tmp = new ArrayList<>(uniqueActivites);
                tmp.add(a);
                SubGroupe g = new SubGroupe(numeroGroupe, tmp, this);
                sub.add(g);
            }
        }

        return sub;
    }

}
