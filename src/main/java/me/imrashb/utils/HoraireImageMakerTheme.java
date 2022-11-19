package me.imrashb.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

@AllArgsConstructor
@Data
public class HoraireImageMakerTheme {
    private String id;
    private String nom;
    private Color colorBackground;
    private Color colorLigneSeparation;
    private Color colorDashedLigneSeparation;
    private Color colorFinDeSemaine;
    private Color colorTexteCours;
    private Color colorTexteOutline;
    private Color colorJour;
    private Color colorHeure;
}