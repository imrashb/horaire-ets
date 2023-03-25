package me.imrashb.repository;

import me.imrashb.domain.CoursDataWrapper;
import org.springframework.data.repository.CrudRepository;

public interface ScrapedCoursDataRepository extends CrudRepository<CoursDataWrapper, String> {
}
