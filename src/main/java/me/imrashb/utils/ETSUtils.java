package me.imrashb.utils;

import me.imrashb.domain.Programme;
import me.imrashb.domain.Trimestre;
import me.imrashb.parser.PdfCours;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ETSUtils {

    public static Map<Programme, Future<File>> getFichiersHoraireAsync(int annee, Trimestre trimestre) {
        final Map<Programme, Future<File>> futures = new HashMap<>();

        final String idTrimestre = trimestre.getIdTrimestre(annee);

        final ExecutorService executor
                = Executors.newFixedThreadPool(Programme.values().length);

        final String tmpFolder = "./pdf";
        new File(tmpFolder).mkdir();

        for (Programme programme : Programme.values()) {
            Future<File> future = downloadHoraire(executor, tmpFolder, programme.getId(), idTrimestre);
            futures.put(programme, future);
        }

        executor.shutdownNow();

        return futures;
    }

    public static List<PdfCours> getFichiersHoraireSync(int annee, Trimestre trimestre) throws ExecutionException, InterruptedException {

        Map<Programme, Future<File>> futures = getFichiersHoraireAsync(annee, trimestre);

        List<PdfCours> files = new ArrayList<>();
        //Resolve all futures
        for(Programme programme : futures.keySet()) {
            File f = futures.get(programme).get();
            if(f != null) {
                files.add(new PdfCours(f, programme));
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

