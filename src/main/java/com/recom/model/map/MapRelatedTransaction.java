//package com.recom.model.map;
//
//import com.recom.dto.map.scanner.TransactionIdentifierDto;
//import com.recom.event.listener.generic.maprelated.MapRelatedDto;
//import com.recom.event.listener.generic.maprelated.TransactionalMapRelatedEntityPackable;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NonNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//@Builder
//public class MapRelatedTransaction<DTO_TYPE extends MapRelatedDto, PACKAGE_TYPE extends TransactionalMapRelatedEntityPackable<DTO_TYPE>> {
//
//    private TransactionIdentifierDto openTransactionIdentifier;
//
//    private TransactionIdentifierDto commitTransactionIdentifier;
//
//    @NonNull
//    @Builder.Default
//    private List<PACKAGE_TYPE> packages = new ArrayList<>();
//
//    public boolean isCommitted() {
//        return commitTransactionIdentifier != null;
//    }
//
//}
