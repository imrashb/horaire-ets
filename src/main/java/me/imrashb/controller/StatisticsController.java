package me.imrashb.controller;

import me.imrashb.domain.*;
import me.imrashb.service.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService service;

    public StatisticsController(StatisticsService service) {
        this.service = service;
    }

    @GetMapping("")
    public Statistics getStatistics() {
        return service.getStatistics();
    }

}
