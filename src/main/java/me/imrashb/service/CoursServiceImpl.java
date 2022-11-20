package me.imrashb.service;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.*;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@EnableScheduling
@Slf4j
@Scope("singleton")
public class CoursServiceImpl implements CoursService {

    private HashMap<String, List<Cours>> coursParSessions = new HashMap<>();
    private List<CoursServiceReadyListener> coursServiceReadyListener = new ArrayList<>();
    private Integer derniereSession = null;
    @Getter
    private boolean ready = false;

    @Override
    public void addSession(Session session, List<Cours> cours) {

        final String nomSession = session.toString();
        final int idSession = session.toId();

        if(coursParSessions.containsKey(nomSession)) {
            coursParSessions.replace(nomSession, cours);
        } else {

            final int sessionId = session.toId();
            if(derniereSession == null || derniereSession < sessionId) {
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
        if(tmp != ready) {
            fireReadyListeners();
        }
    }

    private void fireReadyListeners() {
        for(CoursServiceReadyListener listener : coursServiceReadyListener) {
            listener.onCoursServiceReady(ready);
        }
    }

    public interface CoursServiceReadyListener {
        void onCoursServiceReady(boolean ready);
    }
}
