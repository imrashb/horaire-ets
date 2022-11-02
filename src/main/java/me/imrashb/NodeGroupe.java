package me.imrashb;

import java.util.*;

public class NodeGroupe {

    private List<Groupe> groupes;
    private List<NodeGroupe> nodes = new ArrayList<>();
    public NodeGroupe(Groupe groupe, List<Groupe> groupsPrecedents) {
        if(groupsPrecedents == null) this.groupes = new ArrayList();
        else this.groupes = new ArrayList(groupsPrecedents);
        if(groupe != null)
            this.groupes.add(groupe);
    }

    public NodeGroupe createNode(Groupe groupe) {
        NodeGroupe node = new NodeGroupe(groupe, groupes);
        nodes.add(node);
        return node;
    }

    public boolean isOverlapping(Groupe groupe) {
        for(Groupe g : groupes) {
            if(g.overlapsWith(groupe)) return true;
        }
        return false;
    }

    public List<List<Groupe>> getValidCombinaisons(List<Cours> cours) {

        List<List<Groupe>> liste = new ArrayList<>();

        if(nodes.size() == 0) {

            //Verifie si on a tous les cours voulus
            if(groupes.size() == cours.size()) {
                // Retourne liste vide si les cours sont pas valides
                for(Groupe g : groupes) {
                    if(!cours.contains(g.getCours())) return liste;
                }

                liste.add(groupes);
                return liste;
            }
            // Retourne liste vide si pas bonne branche de l'arbre
            return liste;

        } else {

            for(NodeGroupe node : nodes) {
                List<List<Groupe>> tmp = node.getValidCombinaisons(cours);
                liste.addAll(tmp);
            }

            return liste;
        }

    }

}
