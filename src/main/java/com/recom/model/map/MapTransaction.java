package com.recom.model.map;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.dto.map.scanner.TransactionalMapEntityPackage;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class MapTransaction<T extends TransactionalMapEntityPackage> {

    private TransactionIdentifierDto openTransactionIdentifier;

    private TransactionIdentifierDto commitTransactionIdentifier;
    
    @NonNull
    @Builder.Default
    private List<T> packages = new ArrayList<>();

    public boolean isCommitted() {
        return commitTransactionIdentifier != null;
    }

}
