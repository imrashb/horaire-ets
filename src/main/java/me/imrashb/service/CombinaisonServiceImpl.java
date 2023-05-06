package me.imrashb.service;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.*;
import me.imrashb.domain.combinaison.CombinaisonHoraire;
import me.imrashb.domain.combinaison.comparator.*;
import me.imrashb.exception.CoursNotInitializedException;
import me.imrashb.exception.SessionDoesntExistException;
import me.imrashb.parser.GenerateurHoraire;
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

    private final StatisticsService statisticsService;
    private final Set<String> comparators;

    public CombinaisonServiceImpl(SessionService sessionService, StatisticsService statisticsService) {
        this.sessionService = sessionService;
        this.statisticsService = statisticsService;
        this.comparators = new HashSet<>();
        this.initializeComparators();
    }

    private void initializeComparators() {
        this.comparators.add(new LostTimeComparator(null).getId());
        this.comparators.add(new CongesComparator(null).getId());
    }

    @Override
    public List<CombinaisonHoraire> getCombinaisonsHoraire(ParametresCombinaison parametres) {

        long startTime = System.nanoTime();

        parametres.init(sessionService);

        if (!sessionService.isReady()) {
            throw new CoursNotInitializedException();
        }

        String sessionId = parametres.getSession();

        List<Cours> coursSession = sessionService.getListeCours(sessionId);
        if (coursSession == null)
            throw new SessionDoesntExistException(sessionId);

        List<CombinaisonHoraire> combinaisons = new GenerateurHoraire(coursSession).getCombinaisonsHoraire(parametres);

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;

        this.updateGenerationStatistics(parametres, elapsedTime, combinaisons.size(), sessionId);

        return combinaisons;
    }

    private void updateGenerationStatistics(ParametresCombinaison parametres, long timeSpentGenerating, long totalCombinaisonsGenerated, String sessionId) {
        Statistics stats = this.statisticsService.getStatistics();
        stats.addGenerationsPerProgrammes(parametres.getListeCours());
        stats.addTotalCombinaisons(totalCombinaisonsGenerated);
        stats.incrementTotalGenerations();
        stats.addTimeSpentGenerating(timeSpentGenerating);
        stats.addNombreCoursAverage(parametres.getNbCours());
        stats.incrementGenerationsPerSession(sessionId);
        statisticsService.save();
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
