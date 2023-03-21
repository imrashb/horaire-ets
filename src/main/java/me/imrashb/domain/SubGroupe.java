package me.imrashb.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.*;

public class SubGroupe extends Groupe {

    private static final List<Groupe> EMPTY_SUBGROUP_LIST = new ArrayList<>();

    @Getter
    @JsonIgnore
    private Groupe groupePrincipal;

    public SubGroupe(String numeroGroupe, List<Activite> activites, Groupe groupe) {
        super(numeroGroupe, activites, groupe.getCours());
        this.groupePrincipal = groupe;
    }

    @Override
    public List<Groupe> createSubGroupes() {
        return EMPTY_SUBGROUP_LIST;
    }
}
