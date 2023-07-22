package com.recom.service.map.scanner;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.TransactionalEntityPackageDto;
import com.recom.model.map.MapTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapTransactionValidatorServiceTest {

    private MapTransactionValidatorService serviceUnderTest;

    @BeforeEach
    public void beforeEach() {
        serviceUnderTest = new MapTransactionValidatorService();
    }

    @Test
    void testIsValidTransaction_whenTransactionIsEmpty_shouldFail() {
        // Arrange
        final MapTransaction testTransaction = MapTransaction.builder().build();

        // Act
        boolean resultToTest = serviceUnderTest.isValidTransaction(testTransaction);

        // Assert
        assertFalse(resultToTest);
    }

    @Test
    void testIsValidTransaction_whenTransactionHasNoPackages_shouldFail() {
        // Arrange
        final String sessionName = "session1";
        final MapTransaction testTransaction = MapTransaction.builder()
                .openTransactionIdentifier(TransactionIdentifierDto.builder().sessionIdentifier(sessionName).packageOrder(0).build())
                .commitTransactionIdentifier(TransactionIdentifierDto.builder().sessionIdentifier(sessionName).packageOrder(1).build())
                .packages(List.of())
                .build();

        // Act
        boolean resultToTest = serviceUnderTest.isValidTransaction(testTransaction);

        // Assert
        assertFalse(resultToTest);
    }

    @Test
    void testIsValidTransaction_whenTransactionHasMissingPackage_shouldFail() {
        // Arrange
        final String sessionName = "session1";
        final MapTransaction testTransaction = MapTransaction.builder()
                .openTransactionIdentifier(TransactionIdentifierDto.builder().sessionIdentifier(sessionName).packageOrder(0).build())
                .commitTransactionIdentifier(TransactionIdentifierDto.builder().sessionIdentifier(sessionName).packageOrder(3).build())
                .packages(List.of(
                        TransactionalEntityPackageDto.builder().packageOrder(1).build()
                ))
                .build();

        // Act
        boolean resultToTest = serviceUnderTest.isValidTransaction(testTransaction);

        // Assert
        assertFalse(resultToTest);
    }

    @Test
    void testIsValidTransaction_whenTransactionIsValid_shouldPass() {
        // Arrange
        final String sessionName = "session1";
        final MapTransaction testTransaction = MapTransaction.builder()
                .openTransactionIdentifier(TransactionIdentifierDto.builder().sessionIdentifier(sessionName).packageOrder(0).build())
                .commitTransactionIdentifier(TransactionIdentifierDto.builder().sessionIdentifier(sessionName).packageOrder(3).build())
                .packages(List.of(
                        TransactionalEntityPackageDto.builder().packageOrder(1).build(),
                        TransactionalEntityPackageDto.builder().packageOrder(2).build()
                ))
                .build();

        // Act
        boolean resultToTest = serviceUnderTest.isValidTransaction(testTransaction);

        // Assert
        assertTrue(resultToTest);
    }

}