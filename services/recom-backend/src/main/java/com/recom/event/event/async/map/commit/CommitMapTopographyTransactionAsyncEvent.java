package com.recom.event.event.async.map.commit;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import lombok.NonNull;

public class CommitMapTopographyTransactionAsyncEvent extends CommitAsyncEventBase {

    public CommitMapTopographyTransactionAsyncEvent(@NonNull final TransactionIdentifierDto transactionIdentifierDto) {
        super(transactionIdentifierDto);
    }

}
