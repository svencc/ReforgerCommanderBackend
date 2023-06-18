package com.recom.event.event.async.map;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.event.event.async.RecomAsyncEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommitMapTransactionAsyncEvent extends RecomAsyncEvent {

    @NonNull
    private final TransactionIdentifierDto transactionIdentifierDto;

}
