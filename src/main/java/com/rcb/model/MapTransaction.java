package com.rcb.model;

import com.rcb.dto.map.scanner.EntityPackageDto;
import com.rcb.dto.map.scanner.TransactionIdentifierDto;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class MapTransaction {

    private TransactionIdentifierDto openTransactionIdentifier;
    
    private TransactionIdentifierDto commitTransactionIdentifier;
    @Builder.Default
    private List<EntityPackageDto> packages = new ArrayList<>();

}
