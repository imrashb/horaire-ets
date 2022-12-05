package me.imrashb.domain.combinaison.comparator;

import lombok.*;
import me.imrashb.domain.combinaison.CombinaisonHoraire;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

public abstract class CombinaisonHoraireComparator implements Comparator<CombinaisonHoraire> {

    private final String description;
    private final CombinaisonHoraireComparator comparator;
    private final String id;

    public CombinaisonHoraireComparator(String id, String description, CombinaisonHoraireComparator comparator) {
        this.id = id;
        this.description = description;
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

    public String getId() {
        if (this.comparator == null) return this.id;
        return this.comparator.getId() + "," + this.id;
    }

    public String getDescription() {
        if (this.comparator == null) return "Filtrer par " + this.description;
        return this.comparator.getDescription() + ", par " + this.description;
    }

    public static class Builder {

        private CombinaisonHoraireComparator comparator = null;

        public Builder addComparator(Class<? extends CombinaisonHoraireComparator> comparatorClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            comparator = comparatorClass.getConstructor(CombinaisonHoraireComparator.class).newInstance(comparator);
            return this;
        }

        public CombinaisonHoraireComparator build() {
            return comparator;
        }

    }

    public enum Comparator {

        TEMPS_PERDU(LostTimeComparator.class),
        CONGES(CongesComparator.class);
        @Getter
        private Class<? extends CombinaisonHoraireComparator> comparatorClass;
        Comparator(Class<? extends CombinaisonHoraireComparator> comparatorClass) {
            this.comparatorClass = comparatorClass;
        }

    }

}
