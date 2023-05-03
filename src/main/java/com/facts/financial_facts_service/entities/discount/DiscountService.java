package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.ModelType;
import com.facts.financial_facts_service.entities.discount.models.Operation;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.AbstractTrailingPriceData;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.exceptions.DiscountOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.facts.financial_facts_service.entities.discount.DiscountUtils.assignPeriodDataCik;

@Service
public class DiscountService implements Constants {

    Logger logger = LoggerFactory.getLogger(DiscountService.class);

    private final DiscountRepository discountRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public Mono<ResponseEntity<Discount>> getDiscountByCik(String cik) {
        logger.info("In discount service getting discount with cik {}", cik);
        try {
            Optional<Discount> discountOptional = discountRepository.findById(cik);
            return Mono.just(discountOptional
                    .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                    .orElseThrow(() -> new DataNotFoundException(ModelType.DISCOUNT, cik)));
        } catch (DataAccessException ex) {
            logger.error("Error occurred while adding discount");
            throw new DiscountOperationException(Operation.GET, cik);
        }
    }


    public Mono<ResponseEntity<String>> addNewDiscount(Discount discount) {
        logger.info("In discount service adding discount with cik {}", discount.getCik());
        try {
            this.checkIfDiscountAlreadyExists(discount.getCik());
            assignPeriodDataCik(discount, discount.getCik());
            discount.setLastUpdated(LocalDate.now());
            discountRepository.save(discount);
        } catch (DataAccessException ex) {
            logger.error("Error occurred while adding discount");
            throw new DiscountOperationException(Operation.ADD, discount.getCik());
        }
        return Mono.just(new ResponseEntity<>(DISCOUNT_ADDED, HttpStatus.CREATED));
    }

    public Mono<ResponseEntity<String>> updateDiscount(Discount discount) {
        logger.info("In discount service updating cik {}", discount.getCik());
        try {
            this.checkIfDiscountDoesNotExists(discount.getCik());
            assignPeriodDataCik(discount, discount.getCik());
            discount.setLastUpdated(LocalDate.now());
            discountRepository.save(discount);
        } catch (DataAccessException ex) {
            logger.error("Error occurred while updating discount for cik {}", discount.getCik());
            throw new DiscountOperationException(Operation.UPDATE, discount.getCik());
        }
        return Mono.just(new ResponseEntity<>(DISCOUNT_UPDATED, HttpStatus.OK));
    }

    public Mono<ResponseEntity<String>> deleteDiscount(String cik) {
        try {
            this.checkIfDiscountDoesNotExists(cik);
            discountRepository.deleteById(cik);
        } catch (DataAccessException ex) {
            logger.error("Error occurred while deleting discount for cik {}", cik);
            throw new DiscountOperationException(Operation.DELETE, cik);
        }
        return Mono.just(new ResponseEntity<>(DISCOUNT_DELETED, HttpStatus.OK));
    }

    private void checkIfDiscountAlreadyExists(String cik) {
        if (discountRepository.existsById(cik)) {
            logger.error("Error occurred in discount service: discount with cik {} already exists",
                    cik);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(DISCOUNT_EXISTS, cik));
        }
    }

    private void checkIfDiscountDoesNotExists(String cik) {
        if (!discountRepository.existsById(cik)) {
            logger.error("Error occurred in discount service: discount with cik {} does not exist",
                    cik);
            throw new DataNotFoundException(ModelType.DISCOUNT, cik);
        }
    }

}