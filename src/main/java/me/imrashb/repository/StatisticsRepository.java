package me.imrashb.repository;

import me.imrashb.domain.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface StatisticsRepository extends CrudRepository<Statistics, Long> {

    Optional<Statistics> findFirstByOrderByIdDesc();

}
