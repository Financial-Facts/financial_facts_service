package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.ModelType;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.AbstractQuarterlyData;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.AbstractTrailingPriceData;
import com.facts.financial_facts_service.entities.serverResponse.DiscountResponse;
import com.facts.financial_facts_service.entities.serverResponse.ServerResponse;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class DiscountService {

    Logger logger = LoggerFactory.getLogger(DiscountService.class);

    private final DiscountRepository discountRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository) { this.discountRepository = discountRepository; }

    public Mono<DiscountResponse> getDiscountByCik(String cik) {
        logger.info("In discount service getting discount with cik {}", cik);
        return Mono.just(discountRepository
            .findById(cik)
            .map(response -> new DiscountResponse(Constants.SUCCESS, HttpStatus.OK.value(), response))
            .orElseThrow(() -> new DataNotFoundException(ModelType.DISCOUNT, cik)));
    }

    public Mono<ServerResponse> addNewDiscount(Discount discount) {
        logger.info("In discount service adding discount with cik {}", discount.getCik());
        if (discountRepository.existsById(discount.getCik())) {
            logger.error("Error occurred in discount service: discount with cik {} already exists",
                    discount.getCik());
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(Constants.DISCOUNT_EXISTS, discount.getCik()));
        }
        this.assignPeriodDataCik(discount, discount.getCik());
        discount.setLastUpdated(LocalDate.now());
        DiscountResponse response = new DiscountResponse(
            Constants.SUCCESS,
            HttpStatus.CREATED.value(),
            discountRepository.save(discount));
        if (Objects.isNull(response.getDiscount())) {
            logger.error("Error occurred while adding discount");
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(Constants.DISCOUNT_OPERATION_ERROR, Constants.ADD,
                            discount.getCik()));
        }
        return Mono.just(response);
    }

    public Mono<ServerResponse> updateDiscount(Discount discount) {
        logger.info("In discount service updating cik {}", discount.getCik());
        if (!discountRepository.existsById(discount.getCik())) {
            logger.error("Error occurred in discount service: discount with cik {} does not exist", discount.getCik());
            throw new DataNotFoundException(ModelType.DISCOUNT, discount.getCik());
        }
        this.assignPeriodDataCik(discount, discount.getCik());
        discount.setLastUpdated(LocalDate.now());
        DiscountResponse response = new DiscountResponse(
            Constants.SUCCESS,
            HttpStatus.OK.value(),
            discountRepository.save(discount));
        if (Objects.isNull(response.getDiscount())) {
            logger.error("Error occurred while updating discount for cik {}", discount.getCik());
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(Constants.DISCOUNT_OPERATION_ERROR,
                            Constants.UPDATE, discount.getCik()));
        }
        return Mono.just(response);
    }

    public Mono<ServerResponse> deleteDiscount(String cik) {
        if (!discountRepository.existsById(cik)) {
            logger.error("Error occurred in discount service: discount with cik {} does not exist", cik);
            throw new DataNotFoundException(ModelType.DISCOUNT, cik);
        }
        discountRepository.deleteById(cik);
        return Mono.just(new ServerResponse(
                Constants.SUCCESS,
                HttpStatus.OK.value()));
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
        priceData.forEach(period -> ((AbstractTrailingPriceData) period).setCik(cik));
    }

    private <T> void setQuarterlyDataCik(List<T> quarterlyData, String cik) {
        quarterlyData.forEach(period -> ((AbstractQuarterlyData) period).setCik(cik));
    }

}
