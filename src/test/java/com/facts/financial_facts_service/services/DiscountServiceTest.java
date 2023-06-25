package com.facts.financial_facts_service.services;

import com.facts.financial_facts_service.constants.Constants;
import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.exceptions.DiscountOperationException;
import com.facts.financial_facts_service.repositories.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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

    @Test
    public void testGetDiscountWithCikSuccess() {
        Discount foundDiscount = new Discount();
        foundDiscount.setCik(CIK);
        when(discountRepository.findById(CIK)).thenReturn(Optional.of(foundDiscount));
        Mono<Discount> response = discountService.getDiscountByCik(CIK);
        assertEquals(response.block().getCik(), foundDiscount.getCik());
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

    @Test
    public void testGetDiscountWithDataAccessFailure() {
        DataAccessException ex = mock(DataAccessException.class);
        when(discountRepository.findById(CIK)).thenThrow(ex);
        assertThrows(DiscountOperationException.class, () -> {
            discountService.getDiscountByCik(CIK);
        });
    }

    @Test
    public void testAddNewDiscountSuccess() {
        Discount discount = new Discount();
        discount.setCik(CIK);
        when(discountRepository.existsById(CIK)).thenReturn(false);
        when(discountRepository.save(discount)).thenReturn(discount);
        String response = discountService.addNewDiscount(discount).block();
        assertEquals(Constants.DISCOUNT_ADDED, response);
    }

    @Test
    public void testAddNewDiscountThatExists() {
        Discount discount = new Discount();
        discount.setCik(CIK);
        when(discountRepository.existsById(CIK)).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> {
            discountService.addNewDiscount(discount);
        });
    }

    @Test
    public void testAddNewDiscountDataAccessFailure() {
        Discount discount = new Discount();
        DataAccessException ex = mock(DataAccessException.class);
        when(discountRepository.save(discount)).thenThrow(ex);
        assertThrows(DiscountOperationException.class, () -> {
            discountService.addNewDiscount(discount);
        });
    }

    @Test
    public void testUpdateDiscountSuccess() {
        Discount discount = new Discount();
        discount.setCik(CIK);
        when(discountRepository.existsById(CIK)).thenReturn(true);
        when(discountRepository.save(discount)).thenReturn(discount);
        String response = discountService.updateDiscount(discount).block();
        assertEquals(Constants.DISCOUNT_UPDATED, response);
    }

    @Test
    public void testUpdateDiscountDoesntExist() {
        Discount discount = new Discount();
        discount.setCik(CIK);
        when(discountRepository.existsById(CIK)).thenReturn(false);
        assertThrows(DataNotFoundException.class, () -> {
            discountService.updateDiscount(discount);
        });
    }

    @Test
    public void testUpdateDiscountDataAccessFailure() {
        Discount discount = new Discount();
        discount.setCik(CIK);
        DataAccessException ex = mock(DataAccessException.class);
        when(discountRepository.save(discount)).thenThrow(ex);
        when(discountRepository.existsById(CIK)).thenReturn(true);
        assertThrows(DiscountOperationException.class, () -> {
            discountService.updateDiscount(discount);
        });
    }

    @Test
    public void testDeleteDiscountSuccess() {
        when(discountRepository.existsById(CIK)).thenReturn(true);
        String response = discountService.deleteDiscount(CIK).block();
        assertEquals(Constants.DISCOUNT_DELETED, response);
    }

    @Test
    public void testDeleteDiscountDoesntExist() {
        when(discountRepository.existsById(CIK)).thenReturn(false);
        assertThrows(DataNotFoundException.class, () -> {
            discountService.deleteDiscount(CIK);
        });
    }

    @Test
    public void testDeleteDataAccessExceptionFailure() {
        DataAccessException ex = mock(DataAccessException.class);
        when(discountRepository.existsById(CIK)).thenThrow(ex);
        assertThrows(DiscountOperationException.class, () -> {
            discountService.deleteDiscount(CIK);
        });
    }

}
