package com.facts.financial_facts_service.services.facts.components.retriever.components;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.constants.enums.Taxonomy;
import com.facts.financial_facts_service.entities.facts.models.Period;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.UnitData;
import com.facts.financial_facts_service.entities.models.QuarterlyData;
import com.facts.financial_facts_service.exceptions.FeatureNotImplementedException;
import com.facts.financial_facts_service.exceptions.InsufficientKeysException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParserTest implements TestConstants {

    @InjectMocks
    private Parser parser;

    @BeforeEach
    public void init() { MockitoAnnotations.openMocks(this); }

    @Nested
    @DisplayName("fetchReportedValues")
    class fetchReportedValuesTests {

        @Test
        public void testUsingUsGaapReport() {
            TaxonomyReports taxonomyReports = mock(TaxonomyReports.class);
            when(taxonomyReports.getPrimaryTaxonomy()).thenReturn(Taxonomy.US_GAAP);
            try {
                parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                        FACTS_KEYS, Collections.emptyList()).block();
            } catch (InsufficientKeysException ex) {
                verify(taxonomyReports).getGaap();
            }
        }

        @Test
        public void testUsingIfrsFullReport() {
            TaxonomyReports taxonomyReports = mock(TaxonomyReports.class);
            when(taxonomyReports.getPrimaryTaxonomy()).thenReturn(Taxonomy.IFRS_FULL);
            try {
                parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                        FACTS_KEYS, Collections.emptyList()).block();
            } catch (InsufficientKeysException ex) {
                verify(taxonomyReports).getIfrs();
            }
        }

        @Test
        public void testUsingDeiReport() {
            TaxonomyReports taxonomyReports = mock(TaxonomyReports.class);
            when(taxonomyReports.getPrimaryTaxonomy()).thenReturn(Taxonomy.US_GAAP);
            try {
                parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                        List.of(FACTS_KEY_1), List.of(FACTS_KEY_2)).block();
            } catch (InsufficientKeysException ex) {
                verify(taxonomyReports).getGaap();
                verify(taxonomyReports).getDei();
            }
        }
    }

    @Nested
    @DisplayName("processKeys")
    class processKeysTests {

        @Test
        public void testProcessKeysKeyNotFound() {
            TaxonomyReports taxonomyReports = new TaxonomyReports();
            taxonomyReports.setPrimaryTaxonomy(Taxonomy.US_GAAP);
            Map<String, UnitData> report = new HashMap<>();
            taxonomyReports.setGaap(report);
            assertThrows(InsufficientKeysException.class, () ->
                    parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block());
        }

        @Test
        public void testProcessKeysKeyFoundWithoutValues() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(
                    FACTS_KEYS, Taxonomy.US_GAAP);
            taxonomyReports.setPrimaryTaxonomy(Taxonomy.US_GAAP);
            UnitData mockUnitData = mock(UnitData.class);
            taxonomyReports.getGaap().put(FACTS_KEYS.get(0), mockUnitData);
            when(mockUnitData.getUnits()).thenReturn(buildUnits(USD));
            try {
                parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                        FACTS_KEYS, Collections.emptyList()).block();
            } catch (InsufficientKeysException ex) {
                verify(mockUnitData).getUnits();
            }
        }

        @Test
        public void testProcessKeysKeyFoundWithValues() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(
                    FACTS_KEYS, Taxonomy.US_GAAP);
            taxonomyReports.setPrimaryTaxonomy(Taxonomy.US_GAAP);
            UnitData mockUnitData = mockUnitData(1, USD);
            taxonomyReports.getGaap().put(FACTS_KEYS.get(0), mockUnitData);
            parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            verify(mockUnitData, times(2)).getUnits();
        }

        @Test
        public void testProcessKeysUsesKeyWithMostValues() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(
                    FACTS_KEYS, Taxonomy.US_GAAP);
            taxonomyReports.setPrimaryTaxonomy(Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitData(1, USD);
            UnitData mockUnitData2 = mockUnitData(2, USD);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            taxonomyReports.getGaap().put(FACTS_KEY_2, mockUnitData2);
            parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            verify(mockUnitData1).getUnits();
            verify(mockUnitData2, times(2)).getUnits();
        }
    }

    @Nested
    @DisplayName("parseFactsForData")
    class parseFactsForDataTests {

        @Test
        public void testParseFactsForDataChecksDeiIfNoFacts() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitData(1, USD);
            taxonomyReports.getDei().put(FACTS_KEY_1, mockUnitData1);
            parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            verify(mockUnitData1, times(2)).getUnits();
        }

        @Test
        public void testParseFactsForDataThrowsInsufficientKeysExceptionIfNoValidKeys() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            assertThrows(InsufficientKeysException.class, () -> parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    List.of("InvalidKey"), Collections.emptyList()).block());
        }

        @Test
        public void testParseFactsForDataThrowsInsufficientKeysExceptionIfValidKeysAreEmpty() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitData(0, USD);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            assertThrows(InsufficientKeysException.class, () -> parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block());
        }

        @Test
        public void testParseFactsForDataWithValidKey() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitData(2, USD);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            try {
                parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                        FACTS_KEYS, Collections.emptyList()).block();
            } catch (InsufficientKeysException ex) {
                fail();
            }
        }
    }

    @Nested
    @DisplayName("checkIsSupportedUnits")
    class checkIsSupportedUnitsTests {

        @Test
        public void testCheckIsSupportedUnitsWithSupportedUnits() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(
                    FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitData(2, USD);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            try {
                parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                        FACTS_KEYS, Collections.emptyList()).block();
            } catch (FeatureNotImplementedException ex) {
                fail();
            }
        }

        @Test
        public void testCheckIsSupportedUnitsWithUnsupportedUnits() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(
                    FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitData(2, DOGE_COIN);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            assertThrows(FeatureNotImplementedException.class, () ->
                    parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block());
        }
    }

    @Nested
    @DisplayName("checkHasStartDate")
    class checkHasStartDateTests {

        @Test
        public void testCheckHasStartDateChecksStartDate() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(
                    FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData = mockUnitData(2, USD);
            Period mockPeriod = mock(Period.class);
            mockUnitData.getUnits().get(USD).add(0, mockPeriod);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData);
            parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            verify(mockPeriod).getStart();
        }
    }

    @Nested
    @DisplayName("populateQuarterlyDataWithStartDate")
    class populateQuarterlyDataWithStartDateTests {

        @Test
        public void testSkipsPeriodsWithoutEndDates() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now(), null);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(0, actual.size());
        }

        @Test
        public void testSkipsPeriodsWithoutStartDates() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, null, LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(0, actual.size());
        }

        @Test
        public void testSkipsPeriodsWithDeltaDisparityAboveThreshold() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(106), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(0, actual.size());
        }

        @Test
        public void testIncludesValuesWithinDeltaDisparityThreshold() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(100), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
        }

        @Test
        public void testIncludesFpFYIfIsSharesAndNotWithinDeltaThreshold() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData = mockUnitDataWithStartAndEndDates(1, SHARES,
                    LocalDate.now().minusDays(106), LocalDate.now());
            Period period2 = new Period();
            period2.setFp("FY");
            period2.setStart(LocalDate.now().minusDays(110));
            period2.setEnd(LocalDate.now().minusDays(1));
            period2.setVal(BigDecimal.valueOf(-1));
            addPeriodToUnitData(mockUnitData, period2);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertEquals(BigDecimal.valueOf(-1), actual.get(0).getValue());
        }

        @Test
        public void testIncludesSumMinusFpFYIfIsNotSharesAndNotWithinDeltaThreshold() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData = mockUnitDataWithStartAndEndDates(1, USD,
                    LocalDate.now().minusDays(100), LocalDate.now());
            Period period2 = buildPeriodWithFP(LocalDate.now().minusDays(110),
                    LocalDate.now().minusDays(1));
            addPeriodToUnitData(mockUnitData, period2);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(2, actual.size());
            assertEquals(BigDecimal.valueOf(-1001), actual.get(1).getValue());
        }
    }

    @Nested
    @DisplayName("mapPeriodToQuarterlyData")
    class mapPeriodToQuarterlyDataTests {

        @Test
        public void testMapsPeriodsToQuarterlyData() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(100), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertEquals(CIK, actual.get(0).getCik());
        }

        @Test
        public void testMapsPeriodsToQuarterlyOutstandingShares() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(100), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertEquals(CIK, actual.get(0).getCik());
        }

        @Test
        public void testMapsPeriodsToQuarterlyNetIncome() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(100), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertEquals(CIK, actual.get(0).getCik());
        }

        @Test
        public void testMapsPeriodsToQuarterlyLongTermDebt() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(100), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertEquals(CIK, actual.get(0).getCik());
        }

        @Test
        public void testMapsPeriodsToQuarterlyFactsEPS() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(100), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertEquals(CIK, actual.get(0).getCik());
        }
    }

    @Nested
    @DisplayName("populateQuarterlyDataWithoutStartDate")
    class populateQuarterlyDataWithoutStartDateTests {

        @Test
        public void testSkipsPeriodsWithoutFPorFrame() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitDataWithFPorFrame(1, USD, null, null);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(0, actual.size());
        }

        @Test
        public void testIncludesPeriodsWithFpIncludesQ() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            taxonomyReports.setPrimaryTaxonomy(Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitDataWithFPorFrame(1, USD, "Q1", null);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
        }

        @Test
        public void testIncludesPeriodsWithFrameIncludesQ() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS, Taxonomy.US_GAAP);
            UnitData mockUnitData1 = mockUnitDataWithFPorFrame(1, USD, null, "FY2012Q3");
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyData> actual = parser.parseReportsForQuarterlyData(CIK, taxonomyReports,
                    FACTS_KEYS, Collections.emptyList()).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
        }
    }

    private TaxonomyReports buildTaxonomyReportsContainingKey(List<String> factsKeys,
                                                              Taxonomy primaryTaxonomy) {
        TaxonomyReports taxonomyReports = new TaxonomyReports();
        taxonomyReports.setPrimaryTaxonomy(primaryTaxonomy);
        Map<String, UnitData> report = new HashMap<>();
        UnitData unitData = new UnitData();
        report.put(factsKeys.get(0), unitData);
        taxonomyReports.setGaap(report);
        taxonomyReports.setIfrs(report);
        taxonomyReports.setDei(report);
        return taxonomyReports;
    }

    private Map<String, List<Period>> buildUnits(String currency) {
        Map<String, List<Period>> units = new HashMap<>();
        units.put(currency, Collections.emptyList());
        return units;
    }

    private UnitData mockUnitData(int numberOfPeriods, String currency) {
        UnitData mockUnitData = mock(UnitData.class);
        Map<String, List<Period>> units = buildUnits(currency);
        List<Period> periods = new ArrayList<>();
        while (numberOfPeriods > 0) {
            Period period = new Period();
            period.setVal(BigDecimal.valueOf(numberOfPeriods));
            periods.add(period);
            numberOfPeriods--;
        }
        units.put(currency, periods);
        when(mockUnitData.getUnits()).thenReturn(units);
        return mockUnitData;
    }

    private UnitData mockUnitDataWithStartAndEndDates(int numberOfPeriods, String currency,
                                                      LocalDate start, LocalDate end) {
        UnitData unitData = mockUnitData(numberOfPeriods, currency);
        Period period = unitData.getUnits().get(unitData.getUnits().keySet().stream().toList().get(0))
                .get(0);
        period.setStart(start);
        period.setEnd(end);
        period.setVal(BigDecimal.valueOf(1000));
        return unitData;
    }

    private UnitData mockUnitDataWithFPorFrame(int numberOfPeriods, String currency,
                                                      String fp, String frame) {
        UnitData unitData = mockUnitData(numberOfPeriods, currency);
        Period period = unitData.getUnits().get(unitData.getUnits().keySet().stream().toList().get(0))
                .get(0);
        period.setFp(fp);
        period.setFrame(frame);
        period.setVal(BigDecimal.valueOf(1000));
        return unitData;
    }

    private void addPeriodToUnitData(UnitData unitData, Period period) {
        String key = unitData.getUnits().keySet().stream().toList().get(0);
        unitData.getUnits().get(key).add(period);
    }

    private Period buildPeriodWithFP(LocalDate start, LocalDate end) {
        Period period = new Period();
        period.setFp("FY");
        period.setStart(start);
        period.setEnd(end);
        period.setVal(BigDecimal.valueOf(-1));
        return period;
    }

}
