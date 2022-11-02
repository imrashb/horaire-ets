package me.imrashb.service;

import me.imrashb.domain.CombinaisonHoraire;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CombinaisonService {

    List<CombinaisonHoraire> getCombinaisonsHoraire(String... cours);

}
