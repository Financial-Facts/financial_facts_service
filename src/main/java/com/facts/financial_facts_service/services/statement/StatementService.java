package com.facts.financial_facts_service.services.statement;

import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.facts.financial_facts_service.datafetcher.records.Statements;
import com.facts.financial_facts_service.entities.statements.Statement;
import com.facts.financial_facts_service.entities.statements.models.BalanceSheet;
import com.facts.financial_facts_service.entities.statements.models.IncomeStatement;
import com.facts.financial_facts_service.exceptions.InsufficientDataException;
import com.facts.financial_facts_service.repositories.BalanceSheetRepository;
import com.facts.financial_facts_service.repositories.IncomeStatementRepository;
import com.facts.financial_facts_service.repositories.projections.StatementKeyProjection;
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
import java.util.Objects;
import java.util.Set;

@Service
public class StatementService implements Constants {

    final Logger logger = LoggerFactory.getLogger(StatementService.class);

    private final int SAVE_BATCH_CAPACITY = 50;

    @Autowired
    private BalanceSheetRepository balanceSheetRepository;

    @Autowired
    private IncomeStatementRepository incomeStatementRepository;

    public Mono<Statements> getQuarterlyStatements(String cik) {
        return Mono.zip(
            getQuarterlyIncomeStatements(cik),
            getQuarterlyBalanceSheets(cik)
        ).flatMap(tuples -> {
            logger.info("In statement service returning statements from DB for {}", cik);
            return Mono.just(new Statements(tuples.getT1(), tuples.getT2()));
        });
    }

    public Mono<List<String>> saveStatements(Statements statements) {
        Mono<String> balanceSheetsSave = statements.getBalanceSheets().isEmpty()
            ? Mono.just(BALANCE_SHEET_SAVED)
            : saveStatementList(statements.getBalanceSheets(), BalanceSheet.class);

        Mono<String> incomeStatementsSave = statements.getIncomeStatements().isEmpty()
            ? Mono.just(INCOME_STATEMENTS_SAVED)
            : saveStatementList(statements.getIncomeStatements(), IncomeStatement.class);

        return Mono.zip(balanceSheetsSave, incomeStatementsSave)
            .flatMap(tuples -> Mono.just(List.of(tuples.getT1(), tuples.getT2())));
    }

    public void filterStatementsToTrailingElevenYears(String cik, Statements statements) {
        statements.setIncomeStatements(filterToLastElevenFY(cik, statements.getIncomeStatements()));
        statements.setBalanceSheets(filterToLastElevenFY(cik, statements.getBalanceSheets()));
    }

    public void verifyNoMissingQuarters(String cik, Statements statements) {
        checkConsecutive(cik, statements.getIncomeStatements());
        checkConsecutive(cik, statements.getBalanceSheets());
    }

