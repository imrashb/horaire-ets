package me.imrashb.domain;


import lombok.Getter;

import java.util.List;

public class SubGroupe extends Groupe {

    @Getter
    private Groupe groupePrincipal;

    public SubGroupe(String numeroGroupe, List<Activite> activites, Groupe groupe) {
        super(numeroGroupe, activites, groupe.getCours());
        this.groupePrincipal = groupe;
    }

    @Override
    public List<SubGroupe> getSubGroupes() {
        throw new RuntimeException("Invalid operation: Cannot call getSubGroupes() on a SubGroupe object.");
    }
}
