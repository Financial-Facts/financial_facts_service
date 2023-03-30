package com.facts.financial_facts_service.entities.cikMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "v1/mapping")
public class CikMappingController {

    @Autowired
    CikMappingService cikMappingService;

    @GetMapping(path = "/{cik}")
    public CompletableFuture<ResponseEntity> getSymbolWithCik(@PathVariable String cik) {
        return cikMappingService.getSymbolWithCik(cik.toUpperCase()).toFuture();
    }

    @PostMapping()
    public CompletableFuture<ResponseEntity> setCikMapping(@RequestBody CikMapping mapping) {
        return null;
    }
}
