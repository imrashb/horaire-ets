package me.imrashb.service;

import me.imrashb.domain.*;

import java.util.*;

public interface CoursService {

    List<CoursWithoutGroupes> getCours(List<Programme> programmes);
}
