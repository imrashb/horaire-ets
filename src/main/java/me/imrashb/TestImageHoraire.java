package me.imrashb;

import me.imrashb.domain.*;
import me.imrashb.utils.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class TestImageHoraire {

    public static int pdfCount=0;

    public static void main(String[] args) throws IOException {

        List<Groupe> groupes = new ArrayList<>();
        Groupe g = null;
        {
            Activite a = new Activite("Labo", "D", new HoraireActivite(8, 30, 10, 30, "lun"));


            ArrayList<Activite> list = new ArrayList();
            list.add(a);
            Cours c = new Cours("LOG100", new ArrayList<>(), new HashSet<>());
            c.addProgramme(Programme.TI);
            g = new Groupe("01", list, c);
            c.addGroupe(g);

            groupes.add(g);
        }

        {
            Activite a1 = new Activite("RUBIK = MAD", "C", new HoraireActivite(13, 30, 17, 0, "lun"));

            Activite a2 = new Activite("TP A + B", "C", new HoraireActivite(8, 30, 12, 30, "mar"));

            ArrayList<Activite> list = new ArrayList();
            list.add(a1);
            list.add(a2);
            Cours c = new Cours("LOG320", new ArrayList<>(), new HashSet<>());
            c.addProgramme(Programme.TI);
            g = new Groupe("02", list, c);
            c.addGroupe(g);

            groupes.add(g);
        }

        {

            Activite a2 = new Activite("TP A + B", "C", new HoraireActivite(8, 30, 12, 30, "mar"));

            ArrayList<Activite> list = new ArrayList();
            list.add(a2);
            Cours c = new Cours("LOG240", new ArrayList<>(), new HashSet<>());
            c.addProgramme(Programme.TI);
            g = new Groupe("02", list, c);
            c.addGroupe(g);

            groupes.add(g);
        }

        CombinaisonHoraire comb = new CombinaisonHoraire(groupes);

        Image image = new HoraireImageMaker(comb).drawHoraire();

        ImageIO.write((RenderedImage) image, "png", new File("here.png"));

        Image resultingImage = image.getScaledInstance((int) (image.getWidth(null)/2f), (int) (image.getHeight(null)/2f), Image.SCALE_DEFAULT);
        JLabel picLabel = new JLabel(new ImageIcon(resultingImage));
        JFrame frame = new JFrame();
        frame.add(picLabel);
        frame.setSize(new Dimension(1400, 900));
        frame.setPreferredSize(new Dimension(1400, 900));
        frame.setVisible(true);
    }

}