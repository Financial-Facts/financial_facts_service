package com.facts.financial_facts_service.services.statement;

import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.facts.financial_facts_service.entities.balanceSheet.BalanceSheet;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.entities.incomeStatement.IncomeStatement;
import com.facts.financial_facts_service.exceptions.InvalidRequestException;
import com.facts.financial_facts_service.repositories.BalanceSheetRepository;
import com.facts.financial_facts_service.repositories.IncomeStatementRepository;
import com.facts.financial_facts_service.repositories.projections.StatementKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class StatementService implements Constants {

    final Logger logger = LoggerFactory.getLogger(StatementService.class);

    private final int SAVE_BATCH_CAPACITY = 50;

    @Autowired
    private BalanceSheetRepository balanceSheetRepository;

    @Autowired
    private IncomeStatementRepository incomeStatementRepository;

    public Mono<List<BalanceSheet>> getQuarterlyBalanceSheets(String cik) {
        logger.info("In statement service getting quarterly balance sheets for {}", cik);
        try {
            return Mono.just(balanceSheetRepository.findAllByCik(cik));
        } catch (DataAccessException ex) {
            logger.error("Error occurred while getting quarterly balance sheets for {}", cik);
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    public Mono<List<IncomeStatement>> getQuarterlyIncomeStatements(String cik) {
        logger.info("In statement service getting quarterly income statements for {}", cik);
        try {
            return Mono.just(incomeStatementRepository.findAllByCik(cik));
        } catch (DataAccessException ex) {
            logger.error("Error occurred while getting quarterly income statements for {}", cik);
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    public Mono<String> saveQuarterlyBalanceSheets(List<BalanceSheet> balanceSheets) {
        String cik = balanceSheets.get(0).getCik();
        logger.info("In statement service saving balance sheets for {}", cik);
        try {
            Set<StatementKey> existingBalanceSheets = balanceSheetRepository.getAllBalanceSheetKeysForCik(cik);
            List<BalanceSheet> newBalanceSheets = balanceSheets.stream().filter(sheet ->
                existingBalanceSheets.stream().noneMatch(existingSheet ->
                    existingSheet.getCik().equals(sheet.getCik()) &&
                    existingSheet.getDate().isEqual(sheet.getDate()))).toList();

            if (newBalanceSheets.isEmpty()) {
                return Mono.just("All balance sheets already exist");
            }

            return Mono.fromSupplier(() -> {
                int i = 0;
                List<LocalDate> savedDates = new ArrayList<>();
                while (i < newBalanceSheets.size()) {
                    int value = Math.min(i + SAVE_BATCH_CAPACITY, newBalanceSheets.size());
                    List<BalanceSheet> batch = newBalanceSheets.subList(i, value);
                    balanceSheetRepository.saveAllAndFlush(batch);
                    savedDates.addAll(batch.stream().map(BalanceSheet::getDate).toList());
                    i += SAVE_BATCH_CAPACITY;
                }
                return savedDates;
            }).flatMap(savedDates -> Mono.just(BALANCE_SHEET_SAVED + " - " + savedDates));
        } catch (DataAccessException ex) {
            logger.error("Error occurred while saving quarterly balance sheets for {}", cik);
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    public Mono<String> saveQuarterlyIncomeStatements(List<IncomeStatement> incomeStatements) {
        String cik = incomeStatements.get(0).getCik();
        logger.info("In statement service saving income statements for {}", cik);
        try {
            Set<StatementKey> existingIncomeStatements = incomeStatementRepository.getAllIncomeStatementKeysForCik(cik);
            List<IncomeStatement> newIncomeStatements  = incomeStatements.stream().filter(statement ->
                existingIncomeStatements.stream().noneMatch(existingStatement ->
                    existingStatement.getCik().equals(statement.getCik()) &&
                    existingStatement.getDate().isEqual(statement.getDate()))).toList();

            if (newIncomeStatements.isEmpty()) {
                return Mono.just("All income statements already exist");
            }

            return Mono.fromSupplier(() -> {
                int i = 0;
                List<LocalDate> savedDates = new ArrayList<>();
                while (i < newIncomeStatements.size()) {
                    int value = Math.min(i + SAVE_BATCH_CAPACITY, newIncomeStatements.size());
                    List<IncomeStatement> batch = newIncomeStatements.subList(i, value);
                    incomeStatementRepository.saveAllAndFlush(batch);
                    savedDates.addAll(batch.stream().map(IncomeStatement::getDate).toList());
                    i += SAVE_BATCH_CAPACITY;
                }
                return savedDates;
            }).flatMap(savedDates -> Mono.just(INCOME_STATEMENTS_SAVED + " - " + savedDates))
            .onErrorResume(ex -> {
                logger.error("Error occurred while saving quarterly income statements for {}", cik);
                throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
            });
        } catch (DataAccessException ex) {
            logger.error("Error occurred while saving quarterly income statements for {}", cik);
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

}
