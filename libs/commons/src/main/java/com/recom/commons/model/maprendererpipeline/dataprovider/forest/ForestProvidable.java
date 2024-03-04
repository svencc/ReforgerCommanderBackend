package com.recom.commons.model.maprendererpipeline.dataprovider.forest;

import lombok.NonNull;

import java.util.List;

public interface ForestProvidable {

    @NonNull
    List<ForestItem> provide();

}
