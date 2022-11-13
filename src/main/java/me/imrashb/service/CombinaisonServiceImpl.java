package me.imrashb.service;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.Cours;
import me.imrashb.domain.CoursManager;
import me.imrashb.exception.CoursNotInitializedException;
import me.imrashb.exception.TrimestreDoesntExistException;
import me.imrashb.parser.GenerateurHoraire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
@Slf4j
public class CombinaisonServiceImpl implements CombinaisonService{

    @Autowired
    private CoursManager coursManager;

    @Override
    public List<CombinaisonHoraire> getCombinaisonsHoraire(String trimestre, int nbCours, String... cours) {

        if(!coursManager.isReady()) {
            throw new CoursNotInitializedException();
        }

        List<Cours> coursDuTrimestre = coursManager.getListeCours(trimestre);

        if(coursDuTrimestre == null)
            throw new TrimestreDoesntExistException(trimestre);
        return new GenerateurHoraire(coursDuTrimestre).getCombinaisonsHoraire(nbCours, cours);
    }

}
