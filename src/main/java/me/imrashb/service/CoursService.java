package me.imrashb.service;

import me.imrashb.domain.CombinaisonHoraire;
import me.imrashb.domain.Cours;

import java.util.List;

public interface CoursService {

    List<Cours> getListeCours(String trimestre);

}
