package com.gosh.bingo90.service;

import com.gosh.bingo90.domain.BingoStrip;
import lombok.extern.slf4j.Slf4j;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class BingoStripGeneratorTest {

    private static BingoStrip bingoStrip;
    private static Map<Integer, List<int[]>> tickets;
    private static final int EXPECTED_TOTAL_NUMBERS = 90;
    private static final int EXPECTED_TOTAL_NUMBERS_PER_ROW = 5;
    private static final int EXPECTED_TOTAL_NUMBERS_PER_TICKET = 15;
    private static final int EXPECTED_NUMBER_OF_TICKETS = 6;
    private static final int EXPECTED_ROWS_PER_TICKET = 3;
    private static final int MAX_NUMBERS_PER_TICKET_COLUMN = 3;
    private static final int MIN_NUMBERS_PER_TICKET_COLUMN = 1;
    private static final Map<Integer, Integer> EXPECTED_COLUMN_NUMBERS_UP_TO = Map.of(
            0,10,
            1,20,
            2,30,
            3,40,
            4,50,
            5,60,
            6,70,
            7,80,
            8,91 // 90 is inclusive in this list
    );

    @BeforeAll
    public static void generateBingoStrip() {
        BingoStripGenerator bingoStripGenerator = new BingoStripGenerator();
        bingoStrip = bingoStripGenerator.generateStrip();
        AtomicInteger index = new AtomicInteger(0);
        tickets = Arrays.stream(bingoStrip.getStrip())
                .collect(Collectors.groupingBy(row -> List.of(0,1,2,3,4,5).get(index.getAndIncrement() / 3)));
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

        rowNumbers.forEach(row -> assertEquals(EXPECTED_TOTAL_NUMBERS_PER_ROW, row.size()));
    }

    @Test
    void stripConsistsOf6TicketsWith3Rows() {
        assertEquals(EXPECTED_NUMBER_OF_TICKETS, tickets.entrySet().size());
        tickets.forEach((ticketId, ticketRows) -> assertEquals(EXPECTED_ROWS_PER_TICKET, ticketRows.size()));
    }

    @Test
    void ticketConsistOf15Numbers() {
        tickets.forEach((ticketId, ticket) -> {
            List<Integer> ticketNumbers = ticket
                .stream()
                .flatMapToInt(Arrays::stream)
                .boxed()
                .filter(x -> x > 0)
                .collect(Collectors.toList());
            assertEquals(EXPECTED_TOTAL_NUMBERS_PER_TICKET, ticketNumbers.size());
        });
    }

    @Test
    void ticketColumnContainsMin1Max3AndIsWithinColumnRange() {
        tickets.forEach((ticketId, ticket) -> {
            IntStream.range(0, 9).boxed().forEach(columnId -> {
                List<Integer> columnNumbers = ticket.stream().map(row -> row[columnId]).collect(Collectors.toList());
                long totalNumbersPerColumn = columnNumbers.stream().filter(n -> n > 0).count();
                assertTrue(totalNumbersPerColumn <= MAX_NUMBERS_PER_TICKET_COLUMN);
                assertTrue(totalNumbersPerColumn >= MIN_NUMBERS_PER_TICKET_COLUMN);
                columnNumbers.forEach(colNum -> assertTrue(colNum < EXPECTED_COLUMN_NUMBERS_UP_TO.get(columnId)));
            });
        });
    }

    @Test
    void generate10kBingoStripsInLessThan1s() {
        Instant startTime = Instant.now();
        log.info("Start time: " + startTime);
        for (int i = 0; i < 10000; i++) {
            new BingoStripGenerator();
        }
        Instant endTime = Instant.now();
        log.info("End time:" + endTime);
        assertTrue(endTime.minusMillis(startTime.toEpochMilli()).getEpochSecond() < 1);
    }

}
