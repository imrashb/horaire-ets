package me.imrashb.parser;

import lombok.Data;
import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Groupe;
import me.imrashb.exception.CoursAlreadyPresentException;
import me.imrashb.exception.CoursDoesntExistException;
import me.imrashb.exception.InvalidCoursAmountException;
import me.imrashb.parser.strategy.GroupeOverlapStrategy;
import me.imrashb.parser.strategy.HoraireValidationStrategy;

import java.util.*;

@Data
public class GenerateurHoraire {

    public static int MAX_NB_COURS = 15;
    public static int MIN_NB_COURS = 1;
    private List<Cours> listeCours;
    private Set<HoraireValidationStrategy> strategies = new HashSet<>();

    public GenerateurHoraire(List<Cours> cours) {
        this.listeCours = cours;
        strategies.add(new GroupeOverlapStrategy());
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
        getSubsets(superSet, k, idx + 1, current, solution);
        current.remove(x);
        //"guess" x is not in the subset
        getSubsets(superSet, k, idx + 1, current, solution);
    }

    public GenerateurHoraire addValidationStrategy(HoraireValidationStrategy strategy) {
        this.strategies.add(strategy);
        return this;
    }

    public List<CombinaisonHoraire> getCombinaisonsHoraire(List<Cours> cours, int nbCours) {

        if (cours.size() > MAX_NB_COURS) {
            throw new InvalidCoursAmountException(MAX_NB_COURS);
        }

        if (cours.size() < MIN_NB_COURS) {
            throw new InvalidCoursAmountException(MIN_NB_COURS);
        }

        NodeGroupe node = new NodeGroupe(null, null, strategies);

        List<Set<Cours>> subsets = getSubsets(cours, nbCours);

        for (Set<Cours> sub : subsets) {
            recurCreateCombinaisons(new ArrayList<>(sub), 0, node);
        }

        return node.getValidCombinaisons(nbCours);
    }

    public List<CombinaisonHoraire> getCombinaisonsHoraire(String[] cours, int nbCours) {
        Set<Cours> coursVoulu = new HashSet<>();

        List<String> inexistant = new ArrayList<>(Arrays.asList(cours));

        for (Cours c : listeCours) {
            for (String s : cours) {
                if (c.getSigle().equalsIgnoreCase(s)) {
                    boolean added = coursVoulu.add(c);
                    if (!added) throw new CoursAlreadyPresentException();
                    inexistant.remove(s);
                }
            }
        }

        if (inexistant.size() > 0) {
            throw new CoursDoesntExistException(inexistant);
        }

        return getCombinaisonsHoraire(new ArrayList<>(coursVoulu), nbCours);
    }

    private List<Set<Cours>> getSubsets(List<Cours> superSet, int k) {
        List<Set<Cours>> res = new ArrayList<>();
        getSubsets(superSet, k, 0, new HashSet<>(), res);
        return res;
    }

    private void recurCreateCombinaisons(List<Cours> cours, int index, NodeGroupe node) {

        if (index == cours.size()) {
            return;
        }

        Cours courant = cours.get(index);

        for (Groupe g : courant.getGroupes()) {
            if (node.isValid(g)) {
                NodeGroupe n = node.createNode(g);
                recurCreateCombinaisons(cours, index + 1, n);
            }
        }

    }

}
