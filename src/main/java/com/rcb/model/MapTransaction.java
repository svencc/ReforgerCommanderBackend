package com.rcb.model;

import com.rcb.dto.map.scanner.TransactionIdentifierDto;
import com.rcb.dto.map.scanner.TransactionalEntityPackageDto;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class MapTransaction {

    private TransactionIdentifierDto openTransactionIdentifier;

    private TransactionIdentifierDto commitTransactionIdentifier;
    @NonNull
    @Builder.Default
    private List<TransactionalEntityPackageDto> packages = new ArrayList<>();

    public boolean isCommitted() {
        return commitTransactionIdentifier != null;
    }

}
