package com.gosh.bingo90.web.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class BingoStripControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BingoStripsCounterNotInRangeException.class)
    public ResponseEntity<?> handleException(BingoStripsCounterNotInRangeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    protected ResponseEntity<?> handleMethodArgumentNotValid(NumberFormatException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request parameter could not be parsed to long");
    }
}
