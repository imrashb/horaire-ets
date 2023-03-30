package me.imrashb.parser;

import lombok.Data;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Groupe;
import me.imrashb.domain.Jour;
import me.imrashb.domain.ParametresCombinaison;
import me.imrashb.domain.combinaison.CombinaisonHoraire;
import me.imrashb.exception.InvalidCoursAmountException;
import me.imrashb.parser.strategy.CongeStrategy;
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

    public void populateValidationStrategies(ParametresCombinaison parametres) {
        strategies.clear();
        strategies.add(new GroupeOverlapStrategy());

        if (parametres.getConges() != null && parametres.getConges().length > 0) {
            strategies.add(new CongeStrategy(new HashSet<Jour>(Arrays.asList(parametres.getConges()))));
        }

    }

    public List<CombinaisonHoraire> getCombinaisonsHoraire(ParametresCombinaison parametres) {

        if (parametres.getListeCours().size() > MAX_NB_COURS) {
            throw new InvalidCoursAmountException(MAX_NB_COURS);
        }

        if (parametres.getListeCours().size() < MIN_NB_COURS) {
            throw new InvalidCoursAmountException(MIN_NB_COURS);
        }

        NodeGroupe node = new NodeGroupe(null, null, null, strategies);

        List<Set<Cours>> subsets = getSubsets(parametres.getListeCours(), parametres.getNbCours());

        // Verrouillage des cours qui doivent etre dans l'horaire obligatoirement
        if (parametres.getListeCoursObligatoires() != null && parametres.getListeCoursObligatoires().size() > 0) {
            List<Set<Cours>> invalides = new ArrayList<>();
            for (Set<Cours> set : subsets) {
                if (!set.containsAll(parametres.getListeCoursObligatoires())) {
                    invalides.add(set);
                }
            }
            subsets.removeAll(invalides);
        }

        this.populateValidationStrategies(parametres);

        for (Set<Cours> sub : subsets) {
            recurCreateCombinaisons(new ArrayList<>(sub), 0, node);
        }

        return node.getValidCombinaisons(parametres.getNbCours());
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
            } else {
                // tentative de créer des combinaisons d'horaire avec les sous-groupes (aka les activités Labo A, Labo B, etc.)
                List<NodeGroupe> nodes = node.createNodesFromSubGroupes(g);
                nodes.forEach((n) -> recurCreateCombinaisons(cours, index + 1, n));

            }
        }

    }

}
