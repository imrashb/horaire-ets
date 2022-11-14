package me.imrashb.utils;

import lombok.*;
import me.imrashb.domain.*;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.IOException;
import java.io.InputStream;

public class HoraireImageMaker {

    public static final HoraireImageMakerTheme LIGHT_THEME = new HoraireImageMakerTheme(Color.white, Color.gray, Color.lightGray, Color.white, Color.black);
    public static final HoraireImageMakerTheme DARK_THEME = new HoraireImageMakerTheme(Color.darkGray, Color.lightGray, Color.gray, Color.black, Color.white);
    private static final Font COURS_FONT;
    private static final Font FONT;

    // Load font from resources
    static {
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
    private final Color COLOR_BACKGROUND;
    private final Color COLOR_LIGNE_SEPARATION;
    private final Color COLOR_FIN_DE_SEMAINE;
    private final Color COLOR_TEXTE_COURS;
    private final Color COLOR_TEXTE_OUTLINE;
    private static final Stroke TEXTE_STROKE = new BasicStroke(4.0f);
    private static final Stroke HEURE_STROKE = new BasicStroke(2);
    private static final Stroke JOUR_STROKE = new BasicStroke(2);
    private static final Stroke DEMI_HEURE_STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
            0, new float[]{9}, 0);
    private static final int WIDTH = 1600;
    private static final int HEIGHT = 1000;
    private static final int ARC_COURS = 30;
    private static final int BORDER_COURS = 2;
    private static final int TEXTURE_COURS = BORDER_COURS*6;
    private static final int TOP_PADDING = 100;
    private static final int BOTTOM_PADDING = 50;
    private static final int LEFT_PADDING = 100;
    private static final int RIGHT_PADDING = 200;
    private static final int TEXT_PADDING = 10;
    private static final int DEBUT_COURS = 8;
    private static final int FIN_COURS = 23;
    private static final int NOMBRE_SEPARATIONS_HEURE = FIN_COURS-DEBUT_COURS;
    private static final int PX_ENTRE_HEURE = (HEIGHT-TOP_PADDING-BOTTOM_PADDING)/NOMBRE_SEPARATIONS_HEURE;
    private static final int PX_ENTRE_JOUR = (WIDTH-LEFT_PADDING-RIGHT_PADDING)/7;
    private CombinaisonHoraire horaire;

    public HoraireImageMaker(CombinaisonHoraire horaire) {
        this(horaire, LIGHT_THEME);
    }

    public HoraireImageMaker(CombinaisonHoraire horaire, HoraireImageMakerTheme theme) {
        this.horaire = horaire;
        this.COLOR_BACKGROUND = theme.getColorBackground();
        this.COLOR_FIN_DE_SEMAINE = theme.getColorFinDeSemaine();
        this.COLOR_TEXTE_COURS = theme.getColorTexteCours();
        this.COLOR_LIGNE_SEPARATION = theme.getColorLigneSeparation();
        this.COLOR_TEXTE_OUTLINE = theme.getColorTexteOutline();
    }


    public Image drawHoraire() {
        return this.drawHoraire(WIDTH);
    }

