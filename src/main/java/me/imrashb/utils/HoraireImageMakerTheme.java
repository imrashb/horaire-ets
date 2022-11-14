package me.imrashb.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

@AllArgsConstructor
@Data
public class HoraireImageMakerTheme {
    private final Color colorBackground;
    private final Color colorLigneSeparation;
    private final Color colorFinDeSemaine;
    private final Color colorTexteCours;
    private final Color colorTexteOutline;
}
