package com.recom.commons.model.maprendererpipeline.dataprovider.forest;

import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialItemProvidable;
import lombok.NonNull;

import java.util.List;

public interface ForestProvidable extends SpacialItemProvidable<ForestItem> {

    @NonNull
    List<ForestItem> provide();

}
