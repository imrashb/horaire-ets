package me.imrashb.controller;

import me.imrashb.domain.*;
import me.imrashb.service.SessionService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @GetMapping("/{session}")
    public List<Cours> getCours(@PathVariable String session, @RequestParam(required = false) List<Programme> programmes) {
        List<Cours> cours = service.getListeCours(session);

        if(programmes != null && programmes.size() > 0) {
            Predicate<Cours> byProgramme = (c) -> !Collections.disjoint(c.getProgrammes(), programmes);
            return cours.stream().filter(byProgramme).collect(Collectors.toList());
        } else {
            return cours;
        }
    }

    @GetMapping("/programmes")
    public Programme[] getProgrammes() {
        return Programme.values();
    }

    @GetMapping("")
    public Set<String> getSessions() {
        return service.getSessions();
    }

}
