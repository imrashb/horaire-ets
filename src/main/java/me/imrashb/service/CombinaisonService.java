package me.imrashb.service;

import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.Jour;

import java.util.List;

public interface CombinaisonService {

    List<CombinaisonHoraire> getCombinaisonsHoraire(String[] cours, Jour[] conges, String sessionId, int nbCours);

    CombinaisonHoraire getCombinaisonFromEncodedId(String encodedId);

}
