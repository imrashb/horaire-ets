package me.imrashb.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.List;

public class SubGroupe extends Groupe {

    @Getter
    @JsonIgnore
    private Groupe groupePrincipal;

    public SubGroupe(String numeroGroupe, List<Activite> activites, Groupe groupe) {
        super(numeroGroupe, activites, groupe.getCours());
        this.groupePrincipal = groupe;
    }

    @Override
    public List<Groupe> createSubGroupes() {
        throw new RuntimeException("Invalid operation: Cannot call getSubGroupes() on a SubGroupe object.");
    }
}
