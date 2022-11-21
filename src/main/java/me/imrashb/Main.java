package me.imrashb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static int pdfCount = 0;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}