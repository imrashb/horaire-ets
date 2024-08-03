package me.imrashb.service;

import me.imrashb.domain.*;

import java.util.List;
import java.util.Set;

public interface SessionService {

    void addSessionManagerReadyListener(SessionServiceImpl.SessionServiceReadyListener listener);

    void addSession(Session session, List<Cours> cours, List<Programme> programmes);

    int getDerniereSession();

    Set<String> getSessions();

    Set<Programme> getProgrammes(String session);

    List<Cours> getListeCours(String session);

    Set<Cours> getCoursFromSigles(String sessionId, String... cours);

    boolean isReady();

    void setReady(boolean ready);
}
