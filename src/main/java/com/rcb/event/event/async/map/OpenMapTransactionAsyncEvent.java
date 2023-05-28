package com.rcb.event.event.async.map;

import com.rcb.dto.map.scanner.TransactionIdentifierDto;
import com.rcb.event.event.async.RefComAsyncEvent;
import lombok.*;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class OpenMapTransactionAsyncEvent extends RefComAsyncEvent {

    @NonNull
    private final TransactionIdentifierDto transactionIdentifierDto;

}
