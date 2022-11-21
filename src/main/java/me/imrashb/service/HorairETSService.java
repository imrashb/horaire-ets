package me.imrashb.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
@Getter
public class HorairETSService {

    @Autowired
    private CoursService coursService;

    @Autowired
    private PreferencesUtilisateurService preferencesService;

    @Autowired
    private CombinaisonService combinaisonService;

}

