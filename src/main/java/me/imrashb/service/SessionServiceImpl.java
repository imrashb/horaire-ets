package me.imrashb.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.imrashb.domain.*;
import me.imrashb.exception.*;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@EnableScheduling
@Slf4j
@Scope("singleton")
public class SessionServiceImpl implements SessionService {

    private final HashMap<String, List<Cours>> coursParSessions = new LinkedHashMap<>();
    private final HashMap<String, Set<Programme>> programmesParSessions = new LinkedHashMap<>();
    private final List<SessionServiceReadyListener> sessionServiceReadyListeners = new ArrayList<>();
    private Integer derniereSession = null;
    @Getter
    private boolean ready = false;

    @Override
    public void addSession(Session session, List<Cours> cours, List<Programme> programmes) {
        final String nomSession = session.toString();
        final int idSession = session.toId();

        if (coursParSessions.containsKey(nomSession)) {
            coursParSessions.replace(nomSession, cours);
            programmesParSessions.replace(nomSession, new HashSet<>(programmes));
        } else {

            final int sessionId = session.toId();
            if (derniereSession == null || derniereSession < sessionId) {
                derniereSession = idSession;
            }

            coursParSessions.put(nomSession, cours);
            programmesParSessions.put(nomSession, new HashSet<>(programmes));
        }
    }

    @Override
    public int getDerniereSession() {
        validateReady();
        return this.derniereSession;
    }

    @Override
    public Set<String> getSessions() {
        validateReady();
        return coursParSessions.keySet();
    }

    @Override
    public Set<Programme> getProgrammes(String session) {
        if(session == null) return new HashSet<>(List.of(Programme.values()));
        return programmesParSessions.get(session);
    }

    @Override
    public List<Cours> getListeCours(String sessionId) {
        validateReady();
        return coursParSessions.get(sessionId);
    }

    @Override
    public Set<Cours> getCoursFromSigles(String sessionId, String... sigles) {
        validateReady();
        Set<Cours> cours = new HashSet<>();

        List<String> siglesList = new ArrayList<>(Arrays.asList(sigles));

        List<Cours> listeCours = getListeCours(sessionId);

        for (Cours c : listeCours) {
            for (String s : sigles) {
                if (c.getSigle().equalsIgnoreCase(s)) {
                    boolean added = cours.add(c);
                    if (!added) throw new CoursAlreadyPresentException();
                    siglesList.remove(s);
                }
            }
        }

        if (siglesList.size() > 0) {
            throw new CoursDoesntExistException(siglesList);
        }

        return cours;
    }

    @Override
    public void addSessionManagerReadyListener(SessionServiceReadyListener listener) {
        this.sessionServiceReadyListeners.add(listener);
    }

    private void validateReady() {
        if(!this.isReady()) throw new CoursNotInitializedException();
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
        for (SessionServiceReadyListener listener : sessionServiceReadyListeners) {
            listener.onSessionServiceReady(ready);
        }
    }

    public interface SessionServiceReadyListener {
        void onSessionServiceReady(boolean ready);
    }
}
