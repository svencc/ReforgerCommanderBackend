package com.recom.event.event.async.map.open;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class OpenMapTopographyTransactionAsyncEvent extends OpenMapTransactionAsyncEventBase {

    public OpenMapTopographyTransactionAsyncEvent(@NonNull final TransactionIdentifierDto transactionIdentifierDto) {
        super(transactionIdentifierDto);
    }

}
