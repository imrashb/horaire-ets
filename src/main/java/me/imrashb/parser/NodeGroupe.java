package me.imrashb.parser;

import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Groupe;
import me.imrashb.domain.Jour;

import java.util.*;

public class NodeGroupe {

    private List<Groupe> groupes;
    private List<NodeGroupe> nodes = new ArrayList<>();

    private Set<Jour> conges;
    public NodeGroupe(Groupe groupe, List<Groupe> groupsPrecedents, Set<Jour> conges) {
        if(groupsPrecedents == null) this.groupes = new ArrayList();
        else this.groupes = new ArrayList(groupsPrecedents);
        if(groupe != null)
            this.groupes.add(groupe);
        this.conges = conges;
        if(conges == null) this.conges = new HashSet<>();
    }

    public NodeGroupe createNode(Groupe groupe) {
        NodeGroupe node = new NodeGroupe(groupe, groupes, conges);
        nodes.add(node);
        return node;
    }

    public boolean isOverlapping(Groupe groupe) {
        if(groupes.contains(groupe)) return true;
        for(Groupe g : groupes) {
            if(g.overlapsWith(groupe)) return true;
        }
        return false;
    }

    public boolean isDuringConges(Groupe g) {
        return g.isDuring(conges);
    }

    public List<CombinaisonHoraire> getValidCombinaisons(List<Cours> cours, int nbCours) {

        List<CombinaisonHoraire> liste = new ArrayList<>();

        if(nodes.size() == 0) {

            //Verifie si on a tous les cours voulus
            if(groupes.size() == nbCours) {
                liste.add(new CombinaisonHoraire(groupes));
                return liste;
            }
            // Retourne liste vide si pas bonne branche de l'arbre
            return liste;

        } else {

            for(NodeGroupe node : nodes) {
                List<CombinaisonHoraire> tmp = node.getValidCombinaisons(cours, nbCours);
                liste.addAll(tmp);
            }

            return liste;
        }

    }

}
