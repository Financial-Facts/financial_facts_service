package com.facts.financial_facts_service.services;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.ModelType;
import com.facts.financial_facts_service.datafetcher.projections.SimpleDiscount;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.constants.Operation;
import com.facts.financial_facts_service.entities.discount.models.UpdateDiscountInput;
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

    public Mono<List<SimpleDiscount>> getBulkSimpleDiscounts(boolean filterInactive) {
        logger.info("In discount service getting bulk simple discounts");
        try {
            return Mono.just(discountRepository.findAllSimpleDiscounts())
                .flatMap(simpleDiscounts -> filterInactive
                    ? Mono.just(simpleDiscounts.stream()
                        .filter(SimpleDiscount::getActive).collect(Collectors.toList()))
                    : Mono.just(simpleDiscounts));
        } catch (DataAccessException ex) {
            logger.error("Error occurred while getting bulk simple discounts");
            throw new DiscountOperationException(Operation.BULK_SIMPLE);
        }
    }

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

    public Mono<String> updateDiscountStatus(UpdateDiscountInput input) {
        logger.info("In discount service updating status for {} to {}", input.getCik(), input.isActive());
        try {
            Optional<Discount> discountOptional = discountRepository.findById(input.getCik());
            if (discountOptional.isPresent()) {
                Discount discount = discountOptional.get();
                discount.setActive(input.isActive());
                discountRepository.saveAndFlush(discount);
                return Mono.just(DISCOUNT_UPDATED);
            }
            throw new DataNotFoundException(ModelType.DISCOUNT, input.getCik());
        } catch (DataAccessException ex) {
            logger.error("Error occurred while updating status for discount with cik {}: {}", input.getCik(),
                    ex.getMessage());
            throw new DiscountOperationException(Operation.UPDATE, input.getCik());
        }
    }

    public Mono<String> saveDiscount(Discount discount) {
        logger.info("In discount service adding discount with cik {}", discount.getCik());
        try {
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
        update.forEach(quarter -> {
            if (!currentSet.contains(quarter.getAnnouncedDate())) {
                current.add(quarter);
            }
        });
    }

}