package com.recom.commons.model.maprendererpipeline.dataprovider.village;

import lombok.NonNull;

import java.util.List;

public interface VillageProvidable {

    @NonNull
    List<VillageItem> provide();

}
