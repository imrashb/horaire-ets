package me.imrashb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CombinaisonHoraire {

    private List<Groupe> groupes;

}
