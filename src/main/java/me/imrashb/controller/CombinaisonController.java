package me.imrashb.controller;

import me.imrashb.domain.*;
import me.imrashb.service.CombinaisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CombinaisonController {

    @Autowired
    private CombinaisonService service;

    @GetMapping("/combinaisons")
    public List<CombinaisonHoraire> getCombinaisonsHoraire(@RequestParam String session,
                                                           @RequestParam String[] cours,
                                                           @RequestParam(required = false) Jour[] conges,
                                                           @RequestParam(required = false) Integer nbCours) {
        if(nbCours == null) {
            nbCours = cours.length;
        }
        return service.getCombinaisonsHoraire(cours, conges, session, nbCours);
    }

}
