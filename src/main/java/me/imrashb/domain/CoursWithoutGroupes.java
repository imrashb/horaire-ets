package me.imrashb.domain;

import lombok.*;

import java.util.*;

@AllArgsConstructor
@Getter
public class CoursWithoutGroupes {
    private String sigle;
    private List<String> prealables;
    private Integer credits;
    private Set<Programme> programmes;
    private String titre;
}
