package me.imrashb.parser;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.imrashb.domain.Programme;

import java.io.File;

@Data
@AllArgsConstructor
public class PdfCours {

    private File pdf;
    private Programme programme;

}
