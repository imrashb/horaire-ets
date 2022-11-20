package me.imrashb.service;

import me.imrashb.domain.*;

import java.util.*;

public interface CoursService {

    void addCoursManagerReadyListener(CoursServiceImpl.CoursServiceReadyListener listener);

    void addSession(Session session, List<Cours> cours);

    int getDerniereSession();

    Set<String> getSessions();

    List<Cours> getListeCours(String session);

    void setReady(boolean ready);

    boolean isReady();
}
