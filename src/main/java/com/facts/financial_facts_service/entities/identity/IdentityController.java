package com.facts.financial_facts_service.entities.identity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "v1/identity")
public class IdentityController {

    Logger logger = LoggerFactory.getLogger(IdentityController.class);

    @Autowired
    IdentityService identityService;

    @GetMapping(path = "/{cik}")
    public CompletableFuture<ResponseEntity> getSymbolWithCik(@PathVariable String cik) throws InterruptedException {
        logger.info("In identity controller getting identity for cik {}", cik);
        return identityService.getSymbolFromIdentityMap(cik.toUpperCase()).toFuture();
    }

}
