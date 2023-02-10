package me.imrashb.service;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.*;
import me.imrashb.domain.combinaison.CombinaisonHoraire;
import me.imrashb.domain.combinaison.comparator.*;
import me.imrashb.exception.CoursNotInitializedException;
import me.imrashb.exception.SessionDoesntExistException;
import me.imrashb.parser.GenerateurHoraire;
import me.imrashb.parser.strategy.CongeStrategy;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@EnableScheduling
@Slf4j
@Scope("singleton")
public class CombinaisonServiceImpl implements CombinaisonService {

    private final SessionService sessionService;
    private final Set<String> comparators;

    public CombinaisonServiceImpl(SessionService sessionService) {
        this.sessionService = sessionService;
        this.comparators = new HashSet<>();
        this.initializeComparators();
    }

    private void initializeComparators() {
        this.comparators.add(new LostTimeComparator(null).getId());
        this.comparators.add(new CongesComparator(null).getId());
    }

    @Override
    public List<CombinaisonHoraire> getCombinaisonsHoraire(ParametresCombinaison parametres) {

        parametres.init(sessionService);

        if (!sessionService.isReady()) {
            throw new CoursNotInitializedException();
        }

        String sessionId = parametres.getSession();

        List<Cours> coursSession = sessionService.getListeCours(sessionId);

        if (coursSession == null)
            throw new SessionDoesntExistException(sessionId);


        return new GenerateurHoraire(coursSession).getCombinaisonsHoraire(parametres);
    }

    @Override
    public CombinaisonHoraire getCombinaisonFromEncodedId(String encodedId) {
        return CombinaisonHoraireFactory.fromEncodedUniqueId(encodedId, sessionService);
    }

    @Override
    public CombinaisonHoraireComparator.Comparator[] getAvailableCombinaisonHoraireComparators() {
        return CombinaisonHoraireComparator.Comparator.values();
    }

}
