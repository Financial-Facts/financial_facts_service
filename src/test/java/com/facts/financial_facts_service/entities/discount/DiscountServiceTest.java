package com.facts.financial_facts_service.entities.discount;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.serverResponse.DiscountResponse;
import com.facts.financial_facts_service.entities.serverResponse.ServerResponse;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class DiscountServiceTest implements TestConstants {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountService discountService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        discountService = new DiscountService(discountRepository);
    }

    @Test
    public void testGetDiscountWithCikSuccess() {
        Discount foundDiscount = new Discount();
        foundDiscount.setCik(CIK);
        when(discountRepository.findById(CIK)).thenReturn(Optional.of(foundDiscount));
        Mono<DiscountResponse> response = discountService.getDiscountByCik(CIK);
        assertEquals(response.block().getDiscount().getCik(), foundDiscount.getCik());
    }

    @Test
    public void testGetDiscountWithCikNotFound() {
        when(discountRepository.findById(CIK)).thenReturn(Optional.empty());
        try {
            discountService.getDiscountByCik(CIK);
        } catch(DataNotFoundException ex) {
            assertEquals(ex.getMessage(), DISCOUNT_NOT_FOUND_TEST);
        }
    }

}
