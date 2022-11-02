package me.imrashb.controller;

import me.imrashb.domain.CombinaisonHoraire;
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
    public List<CombinaisonHoraire> getCombinaisonsHoraire(@RequestParam String... cours) {
        return service.getCombinaisonsHoraire(cours);
    }

}
