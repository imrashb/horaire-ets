package me.imrashb.parser.strategy;

import lombok.*;
import me.imrashb.domain.*;

import java.util.*;

@AllArgsConstructor
public class CongeStrategy implements HoraireValidationStrategy {

    private Set<Jour> conges;

    @Override
    public boolean isValid(List<Groupe> current, Groupe groupe) {
        for(Activite a : groupe.getActivites()) {
            if(conges.contains(a.getHoraire().getJour())) return false;
        }
        return true;
    }
}
