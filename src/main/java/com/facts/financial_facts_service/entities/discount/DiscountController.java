package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.entities.serverResponse.DiscountResponse;
import com.facts.financial_facts_service.entities.serverResponse.ServerResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@Validated
@RequestMapping(path = Constants.V1_DISCOUNT)
public class DiscountController {

    Logger logger = LoggerFactory.getLogger(DiscountController.class);

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping(path = Constants.CIK_PATH_PARAM)
    public CompletableFuture<DiscountResponse> getDiscount(@PathVariable @NotBlank @Pattern(regexp = Constants.CIK_REGEX) String cik) {
        logger.info("In discount controller getting discount for cik {}", cik);
        return discountService.getDiscountByCik(cik.toUpperCase()).toFuture();
    }

    @PostMapping
    public CompletableFuture<ServerResponse> addNewDiscount(@Valid @RequestBody Discount discount) {
        logger.info("In discount controller adding discount with cik {}", discount.getCik());
        return discountService.addNewDiscount(discount).toFuture();
    }

    @PutMapping
    public CompletableFuture<ServerResponse> updateDiscount(@Valid @RequestBody Discount discount) {
        logger.info("In discount controller updating discount with cik {}", discount.getCik());
        return discountService.updateDiscount(discount).toFuture();
    }

    @DeleteMapping(Constants.CIK_PATH_PARAM)
    public CompletableFuture<ServerResponse> deleteDiscount(@PathVariable @NotBlank @Pattern(regexp = Constants.CIK_REGEX) String cik) {
        logger.info("In discount controller deleting discount for cik {}", cik);
        return discountService.deleteDiscount(cik).toFuture();
    }

}
