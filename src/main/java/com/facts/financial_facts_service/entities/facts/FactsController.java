package com.facts.financial_facts_service.entities.facts;

import com.facts.financial_facts_service.constants.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@Validated
@RequestMapping(path = Constants.V1_FACTS)
public class FactsController {

    Logger logger = LoggerFactory.getLogger(FactsController.class);

    private final FactsService factsService;

    public FactsController (FactsService factsService) {
        this.factsService = factsService;
    }

    @GetMapping(path = Constants.CIK_PATH_PARAM)
    public CompletableFuture<ResponseEntity<Facts>> getFacts(@PathVariable @NotBlank @Pattern(regexp = Constants.CIK_REGEX) String cik) {
        logger.info("In facts controller getting facts for cik {}", cik);
        return factsService.getFactsByCik(cik.toUpperCase()).toFuture();
    }
}
