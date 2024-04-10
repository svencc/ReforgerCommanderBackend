package com.recom.service.map.scanner;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.structure.MapStructureDto;
import com.recom.dto.map.scanner.structure.TransactionalMapStructurePackageDto;
import com.recom.model.map.MapTransaction;
import com.recom.service.map.MapTransactionValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapTransactionValidatorServiceTest {

    private MapTransactionValidatorService<MapStructureDto, TransactionalMapStructurePackageDto> serviceUnderTest;

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

//    @Test
//    void testIsValidTransaction_whenTransactionHasNoPackages_shouldFail() {
//        // Arrange
//        final String sessionName = "session1";
//        final MapTransaction testTransaction = MapTransaction.builder()
//                .openTransactionIdentifier(TransactionIdentifierDto.builder().sessionIdentifier(sessionName).packageOrder(0).build())
//                .commitTransactionIdentifier(TransactionIdentifierDto.builder().sessionIdentifier(sessionName).packageOrder(1).build())
//                .packages(List.of())
//                .build();
//
//        // Act
//        boolean resultToTest = serviceUnderTest.isValidTransaction(testTransaction);
//
//        // Assert
//        assertFalse(resultToTest);
//    }

    @Test
    void testIsValidTransaction_whenTransactionHasMissingPackage_shouldFail() {
        // Arrange
        final String sessionName = "session1";
        final MapTransaction<MapStructureDto, TransactionalMapStructurePackageDto> testTransaction = MapTransaction.<MapStructureDto, TransactionalMapStructurePackageDto>builder()
                .openTransactionIdentifier(TransactionIdentifierDto.builder().sessionIdentifier(sessionName).packageOrder(0).build())
                .commitTransactionIdentifier(TransactionIdentifierDto.builder().sessionIdentifier(sessionName).packageOrder(3).build())
                .packages(List.of(
                        TransactionalMapStructurePackageDto.builder().packageOrder(1).build()
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
        final MapTransaction<MapStructureDto, TransactionalMapStructurePackageDto> testTransaction = MapTransaction.<MapStructureDto, TransactionalMapStructurePackageDto>builder()
                .openTransactionIdentifier(TransactionIdentifierDto.builder().sessionIdentifier(sessionName).packageOrder(0).build())
                .commitTransactionIdentifier(TransactionIdentifierDto.builder().sessionIdentifier(sessionName).packageOrder(3).build())
                .packages(List.of(
                        TransactionalMapStructurePackageDto.builder().packageOrder(1).build(),
                        TransactionalMapStructurePackageDto.builder().packageOrder(2).build()
                ))
                .build();

        // Act
        boolean resultToTest = serviceUnderTest.isValidTransaction(testTransaction);

        // Assert
        assertTrue(resultToTest);
    }

}