package me.imrashb.service;

import me.imrashb.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

public interface CombinaisonService {

    List<CombinaisonHoraire> getCombinaisonsHoraire(String[] cours, Jour[] conges, String sessionId, int nbCours);

    CombinaisonHoraire getCombinaisonFromEncodedId(String encodedId);

}
