package me.imrashb.utils;

import me.imrashb.domain.Activite;
import me.imrashb.domain.Groupe;
import me.imrashb.domain.HoraireActivite;
import me.imrashb.domain.Jour;
import me.imrashb.domain.combinaison.CombinaisonHoraire;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.stream.*;

public class HoraireImageMaker {

    public static final HoraireImageMakerTheme LIGHT_THEME = new HoraireImageMakerTheme(
            "lumiere", "Thème lumière", Color.white, Color.gray, Color.gray, Color.lightGray, Color.white, Color.black, Color.gray, Color.gray);
    public static final HoraireImageMakerTheme DARK_THEME = new HoraireImageMakerTheme(
            "sombre", "Thème sombre", Color.darkGray, Color.lightGray, Color.lightGray, Color.gray, Color.white, Color.black, Color.white, Color.white);
    public static final HoraireImageMakerTheme CYAN_THEME = new HoraireImageMakerTheme(
            "cyan", "Thème cyan", Color.decode("#023047"), Color.decode("#8ECAE6"), Color.decode("#FFB703"),
            Color.decode("#219EBC"), Color.white, Color.black, Color.decode("#FB8500"), Color.decode("#FB8500"));
    public static final HoraireImageMakerTheme FOREST_THEME = new HoraireImageMakerTheme(
            "foret", "Thème forêt", Color.decode("#283618"), Color.decode("#FEFAE0"), Color.decode("#DDA15E"),
            Color.decode("#606C38"), Color.white, Color.black, Color.decode("#DDA15E"), Color.decode("#DDA15E"));
    public static final HoraireImageMakerTheme NIGHT_THEME = new HoraireImageMakerTheme(
            "nuit", "Thème nuit", Color.decode("#000814"), Color.decode("#003566"), Color.decode("#ffc300"), Color.decode("#001d3d"), Color.decode("#ffffff"), Color.decode("#000000"), Color.decode("#ffd60a"), Color.decode("#ffd60a"));
    public static final HoraireImageMakerTheme PURPLE_THEME = new HoraireImageMakerTheme(
            "mauve", "Thème mauve", Color.decode("#240046"), Color.decode("#5a189a"), Color.decode("#7b2cbf"), Color.decode("#3c096c"), Color.decode("#ffffff"), Color.decode("#000000"), Color.decode("#c77dff"), Color.decode("#c77dff"));
    public static final HoraireImageMakerTheme BLUE_THEME = new HoraireImageMakerTheme(
            "bleu", "Thème bleu", Color.decode("#012a4a"), Color.decode("#61a5c2"), Color.decode("#2a6f97"), Color.decode("#013a63"), Color.decode("#ffffff"), Color.decode("#000000"), Color.decode("#89c2d9"), Color.decode("#89c2d9"));
    public static final HoraireImageMakerTheme EMERALD_THEME = new HoraireImageMakerTheme(
            "emeraude", "Thème emeraude", Color.decode("#004b23"), Color.decode("#38b000"), Color.decode("#ccff33"), Color.decode("#006400"), Color.decode("#ffffff"), Color.decode("#000000"), Color.decode("#9ef01a"), Color.decode("#9ef01a"));


    public static final java.util.List<HoraireImageMakerTheme> themes;

    private static final Font COURS_FONT;
    private static final Font FONT;
    private static final Stroke TEXTE_STROKE = new BasicStroke(4.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final Stroke HEURE_STROKE = new BasicStroke(2);
    private static final Stroke JOUR_STROKE = new BasicStroke(2);
    private static final Stroke DEMI_HEURE_STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
            0, new float[]{9}, 0);
    private static final double UPSCALING = 1.5f;
    private static final int WIDTH = 1600;
    private static final int HEIGHT = 1200;
    private static final int ARC_COURS = 30;
    private static final int BORDER_COURS = 2;
    private static final int TEXTURE_COURS = BORDER_COURS * 6;
    private static final int TOP_PADDING = 100;
    private static final int BOTTOM_PADDING = 50;
    private static final int LEFT_PADDING = 100;
    private static final int RIGHT_PADDING = 200;
    private static final int TEXT_PADDING = 10;
    private static final int DEBUT_COURS = 8;
    private static final int FIN_COURS = 23;
    private static final int NOMBRE_SEPARATIONS_HEURE = FIN_COURS - DEBUT_COURS;
    private static final int PX_ENTRE_HEURE = (HEIGHT - TOP_PADDING - BOTTOM_PADDING) / NOMBRE_SEPARATIONS_HEURE;
    private static final int PX_ENTRE_JOUR = (WIDTH - LEFT_PADDING - RIGHT_PADDING) / 7;

