package me.imrashb.service;

import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.parser.CoursParser;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
public class CombinaisonServiceImpl implements CombinaisonService{

    private CoursParser coursParser;

    @Override
    public List<CombinaisonHoraire> getCombinaisonsHoraire(String... cours) {
        return null;
    }

    @Scheduled(fixedDelay = 1000)
    public void perform() {
        System.out.println("HERE");
    }
}
