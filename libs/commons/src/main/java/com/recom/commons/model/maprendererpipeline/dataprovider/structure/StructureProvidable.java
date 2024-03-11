package com.recom.commons.model.maprendererpipeline.dataprovider.structure;

import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialItemProvidable;
import lombok.NonNull;

import java.util.List;

public interface StructureProvidable extends SpacialItemProvidable<StructureItem> {

    @NonNull
    List<StructureItem> provide();

}
