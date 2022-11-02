package me.imrashb.domain;

import lombok.*;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cours {

    private String sigle;
    private List<Groupe> groupes;

}
