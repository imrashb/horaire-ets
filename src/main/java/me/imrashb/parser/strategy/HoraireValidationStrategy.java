package me.imrashb.parser.strategy;

import me.imrashb.domain.Groupe;

import java.util.List;

public interface HoraireValidationStrategy {

    boolean isValid(List<Groupe> current, Groupe groupe);

}
