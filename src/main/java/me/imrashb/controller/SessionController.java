package me.imrashb.controller;

import me.imrashb.domain.Cours;
import me.imrashb.service.SessionService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @GetMapping("/{session}")
    public List<Cours> getCours(@PathVariable String session) {
        return service.getListeCours(session);
    }

    @GetMapping("")
    public Set<String> getSessions() {
        return service.getSessions();
    }

}
