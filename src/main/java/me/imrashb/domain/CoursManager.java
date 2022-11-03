package me.imrashb.domain;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@Scope("singleton")
public class CoursManager {

    private HashMap<String, List<Cours>> coursParTrimestre = new HashMap<>();

    public boolean isReady() {
        return coursParTrimestre.size() != 0;
    }
    public void addTrimestre(String trimestre, List<Cours> cours) {
        if(coursParTrimestre.containsKey(trimestre)) {
            coursParTrimestre.replace(trimestre, cours);
        } else {
            coursParTrimestre.put(trimestre, cours);
        }
    }

    public List<Cours> getListeCours(String trimestre) {
        return coursParTrimestre.get(trimestre);
    }

}
