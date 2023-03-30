package com.facts.financial_facts_service.entities.discount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "v1/discount")
public class DiscountController {

    private final DiscountService discountService;

    @Autowired
    public DiscountController(DiscountService discountService) { this.discountService = discountService; }

    @GetMapping(path = "/{cik}")
    public CompletableFuture<ResponseEntity> getDiscount(@PathVariable String cik) {
        return discountService
                .getDiscountByCik(cik.toUpperCase())
                .toFuture();
    }

    @PostMapping
    public CompletableFuture<ResponseEntity> addNewDiscount(@RequestBody Discount discount) {
        System.out.println(discount);
        if (Objects.nonNull(discount)) {
            return discountService
                    .addNewDiscount(discount)
                    .toFuture();
        }
        return Mono.just(new ResponseEntity("Invalid input parameters", HttpStatus.BAD_REQUEST)).toFuture();
    }

    @PutMapping
    public CompletableFuture<ResponseEntity> updateDiscount(@RequestBody Discount discount) {
        if (Objects.nonNull(discount)) {
            return discountService
                    .updateDiscount(discount)
                    .toFuture();
        }
        return Mono.just(new ResponseEntity("Invalid input parameters", HttpStatus.BAD_REQUEST)).toFuture();
    }

}
