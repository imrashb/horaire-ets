package me.imrashb.service;

import lombok.*;
import lombok.extern.slf4j.*;
import me.imrashb.domain.*;
import me.imrashb.repository.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@Slf4j
@Scope("singleton")
public class StatisticsService {

    private final StatisticsRepository repository;

    @Getter
    private final Statistics statistics;

    public StatisticsService(StatisticsRepository repository) {
        this.repository = repository;
        statistics = this.loadStatistics();
    }

    private Statistics loadStatistics() {
        Optional<Statistics> opt = repository.findFirstByOrderByIdDesc();
        return opt.orElse(new Statistics());
    }

    public void save() {
        repository.save(statistics);
    }
}
