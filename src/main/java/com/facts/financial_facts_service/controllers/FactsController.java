package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.datafetcher.DataFetcher;
import com.facts.financial_facts_service.datafetcher.records.FactsData;
import com.facts.financial_facts_service.datafetcher.records.StickerPriceData;
import com.facts.financial_facts_service.services.facts.FactsService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

import static com.facts.financial_facts_service.constants.Constants.V1_FACTS;

@RestController
@Validated
@RequestMapping(path = V1_FACTS)
public class FactsController implements Constants {

    Logger logger = LoggerFactory.getLogger(FactsController.class);

    @Autowired
    private DataFetcher dataFetcher;

    @GetMapping(path = CIK_PATH_PARAM)
    public CompletableFuture<ResponseEntity<FactsData>> getFacts(
            @PathVariable @NotBlank @Pattern(regexp = CIK_REGEX) String cik) {
        logger.info("In facts controller getting facts for {}", cik);
        return dataFetcher.getFactsWithCik(cik.toUpperCase())
            .flatMap(response -> {
                logger.info("Returning facts for {}", cik);
                return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
            }).toFuture();
    }

    @GetMapping(path = CIK_PATH_PARAM + SLASH + STICKER_PRICE_DATA)
    public CompletableFuture<ResponseEntity<StickerPriceData>> getStickerPriceData(
            @PathVariable @NotBlank @Pattern(regexp = CIK_REGEX) String cik) {
        logger.info("In facts controller getting facts for {}", cik);
        return dataFetcher.getStickerPriceDataWithCik(cik.toUpperCase())
            .flatMap(response -> {
                logger.info("Returning sticker price data for {}", cik);
                return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
            }).toFuture();
    }
}
