package me.imrashb.task;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.CoursManager;
import me.imrashb.domain.Trimestre;
import me.imrashb.parser.CoursParser;
import me.imrashb.parser.PdfCours;
import me.imrashb.utils.ETSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Configuration
@EnableScheduling
@Slf4j
public class CoursManagerLoadTask {

    @Value("${trimestres}")
    private String[] trimestres;

    @Autowired
    private CoursManager coursManager;

    //Update les horaires a chaque heure
    @Scheduled(fixedDelay = 3600000)
    public void updateCours() throws IOException {
        log.info("method: updateCours() : Début de la mise à jour des cours");

        if(trimestres.length == 0)
            throw new RuntimeException("ERREUR: Trimestres ne sont pas définient dans application.properties. Ex: trimestres=20223,20231");


        for(String trimestre : trimestres) {

            Trimestre trim = Trimestre.getTrimestreFromId(trimestre);
            int annee = Integer.parseInt(trimestre.substring(0, 4));
            if(trim == null)
                throw new RuntimeException("ERREUR: Le trimestre "+trimestre+" est invalide.");

            CoursParser coursParser = new CoursParser();

            List<PdfCours> files = null;
            try {
                files = ETSUtils.getFichiersHoraireSync(annee, trim);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for(PdfCours pdf : files) {
                coursParser.getCoursFromPDF(pdf.getPdf(), pdf.getProgramme());
                pdf.getPdf().delete();
            }

            this.coursManager.addTrimestre(trimestre, coursParser.getCours());
        }
        coursManager.setReady(true);

        log.info("method: updateCours() : Fin de la mise à jour des cours");
    }

}
