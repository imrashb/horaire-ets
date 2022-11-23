package me.imrashb.domain.combinaison.comparator;

import me.imrashb.domain.combinaison.CombinaisonHoraire;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

public abstract class CombinaisonHoraireComparator implements Comparator<CombinaisonHoraire> {

    private CombinaisonHoraireComparator comparator;

    public CombinaisonHoraireComparator(CombinaisonHoraireComparator comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(CombinaisonHoraire o1, CombinaisonHoraire o2) {

        if (comparator != null) {
            int result = comparator.compareCombinaisons(o1, o2);
            if (result != 0) return result;
        }
        return this.compareCombinaisons(o1, o2);
    }

    public abstract int compareCombinaisons(CombinaisonHoraire o1, CombinaisonHoraire o2);

    public static class Builder {

        private CombinaisonHoraireComparator comparator = null;

        public Builder addComparator(Class<? extends CombinaisonHoraireComparator> comparatorClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            comparator = comparatorClass.getConstructor(CombinaisonHoraireComparator.class).newInstance(comparator);
            return this;
        }

        public CombinaisonHoraireComparator build() {
            System.out.println(comparator);
            return comparator;
        }

    }

}
