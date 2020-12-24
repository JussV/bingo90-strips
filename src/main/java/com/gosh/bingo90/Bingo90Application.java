package com.gosh.bingo90;

import com.gosh.bingo90.service.BingoStripGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;

@SpringBootApplication
public class Bingo90Application {

    public static void main(String[] args) {

        SpringApplication.run(Bingo90Application.class, args);
        long rndSeed = Instant.now().toEpochMilli();
        System.out.println(Instant.now());
        for (int i = 0; i < 1; i++) {
            new BingoStripGenerator(rndSeed);
            rndSeed++;
        }
        System.out.println(Instant.now());
    }

}
