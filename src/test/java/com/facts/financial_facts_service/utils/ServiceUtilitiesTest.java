package com.facts.financial_facts_service.utils;

import com.facts.financial_facts_service.entities.discount.Discount;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyBVPS;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyEPS;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyPE;
import com.facts.financial_facts_service.entities.discount.models.quarterlyData.QuarterlyROIC;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.TfyPriceData;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.TtmPriceData;
import com.facts.financial_facts_service.entities.discount.models.trailingPriceData.TtyPriceData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.facts.financial_facts_service.constants.TestConstants.CIK;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ServiceUtilitiesTest {

    @Test
    public void testPadSimpleCikWithValidInput() {
        String simpleCik = "12345";
        String expectedResult = "CIK0000012345";
        String actualResult = ServiceUtilities.padSimpleCik(simpleCik);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testPadSimpleCikWithInputAlreadyPadded() {
        String simpleCik = "0012345";
        String expectedResult = "CIK0000012345";
        String actualResult = ServiceUtilities.padSimpleCik(simpleCik);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testPadSimpleCikWithInputTooShort() {
        String simpleCik = "123";
        String expectedResult = "CIK0000000123";
        String actualResult = ServiceUtilities.padSimpleCik(simpleCik);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testPadSimpleCikWithInputTooLong() {
        String simpleCik = "1234567890123456";
        String expectedResult = "CIK1234567890";
        String actualResult = ServiceUtilities.padSimpleCik(simpleCik);
        assertEquals(expectedResult, actualResult);
    }
}
