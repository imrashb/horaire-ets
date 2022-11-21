package me.imrashb.service;

import me.imrashb.domain.Cours;
import me.imrashb.domain.Session;

import java.util.List;
import java.util.Set;

public interface CoursService {

    void addCoursManagerReadyListener(CoursServiceImpl.CoursServiceReadyListener listener);

    void addSession(Session session, List<Cours> cours);

    int getDerniereSession();

    Set<String> getSessions();

    List<Cours> getListeCours(String session);

    boolean isReady();

    void setReady(boolean ready);
}
