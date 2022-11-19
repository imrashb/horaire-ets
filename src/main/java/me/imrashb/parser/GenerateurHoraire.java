package me.imrashb.parser;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Groupe;
import me.imrashb.domain.Jour;
import me.imrashb.exception.*;

import java.util.*;

@Data
@AllArgsConstructor
public class GenerateurHoraire {

    private List<Cours> listeCours;
    public static int MAX_NB_COURS = 15;
    public static int MIN_NB_COURS = 1;

    public List<CombinaisonHoraire> getCombinaisonsHoraire(List<Cours> cours, Set<Jour> conges, int nbCours) {

        if(cours.size() > MAX_NB_COURS) {
            throw new InvalidCoursAmountException(MAX_NB_COURS);
        }

        if(cours.size() < MIN_NB_COURS) {
            throw new InvalidCoursAmountException(MIN_NB_COURS);
        }

        NodeGroupe node = new NodeGroupe(null, null, conges);

        List<Set<Cours>> subsets = getSubsets(cours, nbCours);

        for(Set<Cours> sub : subsets) {
            recurCreateCombinaisons(new ArrayList<>(sub), 0, node);
        }

        return node.getValidCombinaisons(nbCours);
    }

    public List<CombinaisonHoraire> getCombinaisonsHoraire(List<Cours> cours, int nbCours) {
        return getCombinaisonsHoraire(cours, new HashSet<>(), nbCours);
    }

    public List<CombinaisonHoraire> getCombinaisonsHoraire(int nbCours, Set<Jour> conges, String... cours) {
        Set<Cours> coursVoulu = new HashSet<>();

        List<String> inexistant = new ArrayList<>(Arrays.asList(cours));

        for (Cours c : listeCours) {
            for (String s : cours) {
                if (c.getSigle().equalsIgnoreCase(s)) {
                    boolean added = coursVoulu.add(c);
                    if(!added) throw new CoursAlreadyPresentException();
                    inexistant.remove(s);
                }
            }
        }

        if(inexistant.size() > 0) {
            throw new CoursDoesntExistException(inexistant);
        }

        return getCombinaisonsHoraire(new ArrayList<>(coursVoulu), conges, nbCours);
    }

    public List<CombinaisonHoraire> getCombinaisonsHoraire(int nbCours, String... cours) {
        return this.getCombinaisonsHoraire(nbCours, new HashSet<>(), cours);
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

            if(!node.isOverlapping(g) && !node.isDuringConges(g)) {
                NodeGroupe n = node.createNode(g);
                recurCreateCombinaisons(cours, index+1, n);
            }

        }

    }

}
