package com.facts.financial_facts_service.services;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.datafetcher.projections.SimpleDiscount;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.entities.discount.models.UpdateDiscountInput;
import com.facts.financial_facts_service.entities.discount.models.benchmarkRatioPrice.BenchmarkRatioPrice;
import com.facts.financial_facts_service.entities.discount.models.benchmarkRatioPrice.BenchmarkRatioPriceInput;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.StickerPrice;
import com.facts.financial_facts_service.entities.discount.models.stickerPrice.StickerPriceInput;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.exceptions.DiscountOperationException;
import com.facts.financial_facts_service.repositories.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DiscountServiceTest implements TestConstants {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountService discountService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(discountService, "discountRepository", discountRepository);
    }

    @Nested
    @DisplayName("getDiscountWithCik")
    class getDiscountWithCikTest {

        @Test
        public void testGetDiscountWithCikSuccess() {
            Discount discount = new Discount();
            discount.setCik(CIK);
            when(discountRepository.findById(CIK))
                    .thenReturn(Optional.of(discount));
            Discount actual = discountService.getDiscountWithCik(CIK).block();
            assertNotNull(actual);
            assertEquals(CIK, actual.getCik());
        }

        @Test
        public void testGetDiscountNotFound() {
            when(discountRepository.findById(CIK))
                    .thenReturn(Optional.empty());
            assertThrows(DataNotFoundException.class, () -> discountService.getDiscountWithCik(CIK).block());
        }

        @Test
        public void testGetDiscountDataAccessError() {
            when(discountRepository.findById(CIK))
                    .thenThrow(mock(DataAccessException.class));
            assertThrows(DiscountOperationException.class, () -> discountService.getDiscountWithCik(CIK).block());
        }
    }

    @Nested
    @DisplayName("getBulkSimpleDiscounts")
    class getBulkSimpleDiscountsTest {

        private final SimpleDiscount activeSimpleDiscount = mock(SimpleDiscount.class);

        private final SimpleDiscount simpleDiscount = mock(SimpleDiscount.class);

        @Test
        public void testGetBulkActiveSimpleDiscounts() {
            when(discountRepository.findAllSimpleDiscounts())
                    .thenReturn(List.of(activeSimpleDiscount));
            List<SimpleDiscount> actual =
                    discountService.getBulkSimpleDiscounts().block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertEquals(activeSimpleDiscount, actual.get(0));
        }

        @Test
        public void testGetBulkSimpleDiscounts() {
            when(discountRepository.findAllSimpleDiscounts())
                    .thenReturn(List.of(simpleDiscount));
            List<SimpleDiscount> actual =
                    discountService.getBulkSimpleDiscounts().block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertEquals(simpleDiscount, actual.get(0));
        }

        @Test
        public void testGetBulkActiveDiscountsDataAccessError() {
            when(discountRepository.findAllSimpleDiscounts())
                    .thenThrow(mock(DataAccessException.class));
            assertThrows(DiscountOperationException.class, () ->
                    discountService.getBulkSimpleDiscounts().block());
        }

        @Test
        public void testGetBulkSimpleDiscountsDataAccessError() {
            when(discountRepository.findAllSimpleDiscounts())
                    .thenThrow(mock(DataAccessException.class));
            assertThrows(DiscountOperationException.class, () ->
                    discountService.getBulkSimpleDiscounts().block());
        }
    }

    @Nested
    @DisplayName("updateBulkDiscountStatus")
    class updateBulkDiscountStatusTests {

        private final Discount discount = new Discount();

        private final Discount discount2 = new Discount();

        @BeforeEach
        public void init() {
            discount.setCik(CIK);
            discount2.setCik(CIK2);
            ReflectionTestUtils.setField(discountService, "UPDATE_BATCH_CAPACITY", 5);
        }

        @Test
        public void testBatchSizeOverCapacity() {
            ReflectionTestUtils.setField(discountService, "UPDATE_BATCH_CAPACITY", 1);
            when(discountRepository.findAllById(List.of(CIK)))
                    .thenReturn(List.of(discount));
            when(discountRepository.findAllById(List.of(CIK2)))
                    .thenReturn(List.of(discount2));
            UpdateDiscountInput input = buildUpdateDiscountInput();
            discountService.updateBulkDiscountStatus("cikList", input).block();
            verify(discountRepository).findAllById(List.of(CIK));
            verify(discountRepository).findAllById(List.of(CIK2));
            verify(discountRepository).saveAllAndFlush(List.of(discount));
            verify(discountRepository).saveAllAndFlush(List.of(discount2));
        }

        @Test
        public void testBatchSizeUnderCapacity() {
            UpdateDiscountInput input = buildUpdateDiscountInput();
            when(discountRepository.findAllById(List.of(CIK, CIK2)))
                    .thenReturn(List.of(discount, discount2));
            discountService.updateBulkDiscountStatus("cikList", input).block();
            verify(discountRepository).findAllById(List.of(CIK, CIK2));
            verify(discountRepository).saveAllAndFlush(List.of(discount, discount2));
        }

        @Test
        public void testBulkStatusUpdateSuccess() {
            UpdateDiscountInput input = buildUpdateDiscountInput();
            when(discountRepository.findAllById(List.of(CIK, CIK2)))
                    .thenReturn(List.of(discount, discount2));
            discountService.updateBulkDiscountStatus("cikList", input).block();
            verify(discountRepository).saveAllAndFlush(List.of(discount, discount2));
        }

        private UpdateDiscountInput buildUpdateDiscountInput() {
            UpdateDiscountInput input = new UpdateDiscountInput();
            HashMap<String, Boolean> updateMap = new HashMap<>();
            updateMap.put(CIK, true);
            updateMap.put(CIK2, false);
            input.setDiscountUpdateMap(updateMap);
            return input;
        }
    }

    @Nested
    @DisplayName("saveDiscount")
    class saveDiscountTests {

        @Test
        public void testSaveDiscountIfDiscountDoesNotExist() {
            Discount discount = new Discount();
            discount.setCik(CIK);
            when(discountRepository.existsById(CIK))
                    .thenReturn(false);
            discountService.saveDiscount(discount).block();
            verify(discountRepository, times(0)).getReferenceById(CIK);
            verify(discountRepository).save(discount);
        }

        @Test
        public void testSaveDiscountIfDiscountDoesExist() {
            Discount discount = buildValidDiscount();
            when(discountRepository.existsById(CIK))
                    .thenReturn(true);
            when(discountRepository.getReferenceById(CIK))
                    .thenReturn(discount);
            discountService.saveDiscount(discount).block();
            verify(discountRepository).getReferenceById(CIK);
            verify(discountRepository).save(discount);
        }

        @Test
        public void testExistsByIdDataAccessError() {
            Discount discount = new Discount();
            discount.setCik(CIK);
            when(discountRepository.existsById(CIK))
                    .thenThrow(mock(DataAccessException.class));
            assertThrows(DiscountOperationException.class, () ->
                    discountService.saveDiscount(discount).block());
        }

        @Test
        public void testExistsByIdSaveNewDataAccessError() {
            Discount discount = new Discount();
            discount.setCik(CIK);
            when(discountRepository.existsById(CIK))
                    .thenReturn(false);
            when(discountRepository.save(discount))
                    .thenThrow(mock(DataAccessException.class));
            assertThrows(DiscountOperationException.class, () ->
                    discountService.saveDiscount(discount).block());
        }

        @Test
        public void testExistsByIdUpdateDataAccessError() {
            Discount discount = buildValidDiscount();
            when(discountRepository.existsById(CIK))
                    .thenReturn(true);
            when(discountRepository.getReferenceById(CIK))
                    .thenReturn(discount);
            when(discountRepository.save(discount))
                    .thenThrow(mock(DataAccessException.class));
            assertThrows(DiscountOperationException.class, () ->
                    discountService.saveDiscount(discount).block());
        }

        private Discount buildValidDiscount() {
            Discount discount = new Discount();
            discount.setCik(CIK);
            discount.setSymbol(SYMBOL);
            discount.setName(NAME);
            discount.setStickerPrice(buildStickerPrice());
            discount.setBenchmarkRatioPrice(buildBenchmarkRatioPrice());
            return discount;
        }

        private StickerPrice buildStickerPrice() {
            StickerPrice stickerPrice = new StickerPrice();
            stickerPrice.setCik(CIK);
            stickerPrice.setInput(buildStickerPriceInput());
            return stickerPrice;
        }

        private StickerPriceInput buildStickerPriceInput() {
            StickerPriceInput input = new StickerPriceInput();
            input.setCik(CIK);
            input.setAnnualBVPS(Collections.emptyList());
            input.setAnnualPE(Collections.emptyList());
            input.setAnnualEPS(Collections.emptyList());
            input.setAnnualROIC(Collections.emptyList());
            input.setAnnualRevenue(Collections.emptyList());
            input.setAnnualEquity(Collections.emptyList());
            input.setAnnualOperatingCashFlow(Collections.emptyList());
            return input;
        }

        private BenchmarkRatioPrice buildBenchmarkRatioPrice() {
            BenchmarkRatioPrice ratioPrice = new BenchmarkRatioPrice();
            ratioPrice.setCik(CIK);
            ratioPrice.setPrice(1.00);
            ratioPrice.setInput(buildBenchmarkRatioPriceInput());
            return ratioPrice;
        }

        private BenchmarkRatioPriceInput buildBenchmarkRatioPriceInput() {
            BenchmarkRatioPriceInput input = new BenchmarkRatioPriceInput();
            input.setCik(CIK);
            input.setIndustry("Books");
            input.setPsBenchmarkRatio(1.67);
            input.setTtmRevenue(1000000L);
            input.setSharesOutstanding(1000000L);
            return input;
        }
    }

    @Nested
    @DisplayName("deleteDiscount")
    class deleteDiscountTests {

        @Test
        public void testDeleteDiscountSuccess() {
            when(discountRepository.existsById(CIK)).thenReturn(true);
            discountService.deleteDiscount(CIK).block();
            verify(discountRepository).deleteById(CIK);
        }

        @Test
        public void testDeleteDiscountDoesNotExist() {
            when(discountRepository.existsById(CIK)).thenReturn(false);
            assertThrows(DataNotFoundException.class, () ->
                    discountService.deleteDiscount(CIK));
        }

        @Test
        public void testDeleteDataAccessError() {
            DataAccessException ex = mock(DataAccessException.class);
            when(discountRepository.existsById(CIK)).thenThrow(ex);
            assertThrows(DiscountOperationException.class, () ->
                    discountService.deleteDiscount(CIK));
        }
    }
}
