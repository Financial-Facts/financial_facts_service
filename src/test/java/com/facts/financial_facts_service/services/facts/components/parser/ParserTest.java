package com.facts.financial_facts_service.services.facts.components.parser;

import com.facts.financial_facts_service.constants.Taxonomy;
import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.facts.models.Period;
import com.facts.financial_facts_service.entities.facts.models.TaxonomyReports;
import com.facts.financial_facts_service.entities.facts.models.UnitData;
import com.facts.financial_facts_service.entities.facts.models.quarterlyData.*;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        public void testTaxonomyReportNotFound() {
            TaxonomyReports taxonomyReports = new TaxonomyReports();
            assertThrows(DataNotFoundException.class, () -> {
                parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                        FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            });
        }

        @Test
        public void testUsingUsGaapReport() {
            TaxonomyReports taxonomyReports = mock(TaxonomyReports.class);
            try {
                parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                        FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            } catch (InsufficientKeysException ex) {
                verify(taxonomyReports, times(1)).getGaap();
            }
        }

        @Test
        public void testUsingIfrsFullReport() {
            TaxonomyReports taxonomyReports = mock(TaxonomyReports.class);
            try {
                parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.IFRS_FULL,
                        FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            } catch (InsufficientKeysException ex) {
                verify(taxonomyReports, times(1)).getIfrs();
            }
        }

        @Test
        public void testUsingDeiReport() {
            TaxonomyReports taxonomyReports = mock(TaxonomyReports.class);
            try {
                parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                        List.of(FACTS_KEY_1), List.of(FACTS_KEY_2), QuarterlyShareholderEquity.class).block();
            } catch (InsufficientKeysException ex) {
                verify(taxonomyReports, times(1)).getGaap();
                verify(taxonomyReports, times(1)).getDei();
            }
        }
    }

    @Nested
    @DisplayName("processKeys")
    class processKeysTests {

        @Test
        public void testProcessKeysKeyNotFound() {
            TaxonomyReports taxonomyReports = new TaxonomyReports();
            Map<String, UnitData> report = new HashMap<>();
            taxonomyReports.setGaap(report);
            assertThrows(InsufficientKeysException.class, () -> {
                parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                        FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            });
        }

        @Test
        public void testProcessKeysKeyFoundWithoutValues() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData = mock(UnitData.class);
            taxonomyReports.getGaap().put(FACTS_KEYS.get(0), mockUnitData);
            when(mockUnitData.getUnits()).thenReturn(buildUnits(USD));
            try {
                parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                        FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            } catch (InsufficientKeysException ex) {
                verify(mockUnitData, times(1)).getUnits();
            }
        }

        @Test
        public void testProcessKeysKeyFoundWithValues() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData = mockUnitData(1, USD);
            taxonomyReports.getGaap().put(FACTS_KEYS.get(0), mockUnitData);
            parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            verify(mockUnitData, times(2)).getUnits();
        }

        @Test
        public void testProcessKeysUsesKeyWithMostValues() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitData(1, USD);
            UnitData mockUnitData2 = mockUnitData(2, USD);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            taxonomyReports.getGaap().put(FACTS_KEY_2, mockUnitData2);
            parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            verify(mockUnitData1, times(1)).getUnits();
            verify(mockUnitData2, times(2)).getUnits();
        }
    }

    @Nested
    @DisplayName("parseFactsForData")
    class parseFactsForDataTests {

        @Test
        public void testParseFactsForDataChecksDeiIfNoFacts() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitData(1, USD);
            taxonomyReports.getDei().put(FACTS_KEY_1, mockUnitData1);
            parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            verify(mockUnitData1, times(2)).getUnits();
        }

        @Test
        public void testParseFactsForDataThrowsInsufficientKeysExceptionIfNoValidKeys() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            assertThrows(InsufficientKeysException.class, () -> {
                parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                        List.of("InvalidKey"), Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            });
        }

        @Test
        public void testParseFactsForDataThrowsInsufficientKeysExceptionIfValidKeysAreEmpty() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitData(0, USD);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            assertThrows(InsufficientKeysException.class, () -> {
                parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                        FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            });
        }

        @Test
        public void testParseFactsForDataWithValidKey() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitData(2, USD);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            try {
                parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                        FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
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
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitData(2, USD);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            try {
                parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                        FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            } catch (FeatureNotImplementedException ex) {
                fail();
            }
        }

        @Test
        public void testCheckIsSupportedUnitsWithUnsupportedUnits() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitData(2, DOGE_COIN);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            assertThrows(FeatureNotImplementedException.class, () -> {
                parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                        FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            });
        }
    }

    @Nested
    @DisplayName("checkHasStartDate")
    class checkHasStartDateTests {

        @Test
        public void testCheckHasStartDateChecksStartDate() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData = mockUnitData(2, USD);
            Period mockPeriod = mock(Period.class);
            mockUnitData.getUnits().get(USD).add(0, mockPeriod);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData);
            parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            verify(mockPeriod, times(1)).getStart();
        }
    }

    @Nested
    @DisplayName("populateQuarterlyDataWithStartDate")
    class populateQuarterlyDataWithStartDateTests {

        @Test
        public void testSkipsPeriodsWithoutEndDates() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now(), null);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyShareholderEquity> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            assertNotNull(actual);
            assertEquals(0, actual.size());
        }

        @Test
        public void testSkipsPeriodsWithoutStartDates() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, null, LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyShareholderEquity> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            assertNotNull(actual);
            assertEquals(0, actual.size());
        }

        @Test
        public void testSkipsPeriodsWithDeltaDisparityAboveThreshold() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(106), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyShareholderEquity> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            assertNotNull(actual);
            assertEquals(0, actual.size());
        }

        @Test
        public void testIncludesValuesWithinDeltaDisparityThreshold() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(100), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyShareholderEquity> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
        }

        @Test
        public void testIncludesFpFYIfIsSharesAndNotWithinDeltaThreshold() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData = mockUnitDataWithStartAndEndDates(1, SHARES,
                    LocalDate.now().minusDays(106), LocalDate.now());
            Period period2 = new Period();
            period2.setFp("FY");
            period2.setStart(LocalDate.now().minusDays(110));
            period2.setEnd(LocalDate.now().minusDays(1));
            period2.setVal(BigDecimal.valueOf(-1));
            addPeriodToUnitData(mockUnitData, period2);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData);
            List<QuarterlyShareholderEquity> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertEquals(BigDecimal.valueOf(-1), actual.get(0).getValue());
        }

        @Test
        public void testIncludesSumMinusFpFYIfIsNotSharesAndNotWithinDeltaThreshold() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData = mockUnitDataWithStartAndEndDates(1, USD,
                    LocalDate.now().minusDays(100), LocalDate.now());
            Period period2 = buildPeriodWithFP(LocalDate.now().minusDays(110),
                    LocalDate.now().minusDays(1));
            addPeriodToUnitData(mockUnitData, period2);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData);
            List<QuarterlyShareholderEquity> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            assertNotNull(actual);
            assertEquals(2, actual.size());
            assertEquals(BigDecimal.valueOf(-1001), actual.get(1).getValue());
        }
    }

    @Nested
    @DisplayName("mapPeriodToQuarterlyData")
    class mapPeriodToQuarterlyDataTests {

        @Test
        public void testMapsPeriodsToQuarterlyShareholderEquity() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(100), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyShareholderEquity> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertInstanceOf(QuarterlyShareholderEquity.class, actual.get(0));
            assertEquals(CIK, actual.get(0).getCik());
        }

        @Test
        public void testMapsPeriodsToQuarterlyOutstandingShares() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(100), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyOutstandingShares> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyOutstandingShares.class).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertInstanceOf(QuarterlyOutstandingShares.class, actual.get(0));
            assertEquals(CIK, actual.get(0).getCik());
        }

        @Test
        public void testMapsPeriodsToQuarterlyNetIncome() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(100), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyNetIncome> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyNetIncome.class).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertInstanceOf(QuarterlyNetIncome.class, actual.get(0));
            assertEquals(CIK, actual.get(0).getCik());
        }

        @Test
        public void testMapsPeriodsToQuarterlyLongTermDebt() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(100), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyLongTermDebt> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyLongTermDebt.class).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertInstanceOf(QuarterlyLongTermDebt.class, actual.get(0));
            assertEquals(CIK, actual.get(0).getCik());
        }

        @Test
        public void testMapsPeriodsToQuarterlyFactsEPS() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitDataWithStartAndEndDates(1, USD, LocalDate.now().minusDays(100), LocalDate.now());
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyFactsEPS> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyFactsEPS.class).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
            assertInstanceOf(QuarterlyFactsEPS.class, actual.get(0));
            assertEquals(CIK, actual.get(0).getCik());
        }
    }

    @Nested
    @DisplayName("populateQuarterlyDataWithoutStartDate")
    class populateQuarterlyDataWithoutStartDateTests {

        @Test
        public void testSkipsPeriodsWithoutFPorFrame() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitDataWithFPorFrame(1, USD, null, null);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyShareholderEquity> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            assertNotNull(actual);
            assertEquals(0, actual.size());
        }

        @Test
        public void testIncludesPeriodsWithFpIncludesQ() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitDataWithFPorFrame(1, USD, "Q1", null);
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyShareholderEquity> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
        }

        @Test
        public void testIncludesPeriodsWithFrameIncludesQ() {
            TaxonomyReports taxonomyReports = buildTaxonomyReportsContainingKey(FACTS_KEYS);
            UnitData mockUnitData1 = mockUnitDataWithFPorFrame(1, USD, null, "FY2012Q3");
            taxonomyReports.getGaap().put(FACTS_KEY_1, mockUnitData1);
            List<QuarterlyShareholderEquity> actual = parser.retrieveQuarterlyData(CIK, taxonomyReports, Taxonomy.US_GAAP,
                    FACTS_KEYS, Collections.emptyList(), QuarterlyShareholderEquity.class).block();
            assertNotNull(actual);
            assertEquals(1, actual.size());
        }
    }

    private TaxonomyReports buildTaxonomyReportsContainingKey(List<String> factsKeys) {
        TaxonomyReports taxonomyReports = new TaxonomyReports();
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
