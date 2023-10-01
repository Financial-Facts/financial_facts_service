package com.facts.financial_facts_service.services;

import com.facts.financial_facts_service.constants.interfaces.Constants;
import com.facts.financial_facts_service.constants.enums.ModelType;
import com.facts.financial_facts_service.datafetcher.projections.SimpleDiscount;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.constants.enums.Operation;
import com.facts.financial_facts_service.entities.discount.models.UpdateDiscountInput;
import com.facts.financial_facts_service.entities.discount.models.PeriodicData;
import com.facts.financial_facts_service.entities.discount.models.benchmarkRatioPrice.BenchmarkRatioPrice;
import com.facts.financial_facts_service.entities.discount.models.benchmarkRatioPrice.BenchmarkRatioPriceInput;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.StickerPrice;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.StickerPriceInput;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.trailingPriceData.AbstractTrailingPriceData;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.exceptions.DiscountOperationException;
import com.facts.financial_facts_service.repositories.DiscountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class DiscountService implements Constants {

    final Logger logger = LoggerFactory.getLogger(DiscountService.class);

    @Value("${discount-update.batch.capacity}")
    private int UPDATE_BATCH_CAPACITY;

    @Autowired
    private DiscountRepository discountRepository;

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

    public Mono<List<SimpleDiscount>> getBulkSimpleDiscounts(boolean activeOnly) {
        logger.info("In discount service getting bulk simple discounts");
        try {
            return activeOnly
                ? Mono.just(discountRepository.findAllActiveSimpleDiscounts())
                : Mono.just(discountRepository.findAllSimpleDiscounts());
        } catch (DataAccessException ex) {
            logger.error("Error occurred while getting bulk simple discounts");
            throw new DiscountOperationException(Operation.BULK_SIMPLE);
        }
    }

    public Mono<List<String>> updateBulkDiscountStatus(String cikList, UpdateDiscountInput input) {
        logger.info("In discount service updating status for discounts {}", cikList);
        List<String> updates = new ArrayList<>();
        List<String> keyList = input.getDiscountUpdateMap().keySet().stream().toList();
        int i = 0;
        while (i < keyList.size()) {
            int value = Math.min(i + UPDATE_BATCH_CAPACITY, keyList.size());
            List<String> batchKeys = keyList.subList(i, value);
            List<Discount> discountReferenceList = discountRepository.findAllById(batchKeys);
            discountReferenceList.forEach(discountReference -> {
                String cik = discountReference.getCik();
                boolean settingToActive = input.getDiscountUpdateMap().get(cik);
                discountReference.setActive(settingToActive);
                updates.add(settingToActive
                    ? String.format(SET_TO_ACTIVE_UPDATE, cik)
                    : String.format(SET_TO_INACTIVE_UPDATE, cik));
            });
            discountRepository.saveAllAndFlush(discountReferenceList);
            i += UPDATE_BATCH_CAPACITY;
        }
        return Mono.just(updates);
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
        current.setLastUpdated(LocalDate.now());
        current.setActive(update.getActive());

        updateBenchmarkRatioPrice(current.getBenchmarkRatioPrice(), update.getBenchmarkRatioPrice());
        updateStickerPrice(current.getStickerPrice(), update.getStickerPrice());
    }

    private void updateBenchmarkRatioPrice(BenchmarkRatioPrice current, BenchmarkRatioPrice update) {
        current.setRatioPrice(update.getRatioPrice());
        updateBenchmarkRatioPriceInput(current.getInput(), update.getInput());
    }

    private void updateBenchmarkRatioPriceInput(BenchmarkRatioPriceInput current, BenchmarkRatioPriceInput update) {
        current.setIndustry(update.getIndustry());
        current.setTtmRevenue(update.getTtmRevenue());
        current.setSharesOutstanding(update.getSharesOutstanding());
        current.setPsBenchmarkRatio(update.getPsBenchmarkRatio());
    }

    private void updateStickerPrice(StickerPrice current, StickerPrice update) {
        updateTrailingPeriod(current.getTtmPriceData(), update.getTtmPriceData());
        updateTrailingPeriod(current.getTfyPriceData(), update.getTfyPriceData());
        updateTrailingPeriod(current.getTtyPriceData(), update.getTtyPriceData());
        updateStickerPriceInput(current.getInput(), update.getInput());
    }

    private void updateTrailingPeriod(AbstractTrailingPriceData current, AbstractTrailingPriceData update) {
        current.setStickerPrice(update.getStickerPrice());
        current.setSalePrice(update.getSalePrice());
    }

    private void updateStickerPriceInput(StickerPriceInput current, StickerPriceInput update) {
        updatePeriodicData(current.getAnnualBVPS(), update.getAnnualBVPS());
        updatePeriodicData(current.getAnnualPE(), update.getAnnualPE());
        updatePeriodicData(current.getAnnualEPS(), update.getAnnualEPS());
        updatePeriodicData(current.getAnnualROIC(), update.getAnnualROIC());
        updatePeriodicData(current.getAnnualEquity(), update.getAnnualEquity());
        updatePeriodicData(current.getAnnualRevenue(), update.getAnnualRevenue());
        updatePeriodicData(current.getAnnualOperatingCashFlow(), update.getAnnualOperatingCashFlow());
    }

    private <T extends PeriodicData> void updatePeriodicData(List<T> current, List<T> update) {
        Set<LocalDate> currentSet = current.stream()
                .map(PeriodicData::getAnnouncedDate).collect(Collectors.toSet());
            update.forEach(quarter -> {
                if (!currentSet.contains(quarter.getAnnouncedDate())) {
                    current.add(quarter);
                }
            });
        current.sort(Comparator.comparing(T::getAnnouncedDate));
    }

}