package me.imrashb;

import me.imrashb.domain.*;
import me.imrashb.utils.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.function.*;

public class TestImageHoraire {

    public static int pdfCount=0;

    public static void main(String[] args) throws IOException {

        List<Groupe> groupes = new ArrayList<>();
        Groupe g = null;
        {
            Activite a = new Activite("Labo", "D", new HoraireActivite(8, 30, 10, 30, "lun"));


            ArrayList<Activite> list = new ArrayList();
            list.add(a);
            Cours c = new Cours("LOG100", new ArrayList<>(), new HashSet<>(), new Session(1234, Trimestre.HIVER));
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
            Cours c = new Cours("LOG320", new ArrayList<>(), new HashSet<>(), new Session(1234, Trimestre.HIVER));
            c.addProgramme(Programme.TI);
            g = new Groupe("02", list, c);
            c.addGroupe(g);

            groupes.add(g);
        }

        {

            Activite a2 = new Activite("TP A + B", "C", new HoraireActivite(8, 30, 12, 30, "mar"));

            ArrayList<Activite> list = new ArrayList();
            list.add(a2);
            Cours c = new Cours("LOG240", new ArrayList<>(), new HashSet<>(), new Session(1234, Trimestre.HIVER));
            c.addProgramme(Programme.TI);
            g = new Groupe("02", list, c);
            c.addGroupe(g);

            groupes.add(g);
        }

        CombinaisonHoraire comb = new CombinaisonHoraire(groupes);

        HoraireImageMakerTheme theme = HoraireImageMaker.LIGHT_THEME;
        Image image = new HoraireImageMaker(comb, theme).drawHoraire();

        ImageIO.write((RenderedImage) image, "png", new File("here.png"));

        List<ButtonConsumer> buttons = new ArrayList<>();


        Image resultingImage = image.getScaledInstance((int) (image.getWidth(null)/2f), (int) (image.getHeight(null)/2f), Image.SCALE_DEFAULT);
        JLabel picLabel = new JLabel(new ImageIcon(resultingImage));
        JFrame frame = new JFrame();
        frame.add(picLabel);
        frame.setLayout(new GridLayout(1,2));
        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(new GridLayout(buttons.size(), 1));

        ActionListener listener = event -> {
            System.out.println("HERE");
            Image img = new HoraireImageMaker(comb, theme).drawHoraire();
            try {
                ImageIO.write((RenderedImage) img, "jpeg", new File("save.jpeg"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(theme.getColorBackground());
            Image result = img.getScaledInstance((int) (img.getWidth(null)/2f), (int) (img.getHeight(null)/2f), Image.SCALE_DEFAULT);
            picLabel.setIcon(new ImageIcon(result));
            picLabel.repaint();
        };

        buttons.add(new ButtonConsumer("Background",theme::setColorBackground, listener));
        buttons.add(new ButtonConsumer("Jour",theme::setColorJour, listener));
        buttons.add(new ButtonConsumer("Heure",theme::setColorHeure, listener));
        buttons.add(new ButtonConsumer("Ligne separation ",theme::setColorLigneSeparation, listener));
        buttons.add(new ButtonConsumer("Dash Ligne Separation",theme::setColorDashedLigneSeparation, listener));
        buttons.add(new ButtonConsumer("Fin de semaine",theme::setColorFinDeSemaine, listener));
        buttons.add(new ButtonConsumer("Texte cours",theme::setColorTexteCours, listener));
        buttons.add(new ButtonConsumer("Texte outline",theme::setColorTexteOutline, listener));
        JButton generate = new JButton("Generate");
        generate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("public static final HoraireImageMakerTheme THEME_NAME = new HoraireImageMakerTheme(\n" +
                        "            \"Thème NAME\",Color.decode(\""+hex(theme.getColorBackground())+"\"), Color.decode(\""+hex(theme.getColorLigneSeparation())+"\"), Color.decode(\""+hex(theme.getColorDashedLigneSeparation())+"\"), Color.decode(\""+hex(theme.getColorFinDeSemaine())+"\"), Color.decode(\""+hex(theme.getColorTexteCours())+"\"), Color.decode(\""+hex(theme.getColorTexteOutline())+"\"), Color.decode(\""+hex(theme.getColorJour())+"\"), Color.decode(\""+hex(theme.getColorHeure())+"\"));");
            }
        });
        for(ButtonConsumer but : buttons) {
            panel.add(but);
            panel.setPreferredSize(new Dimension(200,10000));
        }
        panel.add(generate);

        frame.setSize(new Dimension(1700, 900));
        frame.setPreferredSize(new Dimension(1700, 900));
        frame.validate();
        frame.pack();
        frame.setVisible(true);
    }

    public static String hex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static final HoraireImageMakerTheme THEME_NAME = new HoraireImageMakerTheme(
            "Thème NAME",Color.decode("#001408"), Color.decode("#006635"), Color.decode("#ff0ad6"), Color.decode("#003d1d"), Color.decode("#ffffff"), Color.decode("#000000"), Color.decode("#ff0ad6"), Color.decode("#ff0ad6"));

}

class ButtonConsumer extends JButton {

    private Consumer<Color> consumer;

    public ButtonConsumer(String label, Consumer<Color> consumer, ActionListener listener) {
        this.setText(label);
        this.consumer = consumer;
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String data = null;
                try {
                    data = (String) Toolkit.getDefaultToolkit()
                            .getSystemClipboard().getData(DataFlavor.stringFlavor);
                    consumer.accept(Color.decode("#"+data));

                    listener.actionPerformed(null);
                } catch (UnsupportedFlavorException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

}