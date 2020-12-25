package com.gosh.bingo90.domain;

import lombok.*;

import java.util.concurrent.atomic.AtomicLong;

@Data
@AllArgsConstructor
@Builder
public class BingoStrip {

    private static AtomicLong atomicInteger = new AtomicLong(0);
    Long stripId;
    int[][] strip;

    public BingoStrip(){
        this.stripId= atomicInteger.incrementAndGet();
    }

    public BingoStrip(int[][] strip){
        this.stripId= atomicInteger.incrementAndGet();
        this.strip = strip;
    }

}
