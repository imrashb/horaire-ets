package me.imrashb.service;

import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
@Getter
public class HorairETSService {

    private final CombinaisonService combinaisonService;
    private final SessionService sessionService;
    private final PreferencesUtilisateurService preferencesService;

    public HorairETSService(CombinaisonService combinaisonService, SessionService sessionService, PreferencesUtilisateurService preferencesService) {
        this.combinaisonService = combinaisonService;
        this.sessionService = sessionService;
        this.preferencesService = preferencesService;
    }

}

