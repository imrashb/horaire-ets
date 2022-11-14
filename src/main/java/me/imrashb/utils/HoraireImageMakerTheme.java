package me.imrashb.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

@AllArgsConstructor
@Data
public class HoraireImageMakerTheme {
    private final String nom;
    private final Color colorBackground;
    private final Color colorLigneSeparation;
    private final Color colorDashedLigneSeparation;
    private final Color colorFinDeSemaine;
    private final Color colorTexteCours;
    private final Color colorTexteOutline;
    private final Color colorJour;
    private final Color colorHeure;
}
