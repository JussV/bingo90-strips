package com.gosh.bingo90.service;

import com.gosh.bingo90.Constants;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BingoStripGenerator {

    private int[][] bingoStrip = new int[Constants.BINGO_STRIP_ROWS][Constants.BINGO_STRIP_COLUMNS];
    private Random random;
    private long rndSeed;

    public BingoStripGenerator(long rndSeed) {
        this.rndSeed = rndSeed;
        this.random = new Random(rndSeed);
        this.generateStrip();
    }

    private void generateStrip() {
        // counts numbers added per row
        int[] rowNumbersCounter = new int[Constants.BINGO_STRIP_ROWS];

        // full list of bingo 90 numbers
        List<Integer> fullNumberList = IntStream.rangeClosed(1, Constants.BINGO_90_TOTAL_NUMBERS)
                .boxed().collect(Collectors.toList());
        
        // initialize the list to store lists of numbers per column
        List<List<Integer>> columnNumberLists = new ArrayList<>();
        for (int i = 0; i < Constants.BINGO_STRIP_COLUMNS; i++) {
            columnNumberLists.add(new ArrayList<>());
        }

        // distribute numbers per column depending on the column index
        fullNumberList.forEach(number -> {
            int columnId = this.getColumnIndex(number);
            columnNumberLists.get(columnId).add(number);
        });

        // shuffle column lists
        columnNumberLists.forEach(l -> Collections.shuffle(l, random));

        // satisfy min requirements to have at least one number in ticket's column
        for (int colId = 0; colId < Constants.BINGO_STRIP_COLUMNS; colId++) {
            for (int ticketId = 0; ticketId < Constants.BINGO_STRIP_TICKETS; ticketId++) {
                int randomTicketRowIndex;
                int rowId;
                do {
                    randomTicketRowIndex = random.nextInt(Constants.BINGO_STRIP_TICKET_ROWS);
                    rowId = ticketId * Constants.BINGO_STRIP_TICKET_ROWS + randomTicketRowIndex;
                } while (rowNumbersCounter[rowId] >= Constants.BINGO_STRIP_ROW_MAX_NUMBERS);
                bingoStrip[rowId][colId] = columnNumberLists.get(colId).get(0);
                rowNumbersCounter[rowId]++;
                fullNumberList.remove(columnNumberLists.get(colId).get(0));
                int finalRowId = rowId;
                int finalColId = colId;
                columnNumberLists.get(colId).removeIf(number -> number == bingoStrip[finalRowId][finalColId]);
            }
        }
        Collections.shuffle(fullNumberList, random);
        fullNumberList.forEach(number -> {
            int columnId = getColumnIndex(number);
            IntStream.range(0, Constants.BINGO_STRIP_ROWS).boxed()
                .filter(rowId -> bingoStrip[rowId][columnId] == 0 && rowNumbersCounter[rowId] < Constants.BINGO_STRIP_ROW_MAX_NUMBERS)
                .findFirst()
                .ifPresentOrElse(
                    rowId -> {
                        bingoStrip[rowId][columnId] = number;
                        rowNumbersCounter[rowId]++;
                    }, () -> {
                        // find first row that has less than max items
                        int rowIdForNumberToBeAssigned = IntStream.range(0, rowNumbersCounter.length).boxed()
                            .sorted(Collections.reverseOrder())
                            .filter(rowId -> rowNumbersCounter[rowId] < Constants.BINGO_STRIP_ROW_MAX_NUMBERS)
                            .findFirst().orElse(-1);
                        swapWithNumberFromDifferentRow(rowNumbersCounter, rowIdForNumberToBeAssigned, number, columnId);
                    });
        });
        this.sortTicketColumnsExcludingZeros();
        this.stripeToString();
    }

    /**
     * returns column index where the number should be placed in the bingo strip
     * e.g. for 34 returns index 3, for 90 returns index 8
     *
     * @param number - bingo number
     * @return column index
     */
    private int getColumnIndex(int number) {
        return Math.min(number/10, 8);
    }

    /**
     * Add a swapped item in the rowIdForNumberToBeAssigned
     * numberToBeAssigned is placed in the first available row (from the bottom) that does not have a value in the desired column id
     * and another value from that row, but from a different column is taken and placed in the rowIdForNumberToBeAssigned
     *
     * @param rowNumbersCounter - counter of numbers per row
     * @param rowIdForNumberToBeAssigned - row that has less than max allowed items and that can take another value
     * @param number to be assigned in a row
     * @param columnId where the number should be placed
     */
    private void swapWithNumberFromDifferentRow(int[] rowNumbersCounter, int rowIdForNumberToBeAssigned, int number, int columnId) {
        AtomicBoolean isNumberAssignedToRow = new AtomicBoolean(false);
        IntStream.rangeClosed(0, rowNumbersCounter.length - 1).boxed()
                .sorted(Collections.reverseOrder())
                .peek(rowId -> IntStream.range(0, Constants.BINGO_STRIP_COLUMNS).boxed()
                        .filter(colId -> rowNumbersCounter[rowId] == Constants.BINGO_STRIP_ROW_MAX_NUMBERS
                                && bingoStrip[rowId][colId] != 0 && bingoStrip[rowId][columnId] == 0
                                && bingoStrip[rowIdForNumberToBeAssigned][colId] == 0)
                        .findFirst().ifPresent(colId -> {
                            bingoStrip[rowId][columnId] = number;
                            bingoStrip[rowIdForNumberToBeAssigned][colId] = bingoStrip[rowId][colId];
                            bingoStrip[rowId][colId] = 0;
                            rowNumbersCounter[rowIdForNumberToBeAssigned]++;
                            isNumberAssignedToRow.set(true);
                        }))
                .anyMatch(n -> isNumberAssignedToRow.get());
    }

    /**
     * sort ticket columns in ascending order (0s are excluded from the sort)
     */
    private void sortTicketColumnsExcludingZeros() {
        IntStream.range(0, Constants.BINGO_STRIP_TICKETS).boxed()
            .forEach(stripId -> IntStream.range(0, Constants.BINGO_STRIP_COLUMNS).boxed()
                .forEach(rowId -> {
                    List<Integer> stripColumn = Arrays.asList(bingoStrip[Constants.BINGO_STRIP_TICKET_ROWS * stripId][rowId],
                        bingoStrip[Constants.BINGO_STRIP_TICKET_ROWS * stripId + 1][rowId],
                        bingoStrip[Constants.BINGO_STRIP_TICKET_ROWS * stripId + 2][rowId]);
                    stripColumn.sort((o1, o2) -> {
                        if (o1 == 0 || o2 == 0 || o1.equals(o2)) return 0;
                        if (o1 < o2) return -1;
                        return 1;
                    });
                    if (stripColumn.stream().filter(x->x==0).count() == 1 && stripColumn.get(1) == 0 &&
                            stripColumn.get(0) > stripColumn.get(2)) {
                        Collections.swap(stripColumn, 0, 2);
                    }
                    bingoStrip[Constants.BINGO_STRIP_TICKET_ROWS * stripId][rowId] = stripColumn.get(0);
                    bingoStrip[Constants.BINGO_STRIP_TICKET_ROWS * stripId + 1][rowId] = stripColumn.get(1);
                    bingoStrip[Constants.BINGO_STRIP_TICKET_ROWS * stripId + 2][rowId] = stripColumn.get(2);
                }));
    }

    /**
     * Print bingo stripe
     */
    public void stripeToString() {
        IntStream.range(0, bingoStrip.length).boxed()
            .forEach(i -> {
                IntStream.range(0, bingoStrip[0].length)
                    .boxed()
                    .forEach(j -> System.out.print(bingoStrip[i][j] + ","));
                    System.out.println();
                });
    }
}
