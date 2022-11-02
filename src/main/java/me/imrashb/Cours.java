package me.imrashb;

import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cours {

    private String id;

    private List<Groupe> groupes = new ArrayList<>();

}
