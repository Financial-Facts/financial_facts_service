package com.facts.financial_facts_service.services.identity;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.entities.identity.models.BulkIdentitiesRequest;
import com.facts.financial_facts_service.entities.identity.models.SortBy;
import com.facts.financial_facts_service.entities.identity.models.SortOrder;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.exceptions.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class IdentityServiceTest implements TestConstants {

    private ConcurrentHashMap<String, Identity> identityMap;

    @InjectMocks
    private IdentityService identityService;

    @BeforeEach
    public void init() {
        identityService = new IdentityService();
        identityMap = new ConcurrentHashMap<>();
        identityService.init();
        ReflectionTestUtils.setField(identityService, "identityMap", identityMap);
    }

    @Nested
    @DisplayName("getIdentityFromIdentityMap")
    class getIdentityFromIdentityMapTests {

        @Test
        public void testGetIdentityFromIdentityMapNotFound() {
            assertThrows(DataNotFoundException.class, () -> identityService.getIdentityFromIdentityMap(CIK).block());
        }

        @Test
        public void testGetIdentityFromIdentityMapSuccess() {
            Identity identity = new Identity();
            identity.setCik(CIK);
            identity.setName(NAME);
            identity.setSymbol(SYMBOL);
            identityMap.put(CIK, identity);
            Identity actual = identityService.getIdentityFromIdentityMap(CIK).block();
            assertNotNull(actual);
            assertNotNull(actual.getCik());
            assertEquals(CIK, actual.getCik());
            assertNotNull(actual.getSymbol());
            assertEquals(SYMBOL, actual.getSymbol());
            assertNotNull(actual.getName());
            assertEquals(NAME, actual.getName());
        }
    }

    @Nested
    @DisplayName("getBulkIdentities")
    class getBulkIdentitiesTests {

        @Test
        public void testStartIndexGreaterThanLimitBadRequest() {
            BulkIdentitiesRequest request = new BulkIdentitiesRequest();
            request.setStartIndex(10);
            request.setLimit(1);
            assertThrows(InvalidRequestException.class, () ->
                    identityService.getBulkIdentities(request).block());
        }

        @Test
        public void testStartIndexGreaterThanMapSize() {
            BulkIdentitiesRequest request = new BulkIdentitiesRequest();
            request.setStartIndex(10);
            request.setLimit(15);
            mockIdentityMap(1);
            assertThrows(InvalidRequestException.class, () ->
                    identityService.getBulkIdentities(request).block());
        }

        @Test
        public void testNullSortBy() {
            BulkIdentitiesRequest request = new BulkIdentitiesRequest();
            request.setStartIndex(10);
            request.setLimit(15);
            mockIdentityMap(15);
            assertThrows(InvalidRequestException.class, () ->
                    identityService.getBulkIdentities(request).block());
        }

        @Test
        public void testLimitUnderNumOfIdentities() {
            mockIdentityMap(15);
            BulkIdentitiesRequest request = buildValidBulkIdentitiesRequest();
            List<Identity> actual = identityService.getBulkIdentities(request).block();
            assertNotNull(actual);
            assertEquals(10, actual.size());
        }

        @Test
        public void testLimitOverNumOfIdentities() {
            mockIdentityMap(5);
            BulkIdentitiesRequest request = buildValidBulkIdentitiesRequest();
            List<Identity> actual = identityService.getBulkIdentities(request).block();
            assertNotNull(actual);
            assertEquals(5, actual.size());
        }

        @Test
        public void testSortByCIKAscending() {
            mockIdentityMap(10);
            BulkIdentitiesRequest request = buildValidBulkIdentitiesRequest();
            request.setSortBy(SortBy.CIK);
            List<Identity> actual = identityService.getBulkIdentities(request).block();
            assertNotNull(actual);
            assertEquals("0", actual.get(0).getCik());
        }

        @Test
        public void testSortByCIKDescending() {
            mockIdentityMap(10);
            BulkIdentitiesRequest request = buildValidBulkIdentitiesRequest();
            request.setSortBy(SortBy.CIK);
            request.setOrder(SortOrder.DESC);
            List<Identity> actual = identityService.getBulkIdentities(request).block();
            assertNotNull(actual);
            assertEquals("9", actual.get(0).getCik());
        }

        @Test
        public void testSortByNameAscending() {
            mockIdentityMap(10);
            BulkIdentitiesRequest request = buildValidBulkIdentitiesRequest();
            request.setSortBy(SortBy.NAME);
            List<Identity> actual = identityService.getBulkIdentities(request).block();
            assertNotNull(actual);
            assertEquals("0", actual.get(0).getName());
        }

        @Test
        public void testSortByNameDescending() {
            mockIdentityMap(10);
            BulkIdentitiesRequest request = buildValidBulkIdentitiesRequest();
            request.setSortBy(SortBy.NAME);
            request.setOrder(SortOrder.DESC);
            List<Identity> actual = identityService.getBulkIdentities(request).block();
            assertNotNull(actual);
            assertEquals("9", actual.get(0).getName());
        }

        @Test
        public void testSortBySymbolAscending() {
            mockIdentityMap(10);
            BulkIdentitiesRequest request = buildValidBulkIdentitiesRequest();
            request.setSortBy(SortBy.SYMBOL);
            List<Identity> actual = identityService.getBulkIdentities(request).block();
            assertNotNull(actual);
            assertEquals("0", actual.get(0).getSymbol());
        }

        @Test
        public void testSortBySymbolDescending() {
            mockIdentityMap(10);
            BulkIdentitiesRequest request = buildValidBulkIdentitiesRequest();
            request.setSortBy(SortBy.SYMBOL);
            request.setOrder(SortOrder.DESC);
            List<Identity> actual = identityService.getBulkIdentities(request).block();
            assertNotNull(actual);
            assertEquals("9", actual.get(0).getSymbol());
        }

        private BulkIdentitiesRequest buildValidBulkIdentitiesRequest() {
            BulkIdentitiesRequest request = new BulkIdentitiesRequest();
            request.setStartIndex(0);
            request.setLimit(10);
            request.setSortBy(SortBy.CIK);
            return request;
        }
    }

    private void mockIdentityMap(int numOfIdentities) {
        int i = 0;
        while (i < numOfIdentities*3){
            String value = String.valueOf(i);
            identityMap.put(value, buildIdentityWithValue(value));
            i += 3;
        }
    }

    private Identity buildIdentityWithValue(String value) {
        Identity identity = new Identity();
        identity.setCik(value);
        identity.setName(value);
        identity.setSymbol(value);
        return identity;
    }
}
