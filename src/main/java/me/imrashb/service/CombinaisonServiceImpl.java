package me.imrashb.service;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Jour;
import me.imrashb.domain.combinaison.CombinaisonHoraire;
import me.imrashb.exception.CoursNotInitializedException;
import me.imrashb.exception.SessionDoesntExistException;
import me.imrashb.parser.GenerateurHoraire;
import me.imrashb.parser.strategy.CongeStrategy;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
@EnableScheduling
@Slf4j
@Scope("singleton")
public class CombinaisonServiceImpl implements CombinaisonService {

    private final CoursService coursService;

    public CombinaisonServiceImpl(CoursService coursService) {
        this.coursService = coursService;
    }

    @Override
    public List<CombinaisonHoraire> getCombinaisonsHoraire(String[] cours, Jour[] conges, String sessionId, int nbCours) {

        if (!coursService.isReady()) {
            throw new CoursNotInitializedException();
        }

        List<Cours> coursSession = coursService.getListeCours(sessionId);

        if (coursSession == null)
            throw new SessionDoesntExistException(sessionId);

        if (conges == null) {
            return new GenerateurHoraire(coursSession)
                    .getCombinaisonsHoraire(cours, nbCours);
        } else {
            return new GenerateurHoraire(coursSession)
                    .addValidationStrategy(new CongeStrategy(new HashSet<>(Arrays.asList(conges))))
                    .getCombinaisonsHoraire(cours, nbCours);
        }
    }

    @Override
    public CombinaisonHoraire getCombinaisonFromEncodedId(String encodedId) {
        return CombinaisonHoraireFactory.fromEncodedUniqueId(encodedId, coursService);
    }

}
