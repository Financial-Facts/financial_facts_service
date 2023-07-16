package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.datafetcher.DataFetcher;
import com.facts.financial_facts_service.datafetcher.projections.SimpleDiscount;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.entities.discount.models.UpdateDiscountInput;
import com.facts.financial_facts_service.services.DiscountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.facts.financial_facts_service.constants.Constants.V1_DISCOUNT;

@RestController
@Validated
@RequestMapping(path = V1_DISCOUNT)
public class DiscountController implements Constants {

    Logger logger = LoggerFactory.getLogger(DiscountController.class);

    @Autowired
    private DiscountService discountService;

    @GetMapping(path = CIK_PATH_PARAM)
    public CompletableFuture<ResponseEntity<Discount>> getDiscountWithCik(@PathVariable @NotBlank @Pattern(regexp = CIK_REGEX) String cik) {
        logger.info("In discount controller getting bulk discounts");
        return discountService.getDiscountWithCik(cik.toUpperCase())
            .flatMap(discount -> {
                logger.info("Fetch complete for discount with cik {}", cik);
                return Mono.just(new ResponseEntity<>(discount, HttpStatus.OK));
            }).toFuture();
    }

    @GetMapping("/bulkSimpleDiscounts")
    public CompletableFuture<ResponseEntity<List<SimpleDiscount>>> getBulkSimpleDiscounts() {
        logger.info("In discount controller getting bulk simple discounts");
        return discountService.getBulkSimpleDiscounts(false)
            .flatMap(cikList -> {
                logger.info("Fetch complete for bulk simple discounts");
                return Mono.just(new ResponseEntity<>(cikList, HttpStatus.OK));
            }).toFuture();
    }

    @PutMapping
    public CompletableFuture<ResponseEntity<List<String>>> updateBulkDiscountStatus(@Valid @RequestBody UpdateDiscountInput input) {
        String discountCiksToUpdate = StringUtils.collectionToCommaDelimitedString(input.getDiscountUpdateMap().keySet());
        logger.info("In discount controller updating discount status for {}", discountCiksToUpdate);
        return discountService.updateBulkDiscountStatus(discountCiksToUpdate, input)
            .flatMap(response -> {
                logger.info("Update complete for discounts with ciks {}", discountCiksToUpdate);
                return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
            }).toFuture();
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> saveDiscount(@Valid @RequestBody Discount discount) {
        logger.info("In discount controller adding discount with cik {}", discount.getCik());
        return discountService.saveDiscount(discount)
            .flatMap(response -> {
                logger.info("Save complete for discount with cik {}", discount.getCik());
                return Mono.just(new ResponseEntity<>(response, HttpStatus.CREATED));
            }).toFuture();
    }

    @DeleteMapping(path = CIK_PATH_PARAM)
    public CompletableFuture<ResponseEntity<String>> deleteDiscount(@PathVariable @NotBlank @Pattern(regexp = CIK_REGEX) String cik) {
        logger.info("In discount controller deleting discount for cik {}", cik);
        return discountService.deleteDiscount(cik.toUpperCase())
            .flatMap(response -> {
                logger.info("Delete complete for discount with cik {}", cik);
                return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
            }).toFuture();
    }
}
