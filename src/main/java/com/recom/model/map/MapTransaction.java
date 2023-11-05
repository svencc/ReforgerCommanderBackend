package com.recom.model.map;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.event.listener.generic.TransactionalMapEntityPackable;
import com.recom.event.listener.generic.MapLocatedDto;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class MapTransaction<DTO_TYPE extends MapLocatedDto, PACKAGE_TYPE extends TransactionalMapEntityPackable<DTO_TYPE>> {

    private TransactionIdentifierDto openTransactionIdentifier;

    private TransactionIdentifierDto commitTransactionIdentifier;
    
    @NonNull
    @Builder.Default
    private List<PACKAGE_TYPE> packages = new ArrayList<>();

    public boolean isCommitted() {
        return commitTransactionIdentifier != null;
    }

}
