package me.imrashb.parser;

import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.Groupe;
import me.imrashb.parser.strategy.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class NodeGroupe {

    private List<Groupe> groupes;
    private List<NodeGroupe> nodes = new ArrayList<>();
    private Set<HoraireValidationStrategy> validationStrategies;
    public NodeGroupe(Groupe groupe, List<Groupe> groupsPrecedents, @NotNull Set<HoraireValidationStrategy> validationStrategies) {
        if(groupsPrecedents == null) this.groupes = new ArrayList();
        else this.groupes = new ArrayList(groupsPrecedents);
        if(groupe != null)
            this.groupes.add(groupe);

        this.validationStrategies = validationStrategies;
    }

    public NodeGroupe createNode(Groupe groupe) {
        NodeGroupe node = new NodeGroupe(groupe, groupes, validationStrategies);
        nodes.add(node);
        return node;
    }

    public boolean isValid(Groupe groupe) {
        for(HoraireValidationStrategy strategy : validationStrategies) {
            if(!strategy.isValid(groupes, groupe)) return false;
        }
        return true;
    }

    public List<CombinaisonHoraire> getValidCombinaisons(int nbCours) {

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
                List<CombinaisonHoraire> tmp = node.getValidCombinaisons(nbCours);
                liste.addAll(tmp);
            }

            return liste;
        }

    }

}
