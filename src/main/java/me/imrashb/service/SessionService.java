package me.imrashb.service;

import me.imrashb.domain.Cours;
import me.imrashb.domain.Session;

import java.util.List;
import java.util.Set;

public interface SessionService {

    void addSessionManagerReadyListener(SessionServiceImpl.SessionServiceReadyListener listener);

    void addSession(Session session, List<Cours> cours);

    int getDerniereSession();

    Set<String> getSessions();

    List<Cours> getListeCours(String session);

    Set<Cours> getCoursFromSigles(String sessionId, String... cours);

    boolean isReady();

    void setReady(boolean ready);
}
