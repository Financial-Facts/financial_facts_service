package com.facts.financial_facts_service.discount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository) { this.discountRepository = discountRepository; }

    public Mono<ResponseEntity> getDiscountByCik(String cik) {
        return Mono.just(discountRepository
                .findById(cik)
                .map(response -> new ResponseEntity(response, HttpStatus.OK))
                .orElse(new ResponseEntity("Discount " + cik + "not found", HttpStatus.NOT_FOUND)));
    }

    public Mono<ResponseEntity> addNewDiscount(Discount discount) {
        if (discountRepository.existsById(discount.getCik())) {
            return Mono.just(
                    new ResponseEntity("Discount " + discount.getCik() + " already exists", HttpStatus.BAD_REQUEST));
        }
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
        discount.setLastUpdated(LocalDate.now());
        return Mono.just(new ResponseEntity(
                                discountRepository.save(discount),
                                HttpStatus.OK))
                .onErrorReturn(new ResponseEntity("Error occurred while updating " + discount.getCik(), HttpStatus.CONFLICT)
        );
    }

}
