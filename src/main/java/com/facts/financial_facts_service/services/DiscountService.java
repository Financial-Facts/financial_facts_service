package com.facts.financial_facts_service.services;

import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.facts.financial_facts_service.constants.enums.ModelType;
import com.facts.financial_facts_service.datafetcher.projections.SimpleDiscount;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.constants.enums.Operation;
import com.facts.financial_facts_service.entities.discount.models.UpdateDiscountInput;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.exceptions.DiscountOperationException;
import com.facts.financial_facts_service.repositories.DiscountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class DiscountService implements Constants {

    final Logger logger = LoggerFactory.getLogger(DiscountService.class);

    @Value("${discount-update.batch.capacity}")
    private int UPDATE_BATCH_CAPACITY;

    @Autowired
    private DiscountRepository discountRepository;

    public Mono<Discount> getDiscountWithCik(String cik) {
        logger.info("In discount service getting discount for {}", cik);
        try {
            Optional<Discount> discountOptional = discountRepository.findById(cik);
            if (discountOptional.isPresent()) {
                return Mono.just(discountOptional.get());
            }
            throw new DataNotFoundException(ModelType.DISCOUNT, cik);
        } catch (DataAccessException ex) {
            logger.error("Error occurred while getting discount for {}", cik);
            throw new DiscountOperationException(Operation.GET);
        }
    }

    public Mono<List<SimpleDiscount>> getBulkSimpleDiscounts() {
        logger.info("In discount service getting bulk simple discounts");
        try {
            return Mono.just(discountRepository.findAllSimpleDiscounts());
        } catch (DataAccessException ex) {
            logger.error("Error occurred while getting bulk simple discounts");
            throw new DiscountOperationException(Operation.BULK_SIMPLE);
        }
    }

    public Mono<List<String>> updateBulkDiscountStatus(String cikList, UpdateDiscountInput input) {
        logger.info("In discount service updating status for discounts {}", cikList);
        List<String> updates = new ArrayList<>();
        List<String> keyList = input.getDiscountUpdateMap().keySet().stream().toList();
        int i = 0;
        while (i < keyList.size()) {
            int value = Math.min(i + UPDATE_BATCH_CAPACITY, keyList.size());
            List<String> batchKeys = keyList.subList(i, value);
            List<Discount> discountReferenceList = discountRepository.findAllById(batchKeys);
            discountReferenceList.forEach(discountReference -> {
                String cik = discountReference.getCik();
                boolean settingToActive = input.getDiscountUpdateMap().get(cik);
                discountReference.setActive(settingToActive);
                updates.add(settingToActive
                    ? String.format(SET_TO_ACTIVE_UPDATE, cik)
                    : String.format(SET_TO_INACTIVE_UPDATE, cik));
            });
            discountRepository.saveAllAndFlush(discountReferenceList);
            i += UPDATE_BATCH_CAPACITY;
        }
        return Mono.just(updates);
    }

    public Mono<String> saveDiscount(Discount discount) {
        logger.info("In discount service adding discount with cik {}", discount.getCik());
        try {
            if (discountRepository.existsById(discount.getCik())) {
                Discount current = discountRepository.getReferenceById(discount.getCik());
                current.copy(discount);
                discountRepository.save(current);
            } else {
                discountRepository.save(discount);
            }
        } catch (DataAccessException ex) {
            logger.error("Error occurred while adding discount with cik {}: {}", discount.getCik(),
                    ex.getMessage());
            throw new DiscountOperationException(Operation.ADD, discount.getCik(), ex.getMessage());
        }
        logger.info("Save complete for cik {}", discount.getCik());
        return Mono.just(DISCOUNT_ADDED);
    }

    public Mono<String> deleteDiscount(String cik) {
        try {
            this.checkIfDiscountDoesNotExists(cik);
            discountRepository.deleteById(cik);
        } catch (DataAccessException ex) {
            logger.error("Error occurred while deleting discount for cik {}", cik);
            throw new DiscountOperationException(Operation.DELETE, cik, ex.getMessage());
        }
        return Mono.just(DISCOUNT_DELETED);
    }

    private void checkIfDiscountDoesNotExists(String cik) {
        if (!discountRepository.existsById(cik)) {
            logger.error("Error occurred in discount service: discount with cik {} does not exist",
                    cik);
            throw new DataNotFoundException(ModelType.DISCOUNT, cik);
        }
    }

}