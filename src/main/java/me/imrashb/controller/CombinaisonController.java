package me.imrashb.controller;

import me.imrashb.domain.ParametresCombinaison;
import me.imrashb.domain.combinaison.CombinaisonHoraire;
import me.imrashb.domain.combinaison.comparator.CombinaisonHoraireComparator;
import me.imrashb.service.CombinaisonService;
import me.imrashb.utils.HoraireImageMaker;
import me.imrashb.utils.HoraireImageMakerTheme;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/combinaisons")
public class CombinaisonController {

    private static final int MAX_IDS = 64;
    private final CombinaisonService service;

    public CombinaisonController(CombinaisonService service) {
        this.service = service;
    }

    @GetMapping("")
    public List<CombinaisonHoraire> getCombinaisonsHoraire(ParametresCombinaison parametres) throws RuntimeException {

        CombinaisonHoraireComparator comparator = null;

        if (parametres.getSort() != null && parametres.getSort().size() > 0) {
            CombinaisonHoraireComparator.Builder builder = new CombinaisonHoraireComparator.Builder();
            for (CombinaisonHoraireComparator.Comparator c : parametres.getSort()) {
                try {
                    builder.addComparator(c.getComparatorClass());
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            comparator = builder.build();
        }


        List<CombinaisonHoraire> combinaisons = service.getCombinaisonsHoraire(parametres);

        if (comparator != null) Collections.sort(combinaisons, comparator);

        return combinaisons;
    }

    @GetMapping("sort")
    public CombinaisonHoraireComparator.Comparator[] getCombinaisonHoraireSorters() {
        return service.getAvailableCombinaisonHoraireComparators();
    }

    @GetMapping(value = "{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getCombinaisonImage(@PathVariable String id, @RequestParam(required = false) String theme) throws ExecutionException, InterruptedException, IOException {

        HoraireImageMakerTheme imageTheme = HoraireImageMaker.getThemeFromId(theme);

        CombinaisonHoraire comb = service.getCombinaisonFromEncodedId(id);
        Future<Image> future = new HoraireImageMaker(comb, imageTheme).drawHoraire();
        Image img = future.get();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage) img, "jpeg", os);
        return os.toByteArray();
    }

    @GetMapping(value = "id")
    public List<CombinaisonHoraire> getCombinaisonsFromEncodedId(@RequestParam String[] ids) throws ExecutionException, InterruptedException, IOException {

        List<CombinaisonHoraire> combinaisons = new ArrayList<>();

        for (String id : ids) {
            try {
                CombinaisonHoraire comb = service.getCombinaisonFromEncodedId(id);
                combinaisons.add(comb);
            } catch (Exception ex) {
                // Ignore
            }
        }

        return combinaisons;
    }

}
