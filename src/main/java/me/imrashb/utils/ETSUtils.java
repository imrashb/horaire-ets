package me.imrashb.utils;

import me.imrashb.domain.Programme;
import me.imrashb.domain.Session;
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

    public static Map<Programme, Future<File>> getFichiersHoraireAsync(Session session) {
        final Map<Programme, Future<File>> futures = new HashMap<>();

        final String idSession = session.toId() + "";

        final ExecutorService executor
                = Executors.newFixedThreadPool(Programme.values().length);

        final String tmpFolder = "./pdf";
        new File(tmpFolder).mkdir();

        for (Programme programme : Programme.values()) {
            Future<File> future = downloadHoraire(executor, programme.getId(), idSession);
            futures.put(programme, future);
        }

        executor.shutdownNow();

        return futures;
    }

    public static List<PdfCours> getFichiersHoraireSync(Session session) throws ExecutionException, InterruptedException {

        Map<Programme, Future<File>> futures = getFichiersHoraireAsync(session);

        List<PdfCours> files = new ArrayList<>();
        //Resolve all futures
        for (Programme programme : futures.keySet()) {
            File f = futures.get(programme).get();
            if (f != null) {
                files.add(new PdfCours(f, programme));
            } else {
                return null;
            }
        }

        return files;
    }

    private static Future<File> downloadHoraire(ExecutorService executor, String programme, String idSession) {
        final String link = "https://horaire.etsmtl.ca/HorairePublication/HorairePublication_" + idSession + "_" + programme + ".pdf";
        return downloadFile(executor, link, programme+".pdf");
    }

    private static Future<File> downloadFile(ExecutorService executor, String link, String fileName) {
        return executor.submit(() -> {
            URL url = null;
            try {
                url = new URL(link);
            } catch (MalformedURLException e) {
                System.err.println(e.getMessage());
                return null;
            }

            File file = new File("./tmp/" + fileName);
            if(!file.exists()) file.createNewFile();

            try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                fileOutputStream.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return null;
            }

            return file;
        });
    }

}

