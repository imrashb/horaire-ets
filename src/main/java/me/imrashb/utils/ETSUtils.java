package me.imrashb.utils;

import me.imrashb.domain.Trimestre;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ETSUtils {

    private static final String[] PROGRAMMES = {
            "SEG", // Enseignements generaux
            "7084", // LOG
            "5730", // CUT
            "7625", // CTN
            "7694", // ELE
            "7684", // MEC
            "6556", // GOL
            "6557", // GPA
            "7086", // TI
    };

    public static List<Future<File>> getFichiersHoraireAsync(int annee, Trimestre trimestre) {
        final List<Future<File>> futures = new ArrayList<>();

        final String idTrimestre = trimestre.getIdTrimestre(annee);

        final ExecutorService executor
                = Executors.newFixedThreadPool(PROGRAMMES.length);

        final String tmpFolder = "./pdf";
        new File(tmpFolder).mkdir();

        for (String id : PROGRAMMES) {
            Future<File> future = downloadHoraire(executor, tmpFolder, id, idTrimestre);
            futures.add(future);
        }

        executor.shutdownNow();

        return futures;
    }

    public static List<File> getFichiersHoraireSync(int annee, Trimestre trimestre) throws ExecutionException, InterruptedException {

        List<Future<File>> futures = getFichiersHoraireAsync(annee, trimestre);

        List<File> files = new ArrayList<>();
        //Resolve all futures
        for(Future<File> future : futures) {
            File f = future.get();
            if(f != null) {
                files.add(f);
            }
        }

        return files;
    }

    private static Future<File> downloadHoraire(ExecutorService executor, String tmpFolder, String programme, String idTrimestre) {

        return executor.submit(() -> {
            URL url = null;
            try {
                url = new URL("https://horaire.etsmtl.ca/HorairePublication/HorairePublication_" + idTrimestre + "_" + programme + ".pdf");
            } catch (MalformedURLException e) {
                System.err.println(e.getMessage());
                return null;
            }

            File file = new File(tmpFolder + "/" + programme + ".pdf");
            try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

            return file;
        });

    }

}

