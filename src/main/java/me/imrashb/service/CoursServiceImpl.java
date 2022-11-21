package me.imrashb.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.Cours;
import me.imrashb.domain.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
@EnableScheduling
@Slf4j
@Scope("singleton")
public class CoursServiceImpl implements CoursService {

    private final HashMap<String, List<Cours>> coursParSessions = new HashMap<>();
    private final List<CoursServiceReadyListener> coursServiceReadyListener = new ArrayList<>();
    private Integer derniereSession = null;
    @Getter
    private boolean ready = false;

    @Override
    public void addSession(Session session, List<Cours> cours) {

        final String nomSession = session.toString();
        final int idSession = session.toId();

        if (coursParSessions.containsKey(nomSession)) {
            coursParSessions.replace(nomSession, cours);
        } else {

            final int sessionId = session.toId();
            if (derniereSession == null || derniereSession < sessionId) {
                derniereSession = idSession;
            }

            coursParSessions.put(nomSession, cours);
        }
    }

    @Override
    public int getDerniereSession() {
        return this.derniereSession;
    }

    @Override
    public Set<String> getSessions() {
        return coursParSessions.keySet();
    }

    @Override
    public List<Cours> getListeCours(String sessionId) {
        return coursParSessions.get(sessionId);
    }

    @Override
    public void addCoursManagerReadyListener(CoursServiceReadyListener listener) {
        this.coursServiceReadyListener.add(listener);
    }

    @Override
    public void setReady(boolean ready) {
        boolean tmp = this.ready;
        this.ready = ready;
        //Fire events
        if (tmp != ready) {
            fireReadyListeners();
        }
    }

    private void fireReadyListeners() {
        for (CoursServiceReadyListener listener : coursServiceReadyListener) {
            listener.onCoursServiceReady(ready);
        }
    }

    public interface CoursServiceReadyListener {
        void onCoursServiceReady(boolean ready);
    }
}
