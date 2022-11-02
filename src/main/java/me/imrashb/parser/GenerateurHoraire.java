package me.imrashb.parser;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Groupe;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class GenerateurHoraire {

    private List<Cours> listeCours;

    public List<CombinaisonHoraire> getCombinaisonsHoraire(List<Cours> cours) {
        NodeGroupe node = new NodeGroupe(null, null);
        recurCreateCombinaisons(cours, 0, node);
        return node.getValidCombinaisons(cours);
    }

    public List<CombinaisonHoraire> getCombinaisonsHoraire(String... cours) {
        List<Cours> coursVoulu = new ArrayList<>();

        for (Cours c : listeCours) {
            for (String s : cours) {
                if (c.getSigle().equalsIgnoreCase(s)) coursVoulu.add(c);
            }
        }

        return getCombinaisonsHoraire(coursVoulu);
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
