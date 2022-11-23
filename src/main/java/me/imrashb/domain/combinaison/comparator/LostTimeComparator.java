package me.imrashb.domain.combinaison.comparator;

import me.imrashb.domain.Activite;
import me.imrashb.domain.Groupe;
import me.imrashb.domain.Jour;
import me.imrashb.domain.combinaison.CombinaisonHoraire;

import java.util.HashMap;
import java.util.Map;

public class LostTimeComparator extends CombinaisonHoraireComparator {
    private Map<CombinaisonHoraire, Integer> memoization = new HashMap<>();

    public LostTimeComparator(CombinaisonHoraireComparator comparator) {
        super(comparator);
    }

    @Override
    public int compareCombinaisons(CombinaisonHoraire o1, CombinaisonHoraire o2) {
        Integer t1 = memoization.get(o1);
        Integer t2 = memoization.get(o2);

        t1 = (t1 == null) ? memoizeLostTime(o1) : t1;
        t2 = (t2 == null) ? memoizeLostTime(o2) : t2;

        return t1 - t2;
    }

    private int memoizeLostTime(CombinaisonHoraire comb) {

        Map<Jour, LostTimePerDay> tempsParJour = new HashMap<>();

        for (Groupe g : comb.getGroupes()) {
            for (Activite activite : g.getActivites()) {
                LostTimePerDay j = tempsParJour.get(activite.getHoraire().getJour());
                if (j == null) {
                    j = new LostTimePerDay();
                    tempsParJour.put(activite.getHoraire().getJour(), j);
                }
                j.setMin(activite.getHoraire().getHeureDepart());
                j.setMax(activite.getHoraire().getHeureFin());
                j.incrementTotal(activite.getHoraire().getHeureFin() - activite.getHoraire().getHeureDepart());
            }
        }

        int total = 0;
        for (LostTimePerDay j : tempsParJour.values()) {
            total += j.getTempsPerdu();
        }

        memoization.put(comb, total);
        return total;
    }

    private class LostTimePerDay {

        private int min = Integer.MAX_VALUE;
        private int max = Integer.MIN_VALUE;
        private int total = 0;

        public void setMin(int min) {
            this.min = Math.min(this.min, min);
        }

        public void setMax(int max) {
            this.max = Math.max(this.max, max);
        }

        public void incrementTotal(int increment) {
            this.total += increment;
        }

        public int getTempsPerdu() {
            return (max - min) - total;
        }

    }

}
