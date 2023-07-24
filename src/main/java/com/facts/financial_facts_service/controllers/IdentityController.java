package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.facts.financial_facts_service.datafetcher.DataFetcher;
import com.facts.financial_facts_service.datafetcher.records.IdentitiesAndDiscounts;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.entities.identity.models.BulkIdentitiesRequest;
import com.facts.financial_facts_service.services.identity.IdentityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.facts.financial_facts_service.constants.interfaces.Constants.V1_IDENTITY;

@RestController
@Validated
@RequestMapping(path = V1_IDENTITY)
public class IdentityController implements Constants {

    final Logger logger = LoggerFactory.getLogger(IdentityController.class);

    @Autowired
    private DataFetcher dataFetcher;

    @Autowired
    private IdentityService identityService;

    @GetMapping(path = CIK_PATH_PARAM)
    public CompletableFuture<ResponseEntity<Identity>> getIdentityWithCik(@PathVariable @NotBlank @Pattern(regexp = CIK_REGEX) String cik) {
        logger.info("In identity controller getting identity for cik {}", cik);
        return identityService.getIdentityFromIdentityMap(cik.toUpperCase())
                .flatMap(identity -> Mono.just(new ResponseEntity<>(identity, HttpStatus.OK))).toFuture();
    }

    @PostMapping(path = BULK)
    public CompletableFuture<ResponseEntity<IdentitiesAndDiscounts>> getBulkIdentitiesAndOptionalDiscounts(
            @Valid @RequestBody BulkIdentitiesRequest request,
            @RequestParam(required = false) Boolean includeDiscounts) {
        logger.info("In identity controller getting bulk identities {}", request);
        includeDiscounts = Objects.nonNull(includeDiscounts) && includeDiscounts;
        return dataFetcher.getIdentitiesAndOptionalDiscounts(request, includeDiscounts)
            .flatMap(identities -> {
                logger.info("Returning bulk entities for {}", request);
                return Mono.just(new ResponseEntity<>(identities, HttpStatus.OK));
            }).toFuture();
    }
}
