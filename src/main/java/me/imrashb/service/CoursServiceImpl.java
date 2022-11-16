package me.imrashb.service;

import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.Cours;
import me.imrashb.domain.CoursManager;
import me.imrashb.exception.CoursNotInitializedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
@Slf4j
public class CoursServiceImpl implements CoursService{

    @Autowired
    private CoursManager coursManager;


    @Override
    public List<Cours> getListeCours(String session) {
        if(!coursManager.isReady())
            throw new CoursNotInitializedException();

        List<Cours> liste = coursManager.getListeCours(session);
        return liste;
    }
}
