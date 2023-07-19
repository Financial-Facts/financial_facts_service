package com.facts.financial_facts_service.handler;

import com.facts.financial_facts_service.constants.Operation;
import com.facts.financial_facts_service.constants.TestConstants;
import com.facts.financial_facts_service.exceptions.DataNotFoundException;
import com.facts.financial_facts_service.exceptions.DiscountOperationException;
import com.facts.financial_facts_service.exceptions.FeatureNotImplementedException;
import com.facts.financial_facts_service.exceptions.InsufficientKeysException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest implements TestConstants {

    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleMethodArgumentNotValid() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("name", "name", "defaultMessage");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(400);
        WebRequest webRequest = mock(WebRequest.class);

        ResponseEntity<Object> actual =
                globalExceptionHandler.handleMethodArgumentNotValid(ex, headers, httpStatusCode, webRequest);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertEquals(List.of("defaultMessage"),  actual.getBody());
    }

    @Test
    public void testHandleConstraintViolationException() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation constraintViolation = mock(ConstraintViolation.class);
        when(ex.getConstraintViolations()).thenReturn(Set.of(constraintViolation));
        when(constraintViolation.getMessage()).thenReturn("message");
        ResponseEntity<Object> actual = globalExceptionHandler.handleConstraintViolationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertEquals(List.of("message"), actual.getBody());
    }

    @Test
    public void testHandleDataNotFoundException() {
        DataNotFoundException ex = new DataNotFoundException("message");
        ResponseEntity<Object> actual = globalExceptionHandler.handleDataNotFoundException(ex);
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertEquals("message", actual.getBody());
    }

    @Test
    public void testHandleFeatureNotImplementedException() {
        FeatureNotImplementedException ex = new FeatureNotImplementedException("message");
        ResponseEntity<Object> actual = globalExceptionHandler.handleFeatureNotImplementedException(ex);
        assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        assertEquals("message", actual.getBody());
    }

    @Test
    public void testHandleInsufficientKeysException() {
        InsufficientKeysException ex = new InsufficientKeysException("message");
        ResponseEntity<Object> actual = globalExceptionHandler.handleInsufficientKeysException(ex);
        assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        assertEquals("message", actual.getBody());
    }

    @Test
    public void testHandleResponseStatusException() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "message");
        ResponseEntity<Object> actual = globalExceptionHandler.handleResponseStatusException(ex);
        assertEquals(HttpStatus.I_AM_A_TEAPOT, actual.getStatusCode());
        assertEquals("message", actual.getBody());
    }

    @Nested
    @DisplayName("handleDiscountOperationException")
    class HandleDiscountOperationExceptionTests {

        @Test
        public void testHandleDiscountOperationExceptionAddOperation() {
            DiscountOperationException ex = new DiscountOperationException(Operation.ADD, CIK);
            ResponseEntity<Object> actual = globalExceptionHandler.handleDiscountOperationException(ex);
            assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
            assertEquals(String.format(DISCOUNT_OPERATION_ERROR, Operation.ADD, CIK), actual.getBody());
        }

        @Test
        public void testHandleDiscountOperationExceptionDeleteOperation() {
            DiscountOperationException ex = new DiscountOperationException(Operation.DELETE, CIK);
            ResponseEntity<Object> actual = globalExceptionHandler.handleDiscountOperationException(ex);
            assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
            assertEquals(String.format(DISCOUNT_OPERATION_ERROR, Operation.DELETE, CIK), actual.getBody());
        }

        @Test
        public void testHandleDiscountOperationExceptionGetOperation() {
            DiscountOperationException ex = new DiscountOperationException(Operation.GET, CIK);
            ResponseEntity<Object> actual = globalExceptionHandler.handleDiscountOperationException(ex);
            assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
            assertEquals(String.format(DISCOUNT_OPERATION_ERROR, Operation.GET, CIK), actual.getBody());
        }

        @Test
        public void testHandleDiscountOperationExceptionUpdateOperation() {
            DiscountOperationException ex = new DiscountOperationException(Operation.UPDATE, CIK);
            ResponseEntity<Object> actual = globalExceptionHandler.handleDiscountOperationException(ex);
            assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
            assertEquals(String.format(DISCOUNT_OPERATION_ERROR, Operation.UPDATE, CIK), actual.getBody());
        }

        @Test
        public void testHandleDiscountOperationExceptionBulkSimpleOperation() {
            DiscountOperationException ex = new DiscountOperationException(Operation.BULK_SIMPLE, CIK);
            ResponseEntity<Object> actual = globalExceptionHandler.handleDiscountOperationException(ex);
            assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
            assertEquals(String.format(DISCOUNT_OPERATION_ERROR, Operation.BULK_SIMPLE, CIK), actual.getBody());
        }

        @Test
        public void testHandleDiscountOperationExceptionNoCikAdd() {
            DiscountOperationException ex = new DiscountOperationException(Operation.ADD);
            ResponseEntity<Object> actual = globalExceptionHandler.handleDiscountOperationException(ex);
            assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
            assertEquals(String.format(DISCOUNT_OPERATION_ERROR_NO_CIK, Operation.ADD), actual.getBody());
        }

        @Test
        public void testHandleDiscountOperationExceptionNoCikDelete() {
            DiscountOperationException ex = new DiscountOperationException(Operation.DELETE);
            ResponseEntity<Object> actual = globalExceptionHandler.handleDiscountOperationException(ex);
            assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
            assertEquals(String.format(DISCOUNT_OPERATION_ERROR_NO_CIK, Operation.DELETE), actual.getBody());
        }

        @Test
        public void testHandleDiscountOperationExceptionNoCikGet() {
            DiscountOperationException ex = new DiscountOperationException(Operation.GET);
            ResponseEntity<Object> actual = globalExceptionHandler.handleDiscountOperationException(ex);
            assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
            assertEquals(String.format(DISCOUNT_OPERATION_ERROR_NO_CIK, Operation.GET), actual.getBody());
        }

        @Test
        public void testHandleDiscountOperationExceptionNoCikUpdate() {
            DiscountOperationException ex = new DiscountOperationException(Operation.UPDATE);
            ResponseEntity<Object> actual = globalExceptionHandler.handleDiscountOperationException(ex);
            assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
            assertEquals(String.format(DISCOUNT_OPERATION_ERROR_NO_CIK, Operation.UPDATE), actual.getBody());
        }

        @Test
        public void testHandleDiscountOperationExceptionNoCikBulkSimple() {
            DiscountOperationException ex = new DiscountOperationException(Operation.BULK_SIMPLE);
            ResponseEntity<Object> actual = globalExceptionHandler.handleDiscountOperationException(ex);
            assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
            assertEquals(String.format(DISCOUNT_OPERATION_ERROR_NO_CIK, Operation.BULK_SIMPLE), actual.getBody());
        }
    }
}