    private Mono<List<BalanceSheet>> getQuarterlyBalanceSheets(String cik) {
        logger.info("In statement service getting quarterly balance sheets for {}", cik);
        try {
            return Mono.just(balanceSheetRepository.findAllByCikOrderByDateAsc(cik));
        } catch (DataAccessException ex) {
            logger.error("Error occurred while getting quarterly balance sheets for {}", cik);
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    private Mono<List<IncomeStatement>> getQuarterlyIncomeStatements(String cik) {
        logger.info("In statement service getting quarterly income statements for {}", cik);
        try {
            return Mono.just(incomeStatementRepository.findAllByCikOrderByDateAsc(cik));
        } catch (DataAccessException ex) {
            logger.error("Error occurred while getting quarterly income statements for {}", cik);
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    private <T extends Statement> Set<StatementKeyProjection> getExistingStatementKeys(String cik,
                                                                                       Class<T> type) {
        if (type.equals(BalanceSheet.class)) {
            return balanceSheetRepository.getAllBalanceSheetKeysForCik(cik);
        }
        return incomeStatementRepository.getAllIncomeStatementKeysForCik(cik);
    }

    private <T extends Statement> void filterStatements(List<T> statements,
                                                        Set<StatementKeyProjection> existingKeys,
                                                        Class<T> type) {
        filterOutInvalidStatements(statements, type);
        filterOutExistingStatements(statements, existingKeys);
    }

    private <T extends Statement> void filterOutExistingStatements(List<T> statements,
                                                                   Set<StatementKeyProjection> existingKeys) {
        List<T> newStatements = statements.stream().filter(sheet ->
                existingKeys.stream()
                        .noneMatch(existingKey ->
                                existingKey.getCik().equals(sheet.getCik()) &&
                                        existingKey.getDate().isEqual(sheet.getDate()))).toList();
        statements.retainAll(newStatements);
    }

    @SuppressWarnings("unchecked")
    private <T extends Statement> void filterOutInvalidStatements(List<T> statements, Class<T> type) {
        if (type.equals(BalanceSheet.class)) {
            filterInvalidBalanceSheets((List<BalanceSheet>) statements);
        } else {
            filterInvalidIncomeStatements((List<IncomeStatement>) statements);
        }
    }

    private void filterInvalidBalanceSheets(List<BalanceSheet> balanceSheets) {
        List<BalanceSheet> filteredBalanceSheets = balanceSheets
                .stream()
                .filter(balanceSheet -> {
                    if (Objects.nonNull(balanceSheet.getTotalStockholdersEquity()) &&
                            Objects.nonNull(balanceSheet.getLongTermDebt())) {
                        return true;
                    }
                    logger.info("Filtered out balance sheet {}, {} for {} from being saved",
                            balanceSheet.getPeriod(), balanceSheet.getDate(), balanceSheet.getCik());
                    return false;
                }).toList();
        balanceSheets.retainAll(filteredBalanceSheets);
    }

    private void filterInvalidIncomeStatements(List<IncomeStatement> incomeStatements) {
        List<IncomeStatement> filteredIncomeStatements = incomeStatements
                .stream()
                .filter(incomeStatement -> {
                    if (Objects.nonNull(incomeStatement.getEps()) &&
                            Objects.nonNull(incomeStatement.getNetIncome()) &&
                            Objects.nonNull(incomeStatement.getWeightedAverageShsOut())) {
                        return true;
                    }
                    logger.info("Filtered out income statement {}, {} for {} from being saved",
                            incomeStatement.getPeriod(), incomeStatement.getDate(), incomeStatement.getCik());
                    return false;
                }).toList();
        incomeStatements.retainAll(filteredIncomeStatements);
    }

    private <T extends Statement> Mono<String> saveStatementsInBatches(List<T> statements,
                                                                       Class<T> type) {
        String cik = statements.get(0).getCik();
        logger.info("Saving quarters {} for {}", statements.stream().map(Statement::getPeriod).toList(),
                cik);
        return Mono.fromSupplier(() -> {
                int i = 0;
                List<LocalDate> savedDates = new ArrayList<>();
                while (i < statements.size()) {
                    int value = Math.min(i + SAVE_BATCH_CAPACITY, statements.size());
                    List<T> batch = statements.subList(i, value);
                    saveStatementBatch(batch, type);
                    savedDates.addAll(batch.stream().map(Statement::getDate).toList());
                    i += SAVE_BATCH_CAPACITY;
                }
                return savedDates;
            }).flatMap(savedDates -> {
                String message = type.equals(BalanceSheet.class)
                        ? BALANCE_SHEET_SAVED
                        : INCOME_STATEMENTS_SAVED;
                return Mono.just(message + " - " + savedDates);
            }).onErrorResume(ex -> {
                logger.error("Error occurred while saving quarterly statements for {}", cik);
                throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
            });
    }

    @SuppressWarnings("unchecked")
    private <T extends Statement> void saveStatementBatch(List<T> batch, Class<T> type) {
        if (type.equals(BalanceSheet.class)) {
            balanceSheetRepository.saveAllAndFlush((List<BalanceSheet>) batch);
        } else {
            incomeStatementRepository.saveAllAndFlush((List<IncomeStatement>) batch);
        }
    }

    private <T extends Statement> Mono<String> saveStatementList(List<T> statements, Class<T> type) {
        String cik = statements.get(0).getCik();
        logger.info("In statement service saving statement for {}", cik);
        try {
            Set<StatementKeyProjection> existingStatementKeysForType = getExistingStatementKeys(cik, type);
            filterStatements(statements, existingStatementKeysForType, type);

            if (statements.isEmpty()) {
                return Mono.just("All balance sheets already exist and/or are invalid");
            }

            return saveStatementsInBatches(statements, type);
        } catch (DataAccessException ex) {
            logger.error("Error occurred while saving quarterly statements for {}", cik);
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    private <T extends Statement> List<T> filterToLastElevenFY(String cik, List<T> statements) {
        if (statements.size() < 44) {
            throw new InsufficientDataException("Not enough statements available for " + cik);
        }
        return statements.subList(statements.size() - 44, statements.size());
    }

    private <T extends Statement> void checkConsecutive(String cik, List<T> statements) {
        LocalDate lastDate = statements.get(0).getDate();
        for (Statement statement : statements.subList(1, statements.size())) {
            if (lastDate.isBefore(statement.getDate().minusDays(131))) {
                throw new InsufficientDataException("Statements are not consecutive after "
                        + lastDate + " for " + cik);
            }
            lastDate = statement.getDate();
        }
    }

}
