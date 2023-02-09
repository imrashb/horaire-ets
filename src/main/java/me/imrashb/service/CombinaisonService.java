package me.imrashb.service;

import me.imrashb.domain.*;
import me.imrashb.domain.combinaison.CombinaisonHoraire;
import me.imrashb.domain.combinaison.comparator.*;

import java.util.*;

public interface CombinaisonService {

    List<CombinaisonHoraire> getCombinaisonsHoraire(ParametresCombinaison parametres);

    CombinaisonHoraire getCombinaisonFromEncodedId(String encodedId);

    CombinaisonHoraireComparator.Comparator[] getAvailableCombinaisonHoraireComparators();

}
