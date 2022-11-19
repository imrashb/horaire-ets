package me.imrashb.domain;
import lombok.*;
import me.imrashb.repository.PreferencesUtilisateurRepository;
import me.imrashb.service.PreferencesUtilisateurService;
import me.imrashb.service.PreferencesUtilisateurServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("singleton")
public class CoursManager {

    private HashMap<String, List<Cours>> coursParSessions = new HashMap<>();
    @Getter
    private Integer derniereSession = null;

    @Autowired
    @Getter
    private PreferencesUtilisateurService preferencesUtilisateurService;

    public void addCoursManagerReadyListener(CoursManagerReadyListener listener) {
        this.coursManagerReadyListeners.add(listener);
    }

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

    public Set<String> getSessions() {
        return coursParSessions.keySet();
    }

    public List<Cours> getListeCours(String sessionId) {
        return coursParSessions.get(sessionId);
    }

    private List<CoursManagerReadyListener> coursManagerReadyListeners = new ArrayList<>();

    @Getter
    private boolean ready = false;

    public void setReady(boolean ready) {
        boolean tmp = this.ready;
        this.ready = ready;

        //Fire events
        if(tmp != ready) {
            for(CoursManagerReadyListener listener : coursManagerReadyListeners) {
                listener.onCoursManagerReady(ready);
            }
        }
    }

    public interface CoursManagerReadyListener {
        void onCoursManagerReady(boolean ready);
    }

}

