package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.facts.Facts;
import com.facts.financial_facts_service.services.FactsService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

import static com.facts.financial_facts_service.constants.Constants.V1_FACTS;

@RestController
@Validated
@RequestMapping(path = V1_FACTS)
public class FactsController implements Constants {

    Logger logger = LoggerFactory.getLogger(FactsController.class);

    private final FactsService factsService;

    public FactsController (FactsService factsService) { this.factsService = factsService; }

    @GetMapping(path = CIK_PATH_PARAM)
    public CompletableFuture<ResponseEntity<String>> getFacts(@PathVariable @NotBlank @Pattern(regexp = CIK_REGEX) String cik) {
        logger.info("In facts controller getting facts for cik {}", cik);
        return factsService.getFactsByCik(cik.toUpperCase()).toFuture();
    }
}
