package com.gosh.bingo90.web.rest.exceptions;

import com.gosh.bingo90.utils.Constants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BingoStripsCounterNotInRangeException extends RuntimeException{

    public BingoStripsCounterNotInRangeException(long count) {
        super(String.format("Generated %s bingo strips at once, limit is %s", count, Constants.MAX_BINGO_STRIPS_GENERATED_AT_ONCE));
        log.error("Generated {} bingo strips at once, limit is {}", count, Constants.MAX_BINGO_STRIPS_GENERATED_AT_ONCE);
    }

}
