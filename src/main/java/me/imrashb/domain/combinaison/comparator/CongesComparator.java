package me.imrashb.domain.combinaison.comparator;

import me.imrashb.domain.Jour;
import me.imrashb.domain.combinaison.CombinaisonHoraire;

import java.util.HashMap;
import java.util.Map;

public class CongesComparator extends CombinaisonHoraireComparator {
    private final int NB_JOURS_SEMAINE = Jour.values().length;

    public CongesComparator(CombinaisonHoraireComparator comparator) {
        super("nb_jours", "nombre de congés", comparator);
    }

    @Override
    public int compareCombinaisons(CombinaisonHoraire o1, CombinaisonHoraire o2) {
        Integer t1 = NB_JOURS_SEMAINE - o1.getConges().size();
        Integer t2 = NB_JOURS_SEMAINE - o2.getConges().size();

        return t1 - t2;
    }

}
