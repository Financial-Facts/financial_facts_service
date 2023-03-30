package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.entities.discount.models.quarterlyData.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.AbstractTrailingPriceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository) { this.discountRepository = discountRepository; }

    public Mono<ResponseEntity> getDiscountByCik(String cik) {
        return Mono.just(discountRepository
                .findById(cik)
                .map(response -> new ResponseEntity(response, HttpStatus.OK))
                .orElse(new ResponseEntity("Discount " + cik + " not found", HttpStatus.NOT_FOUND)));
    }

    public Mono<ResponseEntity> addNewDiscount(Discount discount) {
        if (discountRepository.existsById(discount.getCik())) {
            return Mono.just(
                    new ResponseEntity("Discount " + discount.getCik() + " already exists", HttpStatus.BAD_REQUEST));
        }
        this.assignPeriodDataCik(discount, discount.getCik());
        discount.setLastUpdated(LocalDate.now());
        return Mono.just(
                new ResponseEntity(
                    discountRepository.save(discount).getCik(),
                    HttpStatus.CREATED))
                .onErrorReturn(new ResponseEntity("Error occurred while adding " + discount.getCik(), HttpStatus.CONFLICT)
        );
    }

    public Mono<ResponseEntity> updateDiscount(Discount discount) {
        if (!discountRepository.existsById(discount.getCik())) {
            return Mono.just(
                    new ResponseEntity("Discount " + discount.getCik() + " does not exist", HttpStatus.BAD_REQUEST));
        }
        this.assignPeriodDataCik(discount, discount.getCik());
        discount.setLastUpdated(LocalDate.now());
        return Mono.just(new ResponseEntity(
                                discountRepository.save(discount),
                                HttpStatus.OK))
                .onErrorReturn(new ResponseEntity("Error occurred while updating " + discount.getCik(), HttpStatus.CONFLICT)
        );
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
