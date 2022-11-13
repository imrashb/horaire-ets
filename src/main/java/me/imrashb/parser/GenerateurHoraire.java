package me.imrashb.parser;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Groupe;
import me.imrashb.exception.*;

import java.util.*;

@Data
@AllArgsConstructor
public class GenerateurHoraire {

    private List<Cours> listeCours;
    public static int MAX_NB_COURS = 15;

    public List<CombinaisonHoraire> getCombinaisonsHoraire(List<Cours> cours, int nbCours) {

        if(cours.size() > MAX_NB_COURS) {
            throw new TooManyCoursException();
        }

        NodeGroupe node = new NodeGroupe(null, null);

        List<Set<Cours>> subsets = getSubsets(cours, nbCours);

        for(Set<Cours> sub : subsets) {
            recurCreateCombinaisons(new ArrayList<>(sub), 0, node);
        }

        return node.getValidCombinaisons(cours, nbCours);
    }

    public List<CombinaisonHoraire> getCombinaisonsHoraire(int nbCours, String... cours) {
        List<Cours> coursVoulu = new ArrayList<>();

        List<String> inexistant = new ArrayList<>(Arrays.asList(cours));

        for (Cours c : listeCours) {
            for (String s : cours) {
                if (c.getSigle().equalsIgnoreCase(s)) {
                    coursVoulu.add(c);
                    inexistant.remove(s);
                }
            }
        }

        if(inexistant.size() > 0) {
            throw new CoursDoesntExistException(inexistant);
        }

        return getCombinaisonsHoraire(coursVoulu, nbCours);
    }

    private static void getSubsets(List<Cours> superSet, int k, int idx, Set<Cours> current, List<Set<Cours>> solution) {
        //successful stop clause
        if (current.size() == k) {
            solution.add(new HashSet<>(current));
            return;
        }
        //unseccessful stop clause
        if (idx == superSet.size()) return;
        Cours x = superSet.get(idx);
        current.add(x);
        //"guess" x is in the subset
        getSubsets(superSet, k, idx+1, current, solution);
        current.remove(x);
        //"guess" x is not in the subset
        getSubsets(superSet, k, idx+1, current, solution);
    }

    private List<Set<Cours>> getSubsets(List<Cours> superSet, int k) {
        List<Set<Cours>> res = new ArrayList<>();
        getSubsets(superSet, k, 0, new HashSet<Cours>(), res);
        return res;
    }

    private void recurCreateCombinaisons(List<Cours> cours, int index, NodeGroupe node) {

        if(index == cours.size()) {
            return;
        }

        Cours courant = cours.get(index);

        for(Groupe g : courant.getGroupes()) {

            if(!node.isOverlapping(g)) {
                NodeGroupe n = node.createNode(g);
                recurCreateCombinaisons(cours, index+1, n);
            }

        }

    }

}