    public Image drawHoraire(int width) {

        AffineTransform at = new AffineTransform();
        double scale = ((double)width)/(double)WIDTH;
        at.setToScale(scale, scale);


        BufferedImage bufferedImage = new BufferedImage((int) (WIDTH*scale), (int) (HEIGHT*scale), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setTransform(at);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        this.drawBackground(g2d);
        this.drawJours(g2d);
        this.drawHeures(g2d);
        this.drawCours(g2d);

        g2d.dispose();
        return bufferedImage;
    }

    private void drawBackground(Graphics2D g2d) {
        g2d.setColor(COLOR_BACKGROUND);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
    }

    private void drawHeures(Graphics2D g2d) {
        g2d.setColor(COLOR_LIGNE_SEPARATION);
        g2d.setFont(FONT);
        int width = PX_ENTRE_JOUR*7;
        for(int i = 0; i<=NOMBRE_SEPARATIONS_HEURE; i++) {

            String heure = (i+DEBUT_COURS)+":00";
            int fontWidth = g2d.getFontMetrics().stringWidth(heure);
            int fontHeight = g2d.getFontMetrics().getAscent()-g2d.getFontMetrics().getDescent();
            int height = TOP_PADDING+i*PX_ENTRE_HEURE;
            g2d.setStroke(HEURE_STROKE);
            g2d.drawLine(LEFT_PADDING,height, LEFT_PADDING+width, height);
            g2d.drawString(heure, LEFT_PADDING-fontWidth-TEXT_PADDING, height+fontHeight/2);

            if(i != NOMBRE_SEPARATIONS_HEURE) {
                g2d.setStroke(DEMI_HEURE_STROKE);
                g2d.drawLine(LEFT_PADDING,height+PX_ENTRE_HEURE/2, LEFT_PADDING+width, height+PX_ENTRE_HEURE/2);
            }

        }
    }

    private void drawJours(Graphics2D g2d) {
        g2d.setFont(FONT);

        int i = 0;
        for(Jour j : Jour.values()) {

            if(j == Jour.DIMANCHE || j == Jour.SAMEDI) {
                g2d.setColor(COLOR_FIN_DE_SEMAINE);
                g2d.fillRect(LEFT_PADDING+PX_ENTRE_JOUR*i, TOP_PADDING, PX_ENTRE_JOUR, PX_ENTRE_HEURE*(FIN_COURS-DEBUT_COURS));
            }

            String texte = j.getNom();
            int fontWidth = g2d.getFontMetrics().stringWidth(texte);

            g2d.setColor(COLOR_LIGNE_SEPARATION);
            g2d.setStroke(JOUR_STROKE);
            g2d.drawLine(LEFT_PADDING+PX_ENTRE_JOUR*i,TOP_PADDING, LEFT_PADDING+PX_ENTRE_JOUR*i, TOP_PADDING+PX_ENTRE_HEURE*(FIN_COURS-DEBUT_COURS));
            g2d.setFont(FONT.deriveFont(Font.BOLD));
            g2d.drawString(texte, LEFT_PADDING+PX_ENTRE_JOUR*i+PX_ENTRE_JOUR/2-fontWidth/2, TOP_PADDING-TEXT_PADDING);
            i++;
        }

        g2d.setStroke(JOUR_STROKE);
        g2d.drawLine(LEFT_PADDING+PX_ENTRE_JOUR*i,TOP_PADDING, LEFT_PADDING+PX_ENTRE_JOUR*i, TOP_PADDING+PX_ENTRE_HEURE*(FIN_COURS-DEBUT_COURS));
    }

    private void drawCours(Graphics2D g2d) {
        int i = 0;

        for(Groupe groupe : horaire.getGroupes()) {

            Color color = Color.getHSBColor(((float)i)/(float)horaire.getGroupes().size(), 0.8f, 1.f);


            for(Activite activite : groupe.getActivites()) {
                HoraireActivite h = activite.getHoraire();
                int idJour = h.getJour().getId();
                double debut = h.getHeureDepart()/100-DEBUT_COURS+(h.getHeureDepart()%100 == 0 ? 0 : 0.5d);
                double fin = h.getHeureFin()/100-DEBUT_COURS+(h.getHeureFin()%100 == 0 ? 0 : 0.5d);

                int x = LEFT_PADDING+idJour*PX_ENTRE_JOUR;
                int y = (int) (TOP_PADDING+PX_ENTRE_HEURE*debut);
                int height = (int) (PX_ENTRE_HEURE*(fin-debut));

                // Dessin du cours avec effet d'ombrage
                g2d.setColor(color.darker());
                g2d.fillRoundRect(x+BORDER_COURS, y, PX_ENTRE_JOUR-2*BORDER_COURS, height-BORDER_COURS, ARC_COURS, ARC_COURS);
                g2d.setColor(color);
                g2d.fillRoundRect(x+2*BORDER_COURS, y, PX_ENTRE_JOUR-4*BORDER_COURS, height-TEXTURE_COURS, ARC_COURS, ARC_COURS);

                g2d.setFont(COURS_FONT);
                int currentHeight = y+TEXT_PADDING+g2d.getFontMetrics().getAscent();
                final int offset = g2d.getFontMetrics().getAscent()+TEXT_PADDING;
                g2d.setColor(COLOR_TEXTE_COURS);
                this.drawOutlinedText(g2d, groupe.toString(), x+TEXT_PADDING, currentHeight, COLOR_TEXTE_OUTLINE);
                currentHeight+=offset;
                this.drawOutlinedText(g2d, activite.getNom(), x+TEXT_PADDING, currentHeight, COLOR_TEXTE_OUTLINE);
                currentHeight+=offset;
                this.drawOutlinedText(g2d, h.toString(), x+TEXT_PADDING, currentHeight, COLOR_TEXTE_OUTLINE);

            }

            g2d.setFont(FONT);
            // Dessin du cours dans la liste des cours
            int fontWidth = g2d.getFontMetrics().stringWidth(groupe.toString());
            int fontHeight = g2d.getFontMetrics().getAscent();
            int xListeCours = WIDTH-RIGHT_PADDING/2-fontWidth/2-BORDER_COURS-TEXT_PADDING;
            int yListeCours = TOP_PADDING+2*i*(fontHeight+TEXT_PADDING*2);
            g2d.setColor(color.darker());
            g2d.fillRoundRect(xListeCours, yListeCours, fontWidth+2*TEXT_PADDING, fontHeight+2*TEXT_PADDING+TEXTURE_COURS/2, ARC_COURS, ARC_COURS);
            g2d.setColor(color);
            g2d.fillRoundRect(xListeCours+BORDER_COURS, yListeCours, fontWidth+2*TEXT_PADDING-2*BORDER_COURS, fontHeight+2*TEXT_PADDING, ARC_COURS, ARC_COURS);
            g2d.setColor(COLOR_TEXTE_COURS);
            g2d.drawString(groupe.toString(), xListeCours+TEXT_PADDING, yListeCours+fontHeight+TEXT_PADDING);
            this.drawOutlinedText(g2d, groupe.toString(), xListeCours+TEXT_PADDING, yListeCours+fontHeight+TEXT_PADDING, COLOR_TEXTE_OUTLINE);

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
        transform.translate(x,y);
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

