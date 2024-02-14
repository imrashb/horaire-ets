package me.imrashb.controller;

import me.imrashb.domain.*;
import me.imrashb.exception.*;
import me.imrashb.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/cours")
public class CoursController {

    private final CoursService service;

    public CoursController(CoursService service) {
        this.service = service;
    }

    @GetMapping("")
    public List<CoursWithoutGroupes> getCours(@RequestParam(required = false) List<Programme> programmes) {
        List<CoursWithoutGroupes> cours = service.getCours(programmes);
        if(cours == null) throw new CoursNotInitializedException();
        return cours;
    }

}
