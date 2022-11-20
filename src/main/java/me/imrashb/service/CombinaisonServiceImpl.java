package me.imrashb.service;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.*;
import me.imrashb.exception.CoursNotInitializedException;
import me.imrashb.exception.SessionDoesntExistException;
import me.imrashb.parser.GenerateurHoraire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@EnableScheduling
@Slf4j
@Scope("singleton")
public class CombinaisonServiceImpl implements CombinaisonService{

    @Autowired
    private CoursService coursService;

    @Override
    public List<CombinaisonHoraire> getCombinaisonsHoraire(String[] cours, Jour[] conges, String sessionId, int nbCours) {

        if(!coursService.isReady()) {
            throw new CoursNotInitializedException();
        }

        List<Cours> coursSession = coursService.getListeCours(sessionId);

        if(coursSession == null)
            throw new SessionDoesntExistException(sessionId);

        if(conges == null) {
            return new GenerateurHoraire(coursSession).getCombinaisonsHoraire(cours, nbCours);
        } else {
            return new GenerateurHoraire(coursSession).getCombinaisonsHoraire(cours, new HashSet<>(Arrays.asList(conges)), nbCours);
        }
    }

    @Override
    public CombinaisonHoraire getCombinaisonFromEncodedId(String encodedId) {
        return CombinaisonHoraireFactory.fromEncodedUniqueId(encodedId, coursService);
    }

}
