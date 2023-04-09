package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.AbstractTrailingPriceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiscountService {

    Logger logger = LoggerFactory.getLogger(DiscountService.class);

    private final DiscountRepository discountRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository) { this.discountRepository = discountRepository; }

    public Mono<ResponseEntity> getDiscountByCik(String cik) {
        logger.info("In discount service getting discount with cik {}", cik);
        return Mono.just(discountRepository
            .findById(cik)
            .map(response -> new ResponseEntity(response, HttpStatus.OK))
            .orElse(new ResponseEntity(
                String.format(Constants.DISCOUNT_NOT_FOUND, cik),
                HttpStatus.NOT_FOUND)));
    }

    public Mono<ResponseEntity> addNewDiscount(Discount discount) {
        logger.info("In discount service adding discount with cik {}", discount.getCik());
        if (discountRepository.existsById(discount.getCik())) {
            logger.error("Error occurred in discount service: discount with cik {} already exists", discount.getCik());
            return Mono.just(
                new ResponseEntity(
                    String.format(Constants.DISCOUNT_EXISTS, discount.getCik()),
                    HttpStatus.BAD_REQUEST));
        }
        this.assignPeriodDataCik(discount, discount.getCik());
        discount.setLastUpdated(LocalDate.now());
        return Mono.just(
            new ResponseEntity(
                discountRepository.save(discount).getCik(),
                HttpStatus.CREATED))
            .onErrorResume(error -> {
                logger.error("Error occurred while adding discount {}", error.getMessage());
                return Mono.just(new ResponseEntity(
                    String.format(Constants.DISCOUNT_OPERATION_ERROR, Constants.ADD, discount.getCik()),
                    HttpStatus.CONFLICT));
            });
    }

    public Mono<ResponseEntity> updateDiscount(Discount discount) {
        logger.info("In discount service updating cik {}", discount.getCik());
        if (!discountRepository.existsById(discount.getCik())) {
            logger.error("Error occurred in discount service: discount with cik {} does not exist", discount.getCik());
            return Mono.just(
                new ResponseEntity(
                    String.format(Constants.DISCOUNT_NOT_FOUND, discount.getCik()),
                    HttpStatus.BAD_REQUEST));
        }
        this.assignPeriodDataCik(discount, discount.getCik());
        discount.setLastUpdated(LocalDate.now());
        return Mono.just(new ResponseEntity(
                discountRepository.save(discount),
                HttpStatus.OK))
            .onErrorResume(error -> {
                logger.error("Error occurred while updating discount for cik {}", discount.getCik());
                return Mono.just(new ResponseEntity(
                        String.format(Constants.DISCOUNT_OPERATION_ERROR, Constants.UPDATE, discount.getCik()),
                        HttpStatus.CONFLICT));
            });
    }

    public Mono<ResponseEntity> deleteDiscount(String cik) {
        if (!discountRepository.existsById(cik)) {
            logger.error("Error occurred in discount service: discount with cik {} does not exist", cik);
            return Mono.just(
                new ResponseEntity(
                    String.format(Constants.DISCOUNT_NOT_FOUND, cik),
                    HttpStatus.BAD_REQUEST));
        }
        discountRepository.deleteById(cik);
        return Mono.just(new ResponseEntity(
                Constants.SUCCESS,
                HttpStatus.OK));
    }

    private void assignPeriodDataCik(Discount discount, String cik) {
        setTrailingDataCik(discount.getTtmPriceData(), cik);
        setTrailingDataCik(discount.getTfyPriceData(), cik);
        setTrailingDataCik(discount.getTtyPriceData(), cik);
        setQuarterlyDataCik(discount.getQuarterlyBVPS(), cik);
        setQuarterlyDataCik(discount.getQuarterlyPE(), cik);
        setQuarterlyDataCik(discount.getQuarterlyEPS(), cik);
        setQuarterlyDataCik(discount.getQuarterlyROIC(), cik);
    }

    private <T> void setTrailingDataCik(List<T> priceData, String cik) {
        priceData.forEach(period -> {
            ((AbstractTrailingPriceData) period).setCik(cik);
        });
    }

    private <T> void setQuarterlyDataCik(List<T> quarterlyData, String cik) {
        quarterlyData.forEach(period -> {
            ((AbstractQuarterlyData) period).setCik(cik);
        });
    }

}
