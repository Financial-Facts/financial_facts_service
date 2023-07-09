package com.facts.financial_facts_service.services;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.ModelType;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.constants.Operation;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.AbstractTrailingPriceData;
import com.facts.financial_facts_service.entities.models.AbstractQuarterlyData;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.exceptions.DiscountOperationException;
import com.facts.financial_facts_service.repositories.DiscountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DiscountService implements Constants {

    Logger logger = LoggerFactory.getLogger(DiscountService.class);

    @Autowired
    private DiscountRepository discountRepository;

    public Mono<List<Discount>> getBulkDiscount() {
        logger.info("In discount service getting bulk discounts");
        try {
            List<Discount> discounts = discountRepository.findAll();
            return Mono.just(discounts);
        } catch (DataAccessException ex) {
            logger.error("Error occurred while getting bulk discounts");
            throw new DiscountOperationException(Operation.BULK);
        }
    }

    public Mono<String> saveDiscount(Discount discount) {
        logger.info("In discount service adding discount with cik {}", discount.getCik());
        try {
            discount.setLastUpdated(LocalDate.now());
            if (discountRepository.existsById(discount.getCik())) {
                Discount current = discountRepository.getReferenceById(discount.getCik());
                updateDiscount(current, discount);
                discountRepository.save(current);
            } else {
                discountRepository.save(discount);
            }
        } catch (DataAccessException ex) {
            logger.error("Error occurred while adding discount with cik {}: {}", discount.getCik(),
                    ex.getMessage());
            throw new DiscountOperationException(Operation.ADD, discount.getCik());
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
            throw new DiscountOperationException(Operation.DELETE, cik);
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

    private void updateDiscount(Discount current, Discount update) {
        current.setSymbol(update.getSymbol());
        current.setName(update.getName());
        current.setRatioPrice(update.getRatioPrice());
        current.setLastUpdated(update.getLastUpdated());

        updateTrailingPeriod(current.getTtmPriceData(), update.getTtmPriceData());
        updateTrailingPeriod(current.getTfyPriceData(), update.getTfyPriceData());
        updateTrailingPeriod(current.getTtyPriceData(), update.getTtyPriceData());

        updateQuarterlyData(current.getQuarterlyBVPS(), update.getQuarterlyBVPS());
        updateQuarterlyData(current.getQuarterlyPE(), update.getQuarterlyPE());
        updateQuarterlyData(current.getQuarterlyEPS(), update.getQuarterlyEPS());
        updateQuarterlyData(current.getQuarterlyROIC(), update.getQuarterlyROIC());
    }

    private void updateTrailingPeriod(AbstractTrailingPriceData current, AbstractTrailingPriceData update) {
        current.setStickerPrice(update.getStickerPrice());
        current.setSalePrice(update.getSalePrice());
    }

    private <T extends AbstractQuarterlyData> void updateQuarterlyData(List<T> current, List<T> update) {
        Set<LocalDate> currentSet = current.stream()
                .map(AbstractQuarterlyData::getAnnouncedDate).collect(Collectors.toSet());
        update.stream().forEach(quarter -> {
            if (!currentSet.contains(quarter.getAnnouncedDate())) {
                current.add(quarter);
            }
        });
    }

}