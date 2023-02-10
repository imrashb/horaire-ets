package me.imrashb.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import me.imrashb.domain.combinaison.comparator.*;
import me.imrashb.exception.*;
import me.imrashb.service.*;
import org.springframework.lang.*;

import java.util.*;

@Data
public class ParametresCombinaison {

    private String session;
    private String[] cours;
    private String[] coursObligatoires;
    @Nullable private Jour[] conges;
    @Nullable private Integer nbCours;
    @Nullable private LinkedHashSet<CombinaisonHoraireComparator.Comparator> sort;

    @JsonIgnore
    private List<Cours> listeCours;
    @JsonIgnore
    private List<Cours> listeCoursObligatoires;

    @Builder
    public ParametresCombinaison(String session, @Nullable Jour[] conges, @Nullable Integer nbCours, @Nullable LinkedHashSet<CombinaisonHoraireComparator.Comparator> sort, List<Cours> listeCours, List<Cours> listeCoursObligatoires) {
        this.session = session;
        this.conges = conges;
        this.nbCours = nbCours;
        this.sort = sort;
        this.listeCours = listeCours;
        this.listeCoursObligatoires = listeCoursObligatoires;
    }

    public void init(SessionService service) {
        if(nbCours == null)
            nbCours = cours.length;

        if(listeCours == null)
            listeCours = new ArrayList<>(service.getCoursFromSigles(session, cours));

        if(listeCoursObligatoires == null && coursObligatoires != null) {
            listeCoursObligatoires = new ArrayList<>(service.getCoursFromSigles(session, coursObligatoires));

            if(!listeCours.containsAll(listeCoursObligatoires) && listeCours.size() >= listeCoursObligatoires.size())
                throw new InvalidCoursObligatoiresException();

        }

        if(nbCours > listeCours.size())
            throw new InvalidCoursAmountException(nbCours);
    }


}
