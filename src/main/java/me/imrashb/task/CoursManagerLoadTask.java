package me.imrashb.task;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.CoursManager;
import me.imrashb.domain.Trimestre;
import me.imrashb.parser.CoursParser;
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
            if(trim == null)
                throw new RuntimeException("ERREUR: Le trimestre "+trimestre+" est invalide.");

            CoursParser coursParser = new CoursParser();

            List<File> files = null;
            try {
                files = ETSUtils.getFichiersHoraireSync(Integer.parseInt(trimestre.substring(0, 4)), trim);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for(File f : files) {
                coursParser.getCoursFromPDF(f);
                f.delete();
            }

            this.coursManager.addTrimestre(trimestre, coursParser.getCours());
        }

        log.info("method: updateCours() : Fin de la mise à jour des cours");
    }

}
