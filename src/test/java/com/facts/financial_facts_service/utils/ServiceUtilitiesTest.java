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

    @Test
    public void testAssignPeriodDataCikTtmPriceData() {
        Discount discount = new Discount();
        TtmPriceData ttmPriceData = new TtmPriceData();
        discount.setTtmPriceData(List.of(ttmPriceData));
        ServiceUtilities.assignPeriodDataCik(discount, CIK);
        assertEquals(CIK, discount.getTtmPriceData().get(0).getCik());
    }

    @Test
    public void testAssignPeriodDataCikTfyPriceData() {
        Discount discount = new Discount();
        TfyPriceData tfyPriceData = new TfyPriceData();
        discount.setTfyPriceData(List.of(tfyPriceData));
        ServiceUtilities.assignPeriodDataCik(discount, CIK);
        assertEquals(CIK, discount.getTfyPriceData().get(0).getCik());
    }

    @Test
    public void testAssignPeriodDataCikTtyPriceData() {
        Discount discount = new Discount();
        TtyPriceData ttyPriceData = new TtyPriceData();
        discount.setTtyPriceData(List.of(ttyPriceData));
        ServiceUtilities.assignPeriodDataCik(discount, CIK);
        assertEquals(CIK, discount.getTtyPriceData().get(0).getCik());
    }

    @Test
    public void testAssignPeriodDataCikQuarterlyBVPS() {
        Discount discount = new Discount();
        QuarterlyBVPS quarterlyBVPS = new QuarterlyBVPS();
        discount.setQuarterlyBVPS(List.of(quarterlyBVPS));
        ServiceUtilities.assignPeriodDataCik(discount, CIK);
        assertEquals(CIK, discount.getQuarterlyBVPS().get(0).getCik());
    }

    @Test
    public void testAssignPeriodDataCikQuarterlyPE() {
        Discount discount = new Discount();
        QuarterlyPE quarterlyPE = new QuarterlyPE();
        discount.setQuarterlyPE(List.of(quarterlyPE));
        ServiceUtilities.assignPeriodDataCik(discount, CIK);
        assertEquals(CIK, discount.getQuarterlyPE().get(0).getCik());
    }

    @Test
    public void testAssignPeriodDataCikQuarterlyEPS() {
        Discount discount = new Discount();
        QuarterlyEPS quarterlyEPS = new QuarterlyEPS();
        discount.setQuarterlyEPS(List.of(quarterlyEPS));
        ServiceUtilities.assignPeriodDataCik(discount, CIK);
        assertEquals(CIK, discount.getQuarterlyEPS().get(0).getCik());
    }

    @Test
    public void testAssignPeriodDataCikQuarterlyROIC() {
        Discount discount = new Discount();
        QuarterlyROIC quarterlyROIC = new QuarterlyROIC();
        discount.setQuarterlyROIC(List.of(quarterlyROIC));
        ServiceUtilities.assignPeriodDataCik(discount, CIK);
        assertEquals(CIK, discount.getQuarterlyROIC().get(0).getCik());
    }
}
