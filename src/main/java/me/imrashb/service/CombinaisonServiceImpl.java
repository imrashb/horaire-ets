package me.imrashb.service;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Trimestre;
import me.imrashb.exception.CoursNotInitializedException;
import me.imrashb.exception.TrimestreDoesntExistException;
import me.imrashb.parser.CoursParser;
import me.imrashb.parser.GenerateurHoraire;
import me.imrashb.utils.ETSUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@EnableScheduling
@Slf4j
public class CombinaisonServiceImpl implements CombinaisonService{

    @Value("${trimestres}")
    private String[] trimestres;
    private HashMap<String, List<Cours>> mapCours = new HashMap<>();

    private boolean currentlyLoadingHoraires = false;

    @Override
    public List<CombinaisonHoraire> getCombinaisonsHoraire(String trimestre, String... cours) {

        List<Cours> coursDuTrimestre = mapCours.get(trimestre);

        if(this.mapCours.size() == 0) {
            throw new CoursNotInitializedException();
        }

        if(coursDuTrimestre == null)
            throw new TrimestreDoesntExistException(trimestre);
        return new GenerateurHoraire(coursDuTrimestre).getCombinaisonsHoraire(cours);
    }

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

            if(this.mapCours.get(trimestre) != null) {
                this.mapCours.replace(trimestre, coursParser.getCours());
            } else {
                this.mapCours.put(trimestre, coursParser.getCours());
            }
        }

        log.info("method: updateCours() : Fin de la mise à jour des cours");
    }
}
