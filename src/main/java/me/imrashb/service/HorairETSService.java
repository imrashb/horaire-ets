package me.imrashb.service;
import lombok.*;
import me.imrashb.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.*;

import java.util.*;

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

