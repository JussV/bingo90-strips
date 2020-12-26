package com.gosh.bingo90.web.rest;

import com.gosh.bingo90.domain.BingoStrip;
import com.gosh.bingo90.service.BingoStripGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class BingoStripResource {

    private final BingoStripGenerator bingoStripGenerator;

    public BingoStripResource(BingoStripGenerator bingoStripGenerator) {
        this.bingoStripGenerator = bingoStripGenerator;
    }

    /**
     * GET  /bingostrips : generate bingo strips
     *
     * @param count - how many strips to generate
     * @return the ResponseEntity with status 200 (OK) and the list of bingo strips in the body
     */
    @GetMapping("/bingostrips")
    public ResponseEntity<List<BingoStrip>> get(@RequestParam Long count) {
        log.info("REST request to generate {} bingoStrips", count);
        return new ResponseEntity<>(this.bingoStripGenerator.generateStrips(count), HttpStatus.OK);
    }

}
