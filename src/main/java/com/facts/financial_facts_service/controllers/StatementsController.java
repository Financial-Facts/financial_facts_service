package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.facts.financial_facts_service.datafetcher.DataFetcher;
import com.facts.financial_facts_service.datafetcher.records.Statements;
import com.facts.financial_facts_service.exceptions.InvalidRequestException;
import com.facts.financial_facts_service.services.statement.StatementService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.facts.financial_facts_service.constants.interfaces.Constants.V1_STATEMENTS;

@RestController
@Validated
@RequestMapping(path = V1_STATEMENTS)
public class StatementsController implements Constants {

    final Logger logger = LoggerFactory.getLogger(StatementsController.class);

    @Autowired
    private StatementService statementService;

    @Autowired
    private DataFetcher dataFetcher;

    @GetMapping(path = CIK_PATH_PARAM)
    public CompletableFuture<ResponseEntity<Statements>> getStatementsWithCik(
            @PathVariable @NotBlank @Pattern(regexp = CIK_REGEX) String cik) {
        logger.info("In statements controller getting statements for {}", cik);
        return dataFetcher.getStatements(cik.toUpperCase())
            .flatMap(statements -> {
                logger.info("Fetch complete for statements with cik {}", cik);
                return Mono.just(new ResponseEntity<>(statements, HttpStatus.OK));
            }).toFuture();
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<List<String>>> saveStatements(
            @Valid @RequestBody Statements statements) {
        logger.info("In statements controller saving statements {}", statements);
        checkValidStatements(statements);
        return statementService.saveStatements(statements).flatMap(message -> {
            logger.info("Save complete for statements");
            return Mono.just(new ResponseEntity<>(message, HttpStatus.OK));
        }).toFuture();
    }

    private void checkValidStatements(Statements statements) {
        if (statements.getBalanceSheets().isEmpty() && statements.getIncomeStatements().isEmpty()) {
            throw new InvalidRequestException("Bad Request: No statements provided");
        }
        if (!statements.getBalanceSheets().isEmpty()) {
            String cik = statements.getBalanceSheets().get(0).getCik();
            statements.getBalanceSheets().forEach(sheet -> {
                if (!cik.equalsIgnoreCase(sheet.getCik())) {
                    throw new InvalidRequestException("Bad Request: Balance sheets contain multiple cik");
                }
            });
        }
        if (!statements.getIncomeStatements().isEmpty()) {
            String cik = statements.getIncomeStatements().get(0).getCik();
            statements.getIncomeStatements().forEach(sheet -> {
                if (!cik.equalsIgnoreCase(sheet.getCik())) {
                    throw new InvalidRequestException("Bad Request: Income statements contain multiple cik");
                }
            });
        }
    }
}
