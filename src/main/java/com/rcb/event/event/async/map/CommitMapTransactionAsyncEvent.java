package com.rcb.event.event.async.map;

import com.rcb.dto.map.scanner.TransactionIdentifierDto;
import com.rcb.event.event.async.RefComAsyncEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class CommitMapTransactionAsyncEvent extends RefComAsyncEvent {

    @NonNull
    private final TransactionIdentifierDto transactionIdentifierDto;

}
