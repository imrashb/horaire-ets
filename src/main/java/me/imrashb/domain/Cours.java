package me.imrashb.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cours {

    private String sigle;

    @JsonIgnore
    private List<Groupe> groupes;

}
