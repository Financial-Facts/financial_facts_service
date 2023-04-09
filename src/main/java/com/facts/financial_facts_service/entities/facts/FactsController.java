package com.facts.financial_facts_service.entities.facts;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.serverResponse.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = Constants.V1_FACTS)
public class FactsController {

    Logger logger = LoggerFactory.getLogger(FactsController.class);

    @Autowired
    private FactsService factsService;

    @GetMapping(path = Constants.CIK_PATH_PARAM)
    public CompletableFuture<ServerResponse> getFacts(@PathVariable String cik) {
        logger.info("In facts controller getting facts for cik {}", cik);
        return factsService
                .getFactsByCik(cik.toUpperCase())
                .toFuture();
    }
}
