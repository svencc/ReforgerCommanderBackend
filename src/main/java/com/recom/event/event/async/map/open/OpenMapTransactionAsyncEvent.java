package com.recom.event.event.async.map.open;

import com.recom.dto.map.scanner.TransactionIdentifierDto;
import com.recom.event.event.async.RecomAsyncEvent;
import lombok.*;

@Getter
@RequiredArgsConstructor
public class OpenMapTransactionAsyncEvent extends RecomAsyncEvent {

    @NonNull
    private final TransactionIdentifierDto transactionIdentifierDto;

}
