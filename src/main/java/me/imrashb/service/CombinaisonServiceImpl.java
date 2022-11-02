package me.imrashb.service;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Trimestre;
import me.imrashb.exception.CoursNotInitializedException;
import me.imrashb.parser.CoursParser;
import me.imrashb.parser.GenerateurHoraire;
import me.imrashb.utils.ETSUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@EnableScheduling
@Slf4j
public class CombinaisonServiceImpl implements CombinaisonService{

    private List<Cours> cours;

    private boolean currentlyLoadingHoraires = false;

    @Override
    public List<CombinaisonHoraire> getCombinaisonsHoraire(String... cours) {
        if(this.cours == null) {
            throw new CoursNotInitializedException();
        }
        return new GenerateurHoraire(this.cours).getCombinaisonsHoraire(cours);
    }

    //Update les horaires a chaque heure
    @Scheduled(fixedDelay = 3600000)
    public void updateCours() throws IOException {
        log.info("method: updateCours() : Début de la mise à jour des cours");

        CoursParser coursParser = new CoursParser();

        List<File> files = null;
        try {
            files = ETSUtils.getFichiersHoraireSync(2022, Trimestre.AUTOMNE);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for(File f : files) {
            coursParser.getCoursFromPDF(f);
            f.delete();
        }

        this.cours = coursParser.getCours();

        log.info("method: updateCours() : Fin de la mise à jour des cours");
    }
}
