package me.imrashb.task;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.*;
import me.imrashb.parser.CoursParser;
import me.imrashb.parser.PdfCours;
import me.imrashb.repository.ScrapedCoursDataRepository;
import me.imrashb.service.HorairETSService;
import me.imrashb.utils.ETSUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Configuration
@EnableScheduling
@Slf4j
public class SessionServiceUpdateScheduledTask {

    private final HorairETSService horairETSService;

    private final ScrapedCoursDataRepository repository;

    public SessionServiceUpdateScheduledTask(HorairETSService horairETSService, ScrapedCoursDataRepository repository) {
        this.horairETSService = horairETSService;
        this.repository = repository;
    }

    private static int getNextSessionId(int sessionId) {
        if (sessionId % 10 == 3) {
            sessionId = (sessionId / 10) * 10 + 11; // Division entière, 20203 / 10 = 2020, * 1000 20200, + 11 20211
        } else {
            sessionId++;
        }
        return sessionId;
    }

    private void scrapeMissingCoursData(Map<String, List<Cours>> map) {
        ExecutorService executor = Executors.newFixedThreadPool(16);
        List<Future<CoursDataWrapper>> futures = new ArrayList<>();
        for (Map.Entry<String, List<Cours>> entry : map.entrySet()) {
            Future<CoursDataWrapper> future = new CoursDataScraper(entry.getKey()).getCoursData(executor);
            futures.add(future);
        }
        for (Future<CoursDataWrapper> future : futures) {
            try {
                CoursDataWrapper data = future.get();
                repository.save(data);
                List<Cours> cours = map.get(data.getSigle());
                cours.forEach((c) -> c.syncFromCoursData(data));
            } catch (InterruptedException | ExecutionException e) {
                // Ignore catch block
            }

        }
        executor.shutdown();

    }

    //Update les horaires a chaque heure
    @Scheduled(fixedDelay = 3600000)
    public void updateCours() throws IOException {
        log.info("method: updateCours() : Début de la mise à jour des cours");
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int startYear = 2023;

        int sessionId = startYear * 10; // 2020 * 10 -> 20200, les sessions sont: 20201 (Hiver), 20202 (Été), 20203 (Automne)


        Map<String, List<Cours>> missingAdditionalCoursData = new HashMap<>();

        while (true) {

            sessionId = getNextSessionId(sessionId);

            log.info("method: updateCours() : Downloading Session " + sessionId);

            Trimestre trim = Trimestre.getTrimestreFromId(sessionId + "");
            int annee = sessionId / 10;
            assert trim != null;
            Session session = trim.getSession(annee);

            CoursParser coursParser = new CoursParser();

            List<PdfCours> files = null;
            try {
                files = ETSUtils.getFichiersHoraireSync(session);

                if (files == null) {
                    if (sessionId / 10 >= currentYear || sessionId / 10 - currentYear > 5) {
                        break; // Exit quand on a fail de telecharger un fichier de cette année ou plus
                    } else {
                        continue;
                    }

                }

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (PdfCours pdf : files) {
                coursParser.getCoursFromPDF(pdf.getPdf(), pdf.getProgramme(), session);
                pdf.getPdf().delete();

            }
            coursParser.getCours().sort(Comparator.comparing(Cours::getSigle));

            // Handle additional cours data
            for (Cours c : coursParser.getCours()) {

                Optional<CoursDataWrapper> data = repository.findById(c.getSigle());

                // If no data add to missing to scrape after
                if (data.isEmpty()) {
                    List<Cours> coursList = missingAdditionalCoursData.get(c.getSigle());
                    if (coursList == null) {
                        coursList = new ArrayList<>();
                        missingAdditionalCoursData.put(c.getSigle(), coursList);
                    }
                    coursList.add(c);
                } else {
                    c.syncFromCoursData(data.get());
                }

            }

            List<Programme> programmes = new ArrayList<>();
            files.forEach((f) -> programmes.add(f.getProgramme()));
            this.horairETSService.getSessionService().addSession(session, coursParser.getCours(), programmes);
        }
        horairETSService.getSessionService().setReady(true);
        scrapeMissingCoursData(missingAdditionalCoursData);
        log.info("method: updateCours() : Fin de la tâche de la mise à jour des cours.");
        System.gc();
    }

}
