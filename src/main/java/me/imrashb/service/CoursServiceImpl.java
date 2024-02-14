package me.imrashb.service;

import lombok.extern.slf4j.*;
import me.imrashb.domain.*;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.*;

@Service
@EnableScheduling
@Slf4j
@Scope("singleton")
public class CoursServiceImpl implements CoursService {

    private List<CoursWithoutGroupes> cours = null;
    private SessionService sessionService;

    public CoursServiceImpl(SessionService sessionService) {
       this.sessionService = sessionService;
    }

   private List<CoursWithoutGroupes> getLazyLoadedCours() {
        if(this.cours != null) return cours;

        Set<String> sessions = sessionService.getSessions();

        Map<String, CoursWithoutGroupes> map = new HashMap();

       for(String session : sessions) {
           List<Cours> coursSession = sessionService.getListeCours(session);

            for(Cours c : coursSession) {
                if(!map.containsKey(c.getSigle())) {
                    map.put(c.getSigle(), new CoursWithoutGroupes(c.getSigle(), c.getPrealables(), c.getCredits(), c.getProgrammes(), c.getTitre()));
                }
            }

       }
       this.cours = new ArrayList(map.values());
       return this.cours;
   }

    @Override
    public List<CoursWithoutGroupes> getCours(List<Programme> programmes) {
        if(!sessionService.isReady()) return null;
        List<CoursWithoutGroupes> cours = getLazyLoadedCours();

        if(programmes == null) return cours;

        List<CoursWithoutGroupes> filtered = cours.stream().filter((c) -> !Collections.disjoint(c.getProgrammes(), programmes)).collect(Collectors.toList());
        return filtered;
    }


}

