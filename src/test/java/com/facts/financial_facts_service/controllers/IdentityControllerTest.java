package com.facts.financial_facts_service.controllers;

import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.entities.identity.Identity;
import com.facts.financial_facts_service.services.identity.IdentityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class IdentityControllerTest implements TestConstants {

    @Mock
    private IdentityService identityService;

    @InjectMocks
    private IdentityController identityController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(identityController, "identityService", identityService);
    }

    @Test
    public void testGetIdentity() throws ExecutionException, InterruptedException {
        Identity identity = new Identity();
        when(identityService.getIdentityFromIdentityMap(CIK))
                .thenReturn(Mono.just(identity));
        ResponseEntity<Identity> response = identityController.getIdentityWithCik(CIK).get();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(identity, response.getBody());
    }
}
