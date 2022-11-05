package me.imrashb.domain;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("singleton")
public class CoursManager {

    private HashMap<String, List<Cours>> coursParTrimestre = new HashMap<>();

    @Getter
    private String dernierTrimestre = null;

    public void addCoursManagerReadyListener(CoursManagerReadyListener listener) {
        this.coursManagerReadyListeners.add(listener);
    }

    public void addTrimestre(String trimestre, List<Cours> cours) {

        if(coursParTrimestre.containsKey(trimestre)) {
            coursParTrimestre.replace(trimestre, cours);
        } else {

            final int trimestreInt = Integer.parseInt(trimestre);
            if(dernierTrimestre == null || Integer.parseInt(dernierTrimestre) < trimestreInt) {
                dernierTrimestre = trimestre;
            }

            coursParTrimestre.put(trimestre, cours);
        }
    }

    public Set<String> getTrimestres() {
        return coursParTrimestre.keySet();
    }

    public List<Cours> getListeCours(String trimestre) {
        return coursParTrimestre.get(trimestre);
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

