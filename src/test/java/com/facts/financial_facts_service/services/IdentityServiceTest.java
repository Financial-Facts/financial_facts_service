package com.facts.financial_facts_service.services;

import com.facts.financial_facts_service.components.IdentityMap;
import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IdentityServiceTest implements TestConstants {

    @Mock
    private IdentityMap identityMap;

    @InjectMocks
    private IdentityService identityService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(identityService, "identityMap", identityMap);
    }

    @Test
    public void testGetSymbolFromIdentityMapSuccess() {
        Identity identity = new Identity();
        when(identityMap.getValue(CIK))
                .thenReturn(Mono.just(Optional.of(identity)));
        ResponseEntity<Identity> response = identityService.getIdentityFromIdentityMap(CIK).block();
        assertEquals(identity, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetSymbolFromIdentityMapNotFound() {
        when(identityMap.getValue(CIK))
                .thenReturn(Mono.just(Optional.empty()));
        assertThrows(DataNotFoundException.class, () -> {
            identityService.getIdentityFromIdentityMap(CIK).block();
        });
    }

    @Test
    public void testGetSymbolFromIdentityMapFailure() {
        DataAccessException ex = mock(DataAccessException.class);
        when(identityMap.getValue(CIK))
                .thenThrow(ex);
        assertThrows(ResponseStatusException.class, () -> {
            identityService.getIdentityFromIdentityMap(CIK).block();
        });
    }
}
