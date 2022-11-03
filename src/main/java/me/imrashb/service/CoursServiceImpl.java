package me.imrashb.service;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.Cours;
import me.imrashb.domain.CoursManager;
import me.imrashb.exception.CoursNotInitializedException;
import me.imrashb.exception.TrimestreDoesntExistException;
import me.imrashb.parser.GenerateurHoraire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@EnableScheduling
@Slf4j
public class CoursServiceImpl implements CoursService{

    @Autowired
    private CoursManager coursManager;


    @Override
    public String[] getListeCours(String trimestre) {
        if(!coursManager.isReady())
            throw new CoursNotInitializedException();

        List<Cours> liste = coursManager.getListeCours(trimestre);

        String[] arr = new String[liste.size()];

        for(int i = 0; i<liste.size(); i++) {
            arr[i] = liste.get(i).getSigle();
        }
        return arr;
    }
}
