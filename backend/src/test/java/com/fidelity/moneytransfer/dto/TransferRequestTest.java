package com.fidelity.moneytransfer.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransferRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRequest() {
        TransferRequest request = new TransferRequest(
                "FIDPY100001",
                "FIDPY100002",
                new BigDecimal("100.00"),
                UUID.randomUUID().toString(),
                "Payment for services"
        );

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Should not have validation errors");
    }

    @Test
    void testInvalidAmount() {
        // Amount is negative (-100), which violates @DecimalMin
        TransferRequest request = new TransferRequest(
                "FIDPY100001",
                "FIDPY100002",
                new BigDecimal("-100.00"),
                UUID.randomUUID().toString(),
                null
        );

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Should have validation error for negative amount");
        assertEquals("Transfer amount must be greater than 0", violations.iterator().next().getMessage());
    }

    @Test
    void testNullFields() {
        TransferRequest request = new TransferRequest(null, null, null, null, null);

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);

        assertEquals(4, violations.size(), "Should verify all 4 fields are not null");
    }
}