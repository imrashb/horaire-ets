package me.imrashb.task;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Session;
import me.imrashb.domain.Trimestre;
import me.imrashb.parser.CoursParser;
import me.imrashb.parser.PdfCours;
import me.imrashb.service.HorairETSService;
import me.imrashb.utils.ETSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Configuration
@EnableScheduling
@Slf4j
public class CoursServiceUpdateScheduledTask {

    private final HorairETSService horairETSService;
    @Value("${sessions}")
    private String[] sessions;

    @Autowired
    private Environment env;


    public CoursServiceUpdateScheduledTask(HorairETSService horairETSService) {
        this.horairETSService = horairETSService;
    }

    //Update les horaires a chaque heure
    @Scheduled(fixedDelay = 3600000)
    public void updateCours() throws IOException {
        log.info("method: updateCours() : Début de la mise à jour des cours");

        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            System.out.format("%s=%s%n", envName, env.get(envName));
        }


        if (sessions.length == 0)
            throw new RuntimeException("ERREUR: Sessions ne sont pas définient dans application.properties. Ex: sessions=20223,20231");


        for (String sessionId : sessions) {

            Trimestre trim = Trimestre.getTrimestreFromId(sessionId);
            int annee = Integer.parseInt(sessionId.substring(0, 4));
            assert trim != null;
            Session session = trim.getSession(annee);

            CoursParser coursParser = new CoursParser();

            List<PdfCours> files = null;
            try {
                files = ETSUtils.getFichiersHoraireSync(session);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (PdfCours pdf : files) {
                coursParser.getCoursFromPDF(pdf.getPdf(), pdf.getProgramme(), session);
                pdf.getPdf().delete();
            }
            coursParser.getCours().sort(Comparator.comparing(Cours::getSigle));

            this.horairETSService.getCoursService().addSession(session, coursParser.getCours());
        }
        horairETSService.getCoursService().setReady(true);

        log.info("method: updateCours() : Fin de la mise à jour des cours");
        System.gc();
    }

}
