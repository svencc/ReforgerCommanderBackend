package com.recom.commons.model.maprendererpipeline.dataprovider.village;

import lombok.NonNull;

import java.util.List;

public interface StructureProvidable {

    @NonNull
    List<StructureItem> provide();

}
