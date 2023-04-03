package com.facts.financial_facts_service.entities.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "v1/mapping")
public class IdentityController {

    @Autowired
    IdentityService identityService;

    @GetMapping(path = "/{cik}")
    public CompletableFuture<ResponseEntity> getSymbolWithCik(@PathVariable String cik) throws InterruptedException {
        return identityService.getSymbolFromIdentityMap(cik.toUpperCase()).toFuture();
    }

}
