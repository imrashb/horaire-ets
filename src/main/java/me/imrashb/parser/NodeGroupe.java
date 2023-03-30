package me.imrashb.parser;

import lombok.Getter;
import me.imrashb.domain.Groupe;
import me.imrashb.domain.combinaison.CombinaisonHoraire;
import me.imrashb.parser.strategy.HoraireValidationStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NodeGroupe {

    @Getter
    private final NodeGroupe previous;
    private final List<Groupe> groupes;
    private final List<NodeGroupe> nodes = new ArrayList<>();
    private final Set<HoraireValidationStrategy> validationStrategies;

    public NodeGroupe(NodeGroupe previous, Groupe groupe, List<Groupe> groupsPrecedents, @NotNull Set<HoraireValidationStrategy> validationStrategies) {
        this.previous = previous;
        if (groupsPrecedents == null) this.groupes = new ArrayList<>();
        else this.groupes = new ArrayList<>(groupsPrecedents);
        if (groupe != null)
            this.groupes.add(groupe);

        this.validationStrategies = validationStrategies;
    }

    public NodeGroupe createNode(Groupe groupe) {
        return createNode(this.groupes, groupe);
    }

    private NodeGroupe createNode(List<Groupe> groupes, Groupe groupe) {
        NodeGroupe node = new NodeGroupe(this, groupe, groupes, validationStrategies);
        nodes.add(node);
        return node;
    }

    public List<NodeGroupe> createNodesFromSubGroupes(Groupe groupe) {

        List<NodeGroupe> nodes = new ArrayList<>();
        int index = this.groupes.size();

        List<Groupe> tmp = new ArrayList<>(this.groupes);
        List<Groupe> subGroupes = groupe.createSubGroupes();
        subGroupes.add(0, groupe);

        while (index >= 0) {
            for (Groupe sub : subGroupes) {
                if (index != this.groupes.size()) {

                    Groupe original = this.groupes.get(index);
                    // Modifie les groupes dans le tableau avec leur sous-groupe, teste s'il y a un horaire dispo
                    for (Groupe replacement : original.createSubGroupes()) {
                        tmp.remove(index);
                        tmp.add(index, replacement);

                        if (this.isValid(tmp, sub)) {
                            NodeGroupe node = createNode(tmp, sub);
                            nodes.add(node);
                        }
                    }
                } else {
                    // Valide si le sous groupe fonctionne avec la liste de cours déja existante
                    if (this.isValid(this.groupes, sub)) {
                        NodeGroupe node = createNode(this.groupes, sub);
                        nodes.add(node);
                    }
                }

            }
            index--;
        }
        return nodes;
    }

    public boolean isValid(Groupe groupe) {
        return this.isValid(groupes, groupe);
    }

    private boolean isValid(List<Groupe> groupes, Groupe groupe) {
        for (HoraireValidationStrategy strategy : validationStrategies) {
            if (!strategy.isValid(groupes, groupe)) return false;
        }
        return true;
    }

    public List<CombinaisonHoraire> getValidCombinaisons(int nbCours) {

        List<CombinaisonHoraire> liste = new ArrayList<>();

        if (nodes.size() == 0) {

            //Verifie si on a tous les cours voulus
            if (groupes.size() == nbCours) {
                liste.add(new CombinaisonHoraire(groupes));
                return liste;
            }
            // Retourne liste vide si pas bonne branche de l'arbre

        } else {

            for (NodeGroupe node : nodes) {
                List<CombinaisonHoraire> tmp = node.getValidCombinaisons(nbCours);
                liste.addAll(tmp);
            }

        }
        return liste;

    }

}
