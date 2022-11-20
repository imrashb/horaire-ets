package me.imrashb.parser.strategy;

import me.imrashb.domain.*;

import java.util.*;

public interface HoraireValidationStrategy {

    boolean isValid(List<Groupe> current, Groupe groupe);

}