    // Load font from resources
    static {
        themes = new ArrayList<>();
        themes.add(LIGHT_THEME);
        themes.add(DARK_THEME);
        themes.add(CYAN_THEME);
        themes.add(FOREST_THEME);
        themes.add(NIGHT_THEME);
        themes.add(PURPLE_THEME);
        themes.add(BLUE_THEME);
        themes.add(EMERALD_THEME);

        final String filename = "/bahnschrift.ttf";
        InputStream is = HoraireImageMaker.class.getResourceAsStream(filename);
        try {
            COURS_FONT = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 25f);
            FONT = COURS_FONT.deriveFont(Font.PLAIN, 30f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private final CombinaisonHoraire horaire;
    private final HoraireImageMakerTheme theme;

    public HoraireImageMaker(CombinaisonHoraire horaire) {
        this(horaire, LIGHT_THEME);
    }

    public HoraireImageMaker(CombinaisonHoraire horaire, HoraireImageMakerTheme theme) {
        this.horaire = horaire;
        this.theme = theme;
    }

    public static HoraireImageMakerTheme getThemeFromId(String themeId) {
        if (themeId == null) return LIGHT_THEME;

        for (HoraireImageMakerTheme t : HoraireImageMaker.themes) {
            if (t.getId().equals(themeId)) {
                return t;
            }
        }
        return LIGHT_THEME;
    }

    public Future<Image> drawHoraire() {
        return this.drawHoraire((int) (WIDTH*UPSCALING));
    }

    public Future<Image> drawHoraire(int width) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        return executor.submit(() -> {
            AffineTransform at = new AffineTransform();
            double scale = ((double) width) / (double) WIDTH;
            at.setToScale(scale, scale);


            BufferedImage bufferedImage = new BufferedImage((int) (WIDTH * scale), (int) (HEIGHT * scale), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setTransform(at);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            this.drawBackground(g2d);
            this.drawJours(g2d);
            this.drawHeures(g2d);
            this.drawCours(g2d);

            g2d.dispose();
            return bufferedImage;
        });

    }

    private void drawBackground(Graphics2D g2d) {
        g2d.setColor(theme.getColorBackground());
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
    }

    private void drawHeures(Graphics2D g2d) {
        g2d.setFont(FONT);
        int width = PX_ENTRE_JOUR * 7;
        for (int i = 0; i <= NOMBRE_SEPARATIONS_HEURE; i++) {

            String heure = (i + DEBUT_COURS) + ":00";
            int fontWidth = g2d.getFontMetrics().stringWidth(heure);
            int fontHeight = g2d.getFontMetrics().getAscent() - g2d.getFontMetrics().getDescent();
            int height = TOP_PADDING + i * PX_ENTRE_HEURE;
            g2d.setColor(this.theme.getColorLigneSeparation());
            g2d.setStroke(HEURE_STROKE);
            g2d.drawLine(LEFT_PADDING, height, LEFT_PADDING + width, height);
            g2d.setColor(this.theme.getColorHeure());
            g2d.drawString(heure, LEFT_PADDING - fontWidth - TEXT_PADDING, height + fontHeight / 2);

            if (i != NOMBRE_SEPARATIONS_HEURE) {
                g2d.setColor(this.theme.getColorDashedLigneSeparation());
                g2d.setStroke(DEMI_HEURE_STROKE);
                g2d.drawLine(LEFT_PADDING, height + PX_ENTRE_HEURE / 2, LEFT_PADDING + width, height + PX_ENTRE_HEURE / 2);
            }

        }
    }

    private void drawJours(Graphics2D g2d) {
        g2d.setFont(FONT);

        int i = 0;
        for (Jour j : Jour.values()) {

            if (j == Jour.DIMANCHE || j == Jour.SAMEDI) {
                g2d.setColor(this.theme.getColorFinDeSemaine());
                g2d.fillRect(LEFT_PADDING + PX_ENTRE_JOUR * i, TOP_PADDING, PX_ENTRE_JOUR, PX_ENTRE_HEURE * (FIN_COURS - DEBUT_COURS));
            }

            String texte = j.getNom();
            int fontWidth = g2d.getFontMetrics().stringWidth(texte);

            g2d.setColor(this.theme.getColorLigneSeparation());
            g2d.setStroke(JOUR_STROKE);
            g2d.drawLine(LEFT_PADDING + PX_ENTRE_JOUR * i, TOP_PADDING, LEFT_PADDING + PX_ENTRE_JOUR * i, TOP_PADDING + PX_ENTRE_HEURE * (FIN_COURS - DEBUT_COURS));
            g2d.setFont(FONT.deriveFont(Font.BOLD));
            g2d.setColor(this.theme.getColorJour());
            g2d.drawString(texte, LEFT_PADDING + PX_ENTRE_JOUR * i + PX_ENTRE_JOUR / 2 - fontWidth / 2, TOP_PADDING - TEXT_PADDING);
            i++;
        }

        g2d.setColor(this.theme.getColorLigneSeparation());
        g2d.setStroke(JOUR_STROKE);
        g2d.drawLine(LEFT_PADDING + PX_ENTRE_JOUR * i, TOP_PADDING, LEFT_PADDING + PX_ENTRE_JOUR * i, TOP_PADDING + PX_ENTRE_HEURE * (FIN_COURS - DEBUT_COURS));
    }

    private void drawCours(Graphics2D g2d) {
        int i = 0;

        for (Groupe groupe : horaire.getGroupes()) {

            Color color = Color.getHSBColor(((float) i) / (float) horaire.getGroupes().size(), 0.8f, 1.f);


            for (Activite activite : groupe.getActivites()) {
                HoraireActivite h = activite.getHoraire();
                int idJour = h.getJour().getId();
                double debut = h.getHeureDepart() / 100 - DEBUT_COURS + (h.getHeureDepart() % 100 == 0 ? 0 : 0.5d);
                double fin = h.getHeureFin() / 100 - DEBUT_COURS + (h.getHeureFin() % 100 == 0 ? 0 : 0.5d);

                int x = LEFT_PADDING + idJour * PX_ENTRE_JOUR;
                int y = (int) (TOP_PADDING + PX_ENTRE_HEURE * debut);
                int height = (int) (PX_ENTRE_HEURE * (fin - debut));

                // Dessin du cours avec effet d'ombrage
                g2d.setColor(color.darker());
                g2d.fillRoundRect(x + BORDER_COURS, y, PX_ENTRE_JOUR - 2 * BORDER_COURS, height - BORDER_COURS, ARC_COURS, ARC_COURS);
                g2d.setColor(color);
                g2d.fillRoundRect(x + 2 * BORDER_COURS, y, PX_ENTRE_JOUR - 4 * BORDER_COURS, height - TEXTURE_COURS, ARC_COURS, ARC_COURS);

                g2d.setFont(COURS_FONT);
                int currentHeight = y + TEXT_PADDING + g2d.getFontMetrics().getAscent();
                final int offset = g2d.getFontMetrics().getAscent() + TEXT_PADDING;
                g2d.setColor(this.theme.getColorTexteCours());
                this.drawOutlinedText(g2d, groupe.toString(), x + TEXT_PADDING, currentHeight, this.theme.getColorTexteOutline());
                currentHeight += offset;
                this.drawOutlinedText(g2d, activite.getNom(), x + TEXT_PADDING, currentHeight, this.theme.getColorTexteOutline());
                for(String local : activite.getLocaux()) {
                    currentHeight += offset;
                    this.drawOutlinedText(g2d, local, x + TEXT_PADDING, currentHeight, this.theme.getColorTexteOutline());
                }
            }

            g2d.setFont(FONT);
            // Dessin du cours dans la liste des cours
            int fontWidth = g2d.getFontMetrics().stringWidth(groupe.toString());
            int fontHeight = g2d.getFontMetrics().getAscent();
            int xListeCours = WIDTH - RIGHT_PADDING / 2 - fontWidth / 2 - BORDER_COURS - TEXT_PADDING;
            int yListeCours = TOP_PADDING + 2 * i * (fontHeight + TEXT_PADDING * 2);
            g2d.setColor(color.darker());
            g2d.fillRoundRect(xListeCours, yListeCours, fontWidth + 2 * TEXT_PADDING, fontHeight + 2 * TEXT_PADDING + TEXTURE_COURS / 2, ARC_COURS, ARC_COURS);
            g2d.setColor(color);
            g2d.fillRoundRect(xListeCours + BORDER_COURS, yListeCours, fontWidth + 2 * TEXT_PADDING - 2 * BORDER_COURS, fontHeight + 2 * TEXT_PADDING, ARC_COURS, ARC_COURS);
            g2d.setColor(this.theme.getColorTexteCours());
            g2d.drawString(groupe.toString(), xListeCours + TEXT_PADDING, yListeCours + fontHeight + TEXT_PADDING);
            this.drawOutlinedText(g2d, groupe.toString(), xListeCours + TEXT_PADDING, yListeCours + fontHeight + TEXT_PADDING, this.theme.getColorTexteOutline());

            i++;
        }

    }

    private void drawOutlinedText(Graphics2D g2d, String texte, int x, int y, Color outlineColor) {
        GlyphVector glyphVector = g2d.getFont().createGlyphVector(g2d.getFontRenderContext(), texte);
        Shape textShape = glyphVector.getOutline();
        Color originalColor = g2d.getColor();
        Stroke originalStroke = g2d.getStroke();
        AffineTransform originalTransform = g2d.getTransform();

        AffineTransform transform = new AffineTransform(originalTransform);
        transform.translate(x, y);
        g2d.setTransform(transform);
        g2d.setColor(outlineColor);
        g2d.setStroke(TEXTE_STROKE);
        g2d.draw(textShape);

        g2d.setTransform(originalTransform);
        g2d.setStroke(originalStroke);
        g2d.setColor(originalColor);
        g2d.drawString(texte, x, y);
    }

}

