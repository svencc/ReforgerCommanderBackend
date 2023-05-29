package com.rcb.service.map;

import com.rcb.dto.map.scanner.TransactionalEntityPackageDto;
import com.rcb.model.MapTransaction;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapTransactionValidatorService {

    public boolean isValidTransaction(@NonNull final MapTransaction transaction) {
        // Open Transaction Message has to be present!
        if (transaction.getOpenTransactionIdentifier() == null) {
            log.warn("Required open transaction messages not found!");
            return false;
        }
        // Commit Transaction Message has to be present!
        if (transaction.getCommitTransactionIdentifier() == null) {
            log.warn("Required commit transaction messages not found!");
            return false;
        }
        // OpenTransactionPackage number has to be 0
        if (transaction.getOpenTransactionIdentifier().getPackageOrder() != 0) {
            log.warn("Open transaction messages packageOrder has to be 0! => {}", transaction.getOpenTransactionIdentifier().getPackageOrder());
            return false;
        }
        // CommitTransactionIdentifier number has to be greater than 0 or 1
        if (transaction.getCommitTransactionIdentifier().getPackageOrder() <= 1) {
            log.warn("Commit transaction messages packageOrder has to be at least 2! => {}", transaction.getCommitTransactionIdentifier().getPackageOrder());
            return false;
        }

        // Get commitPackageNumber - should be the highest/last number!
        final Integer commitPackageNumber = transaction.getCommitTransactionIdentifier().getPackageOrder();
        final Integer lastPackageNumber = commitPackageNumber - 1;

        // Number of submitted packages must be: (commitPackageNumber - 1)
        if (transaction.getPackages().size() != lastPackageNumber) {
            log.warn("Package size does not match required size {} => {}", transaction.getPackages().size(), lastPackageNumber);
            return false;
        }

        // Predict expected package order checksum
        final Integer expectedPackageOrderChecksum = IntStream.range(0, commitPackageNumber).sum();

        // Calculate actual package order checksum
        final Integer calculatedPackageOrderChecksum = transaction.getPackages().stream()
                .map(TransactionalEntityPackageDto::getPackageOrder)
                .distinct()
                .sorted()
                .mapToInt(packageOrder -> packageOrder)
                .sum();

        boolean isChecksumValid = expectedPackageOrderChecksum.equals(calculatedPackageOrderChecksum);

        if (isChecksumValid) {
            log.info(
                    "Expected sum {} vs. actual sum {} => valid",
                    expectedPackageOrderChecksum,
                    calculatedPackageOrderChecksum
            );
        } else {
            log.error(
                    "Expected sum {} vs. actual sum {} => invalid",
                    expectedPackageOrderChecksum,
                    calculatedPackageOrderChecksum
            );
        }

        return isChecksumValid;
    }

}
