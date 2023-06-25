package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.services.DiscountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

import static com.facts.financial_facts_service.constants.Constants.V1_DISCOUNT;

@RestController
@Validated
@RequestMapping(path = V1_DISCOUNT)
public class DiscountController implements Constants {

    Logger logger = LoggerFactory.getLogger(DiscountController.class);

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping(path = CIK_PATH_PARAM)
    public CompletableFuture<ResponseEntity<Discount>> getDiscount(@PathVariable @NotBlank @Pattern(regexp = CIK_REGEX) String cik) {
        logger.info("In discount controller getting discount for cik {}", cik);
        return discountService.getDiscountByCik(cik.toUpperCase())
                .flatMap(discount -> Mono.just(new ResponseEntity<>(discount, HttpStatus.OK))).toFuture();
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> addNewDiscount(@Valid @RequestBody Discount discount) {
        logger.info("In discount controller adding discount with cik {}", discount.getCik());
        return discountService.addNewDiscount(discount)
                .flatMap(response -> Mono.just(new ResponseEntity<>(response, HttpStatus.CREATED))).toFuture();
    }

    @PutMapping
    public CompletableFuture<ResponseEntity<String>> updateDiscount(@Valid @RequestBody Discount discount) {
        logger.info("In discount controller updating discount with cik {}", discount.getCik());
        return discountService.updateDiscount(discount)
                .flatMap(response -> Mono.just(new ResponseEntity<>(response, HttpStatus.OK))).toFuture();
    }

    @DeleteMapping(path = CIK_PATH_PARAM)
    public CompletableFuture<ResponseEntity<String>> deleteDiscount(@PathVariable @NotBlank @Pattern(regexp = CIK_REGEX) String cik) {
        logger.info("In discount controller deleting discount for cik {}", cik);
        return discountService.deleteDiscount(cik)
                .flatMap(response -> Mono.just(new ResponseEntity<>(response, HttpStatus.OK))).toFuture();
    }

}
