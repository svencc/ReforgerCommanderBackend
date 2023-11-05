package com.recom.event.event.async.map.commit;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.event.event.async.RecomAsyncEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class CommitMapTopographyTransactionAsyncEvent extends CommitAsyncEventBase {

    public CommitMapTopographyTransactionAsyncEvent(@NonNull final TransactionIdentifierDto transactionIdentifierDto) {
        super(transactionIdentifierDto);
    }

}
