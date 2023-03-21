package me.imrashb.task;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Session;
import me.imrashb.domain.Trimestre;
import me.imrashb.parser.CoursParser;
import me.imrashb.parser.PdfCours;
import me.imrashb.service.HorairETSService;
import me.imrashb.utils.ETSUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Configuration
@EnableScheduling
@Slf4j
public class SessionServiceUpdateScheduledTask {

    private final HorairETSService horairETSService;

    public SessionServiceUpdateScheduledTask(HorairETSService horairETSService) {
        this.horairETSService = horairETSService;
    }

    private static int getNextSessionId(int sessionId) {
        if (sessionId % 10 == 3) {
            sessionId = (sessionId / 10) * 10 + 11; // Division entière, 20203 / 10 = 2020, * 1000 20200, + 11 20211
        } else {
            sessionId++;
        }
        return sessionId;
    }

    //Update les horaires a chaque heure
    @Scheduled(fixedDelay = 3600000)
    public void updateCours() throws IOException {
        log.info("method: updateCours() : Début de la mise à jour des cours");
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int startYear = 2021;

        int sessionId = startYear * 10; // 2020 * 10 -> 20200, les sessions sont: 20201 (Hiver), 20202 (Été), 20203 (Automne)


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
            this.horairETSService.getSessionService().addSession(session, coursParser.getCours());
        }
        horairETSService.getSessionService().setReady(true);

        log.info("method: updateCours() : Fin de la mise à jour des cours");
        System.gc();
    }

}
