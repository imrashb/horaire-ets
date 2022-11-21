package me.imrashb.service;

import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
@Getter
public class HorairETSService {

    private final CombinaisonService combinaisonService;
    private final CoursService coursService;
    private final PreferencesUtilisateurService preferencesService;

    public HorairETSService(CombinaisonService combinaisonService, CoursService coursService, PreferencesUtilisateurService preferencesService) {
        this.combinaisonService = combinaisonService;
        this.coursService = coursService;
        this.preferencesService = preferencesService;
    }

}

