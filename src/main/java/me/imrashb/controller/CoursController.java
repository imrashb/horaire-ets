package me.imrashb.controller;

import me.imrashb.domain.Cours;
import me.imrashb.service.CoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cours")
public class CoursController {

    @Autowired
    private CoursService service;

    @GetMapping("")
    public List<Cours> getCours(@RequestParam String session) {
        return service.getListeCours(session);
    }

}
