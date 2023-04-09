package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.constants.Constants;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = Constants.V1_DISCOUNT)
public class DiscountController {

    Logger logger = LoggerFactory.getLogger(DiscountController.class);

    private final DiscountService discountService;

    @Autowired
    public DiscountController(DiscountService discountService) { this.discountService = discountService; }

    @GetMapping(path = Constants.CIK_PATH_PARAM)
    public CompletableFuture<ResponseEntity> getDiscount(@PathVariable String cik) {
        logger.info("In discount controller getting discount for cik {}", cik);
        return discountService
                .getDiscountByCik(cik.toUpperCase())
                .toFuture();
    }

    @PostMapping
    public CompletableFuture<ResponseEntity> addNewDiscount(@RequestBody Discount discount) {
        logger.info("In discount controller adding discount with cik {}", discount.getCik());
        if (Objects.nonNull(discount) && StringUtils.isNotEmpty(discount.getCik())) {
            return discountService
                    .addNewDiscount(discount)
                    .toFuture();
        }
        logger.error("Invalid input parameters provided in controller addNewDiscount");
        return Mono.just(new ResponseEntity(Constants.INVALID_INPUT, HttpStatus.BAD_REQUEST)).toFuture();
    }

    @PutMapping
    public CompletableFuture<ResponseEntity> updateDiscount(@RequestBody Discount discount) {
        logger.info("In discount controller updating discount with cik {}", discount.getCik());
        if (Objects.nonNull(discount) && StringUtils.isNotEmpty(discount.getCik())) {
            return discountService
                    .updateDiscount(discount)
                    .toFuture();
        }
        logger.error("Invalid input parameters provided in controller updateDiscount");
        return Mono.just(new ResponseEntity(Constants.INVALID_INPUT, HttpStatus.BAD_REQUEST)).toFuture();
    }

    @DeleteMapping("/{cik}")
    public CompletableFuture<ResponseEntity> deleteDiscount(@PathVariable String cik) {
        logger.info("In discount controller deleting discount for cik {}", cik);
        if (StringUtils.isNotBlank(cik)) {
            return discountService
                    .deleteDiscount(cik)
                    .toFuture();
        }
        logger.error("Invalid input parameter provided in controller deleteDiscount");
        return Mono.just(new ResponseEntity(Constants.INVALID_INPUT, HttpStatus.BAD_REQUEST)).toFuture();
    }

}
