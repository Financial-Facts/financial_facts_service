package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.services.IdentityService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static com.facts.financial_facts_service.constants.Constants.V1_IDENTITY;

@RestController
@Validated
@RequestMapping(path = V1_IDENTITY)
public class IdentityController implements Constants {

    Logger logger = LoggerFactory.getLogger(IdentityController.class);

    private final IdentityService identityService;

    public IdentityController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @GetMapping(path = CIK_PATH_PARAM)
    public CompletableFuture<ResponseEntity<Identity>> getIdentityWithCik(@PathVariable @NotBlank @Pattern(regexp = CIK_REGEX) String cik) {
        logger.info("In identity controller getting identity for cik {}", cik);
        return identityService.getIdentityFromIdentityMap(cik.toUpperCase()).toFuture();
    }

}
