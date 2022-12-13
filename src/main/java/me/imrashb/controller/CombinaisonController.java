package me.imrashb.controller;

import me.imrashb.domain.Jour;
import me.imrashb.domain.combinaison.CombinaisonHoraire;
import me.imrashb.domain.combinaison.comparator.*;
import me.imrashb.service.CombinaisonService;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.*;
import java.util.*;

@RestController
@RequestMapping("/combinaisons")
public class CombinaisonController {

    private final CombinaisonService service;

    public CombinaisonController(CombinaisonService service) {
        this.service = service;
    }

    @GetMapping("")
    public List<CombinaisonHoraire> getCombinaisonsHoraire(@RequestParam String session,
                                                           @RequestParam String[] cours,
                                                           @RequestParam(required = false) Jour[] conges,
                                                           @RequestParam(required = false) Integer nbCours,
                                                           @RequestParam(required = false) LinkedHashSet<CombinaisonHoraireComparator.Comparator> sort) throws RuntimeException {
        if (nbCours == null) {
            nbCours = cours.length;
        }

        CombinaisonHoraireComparator comparator = null;

        if(sort != null && sort.size() > 0) {
            CombinaisonHoraireComparator.Builder builder = new CombinaisonHoraireComparator.Builder();
            for(CombinaisonHoraireComparator.Comparator c : sort) {
                try {
                    builder.addComparator(c.getComparatorClass());
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            comparator = builder.build();
        }

        List<CombinaisonHoraire> combinaisons = service.getCombinaisonsHoraire(cours, conges, session, nbCours);

        if(comparator != null) Collections.sort(combinaisons, comparator);

        return combinaisons;
    }

    @GetMapping("sort")
    public CombinaisonHoraireComparator.Comparator[] getCombinaisonHoraireSorters() {
        return service.getAvailableCombinaisonHoraireComparators();
    }

}
