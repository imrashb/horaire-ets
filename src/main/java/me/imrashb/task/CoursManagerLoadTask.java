package me.imrashb.task;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.Cours;
import me.imrashb.domain.CoursManager;
import me.imrashb.domain.Session;
import me.imrashb.domain.Trimestre;
import me.imrashb.parser.CoursParser;
import me.imrashb.parser.PdfCours;
import me.imrashb.utils.ETSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Configuration
@EnableScheduling
@Slf4j
public class CoursManagerLoadTask {

    @Value("${sessions}")
    private String[] sessions;

    @Autowired
    private CoursManager coursManager;

    //Update les horaires a chaque heure
    @Scheduled(fixedDelay = 3600000)
    public void updateCours() throws IOException {
        log.info("method: updateCours() : Début de la mise à jour des cours");

        if(sessions.length == 0)
            throw new RuntimeException("ERREUR: Sessions ne sont pas définient dans application.properties. Ex: sessions=20223,20231");


        for(String sessionId : sessions) {

            Trimestre trim = Trimestre.getTrimestreFromId(sessionId);
            int annee = Integer.parseInt(sessionId.substring(0, 4));
            Session session = trim.getSession(annee);
            if(trim == null)
                throw new RuntimeException("ERREUR: La session '"+sessionId+"' est invalide.");

            CoursParser coursParser = new CoursParser();

            List<PdfCours> files = null;
            try {
                files = ETSUtils.getFichiersHoraireSync(session);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for(PdfCours pdf : files) {
                coursParser.getCoursFromPDF(pdf.getPdf(), pdf.getProgramme(), session);
                pdf.getPdf().delete();
            }
            coursParser.getCours().sort(new Comparator<Cours>() {
                @Override
                public int compare(Cours o1, Cours o2) {
                    return o1.getSigle().compareTo(o2.getSigle());
                }
            });

            this.coursManager.addSession(session, coursParser.getCours());
        }
        coursManager.setReady(true);

        log.info("method: updateCours() : Fin de la mise à jour des cours");
    }

}
