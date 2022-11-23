package me.imrashb.parser.strategy;

import lombok.AllArgsConstructor;
import me.imrashb.domain.Activite;
import me.imrashb.domain.Groupe;
import me.imrashb.domain.Jour;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class CongeStrategy implements HoraireValidationStrategy {

    private Set<Jour> conges;

    @Override
    public boolean isValid(List<Groupe> current, Groupe groupe) {
        for (Activite a : groupe.getActivites()) {
            if (conges.contains(a.getHoraire().getJour())) return false;
        }
        return true;
    }
}
