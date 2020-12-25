package com.gosh.bingo90.service;

import com.gosh.bingo90.domain.BingoStrip;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BingoStripGeneratorTest {

    private static BingoStrip bingoStrip;
    private static final int EXPECTED_TOTAL_NUMBERS = 90;
    private static final int EXPECTED_TOTAL_NUMBERS_PER_ROW = 5;
    private static final int EXPECTED_TOTAL_NUMBERS_PER_TICKET = 15;
    private static final int EXPECTED_NUMBER_OF_TICKETS = 6;
    private static final int EXPECTED_ROWS_PER_TICKET = 3;

    @BeforeAll
    public static void generateBingoStrip() {
        BingoStripGenerator bingoStripGenerator = new BingoStripGenerator(Instant.now().toEpochMilli());
        bingoStrip = bingoStripGenerator.generateStrip();
    }

    @Test
    void stripConsistsOfNumbers1to90() {
        List<Integer> bingoStripNumbers = Arrays.stream(bingoStrip.getStrip())
                .flatMapToInt(Arrays::stream)
                .filter(n -> n > 0)
                .sorted()
                .boxed()
                .collect(Collectors.toList());

        assertEquals(EXPECTED_TOTAL_NUMBERS, bingoStripNumbers.size());
        assertEquals(IntStream.rangeClosed(1, EXPECTED_TOTAL_NUMBERS).boxed().collect(Collectors.toList()), bingoStripNumbers);
    }

    @Test
    void rowConsistsOf5Numbers() {
        List<List<Integer>> rowNumbers = Arrays.stream(bingoStrip.getStrip())
                .map(arr -> Arrays.stream(arr).filter(x -> x > 0).boxed().collect(Collectors.toList()))
                .collect(Collectors.toList());

        rowNumbers.forEach(row -> {
            assertEquals(EXPECTED_TOTAL_NUMBERS_PER_ROW, row.size());
        });
    }

    @Test
    void stripConsistsOf6TicketsWith3Rows() {
        AtomicInteger index = new AtomicInteger(0);
        Map<Integer, List<int[]>> tickets = Arrays.stream(bingoStrip.getStrip())
            .collect(Collectors.groupingBy(row -> List.of(0,1,2,3,4,5).get(index.getAndIncrement() / 3)));
        assertEquals(EXPECTED_NUMBER_OF_TICKETS, tickets.entrySet().size());
        tickets.forEach((ticketId, ticketRows) -> {
            assertEquals(EXPECTED_ROWS_PER_TICKET, ticketRows.size());
        });
    }

    @Test
    void ticketConsistOf15Numbers() {
        AtomicInteger index = new AtomicInteger(0);
        Map<Integer, List<int[]>> tickets = Arrays.stream(bingoStrip.getStrip())
            .collect(Collectors.groupingBy(row -> List.of(0,1,2,3,4,5).get(index.getAndIncrement() / 3)));
        tickets.forEach((key, value) -> {
            List<Integer> ticketNumbers = value
                .stream()
                .flatMapToInt(Arrays::stream)
                .boxed()
                .filter(x -> x > 0)
                .collect(Collectors.toList());
            assertEquals(EXPECTED_TOTAL_NUMBERS_PER_TICKET, ticketNumbers.size());
        });
    }



}
