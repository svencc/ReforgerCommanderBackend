package com.recom.commons.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Partition {

    @NonNull
    public <T> List<List<T>> from(
            @NonNull final List<T> list,
            final int partitionSize
    ) {
        final List<List<T>> partitions = new ArrayList<>();

        for (int fromIndex = 0; fromIndex < list.size(); fromIndex += partitionSize) {
            final int toIndex = Math.min(fromIndex + partitionSize, list.size());
            final List<T> partition = list.subList(fromIndex, toIndex);
            partitions.add(partition);
        }

        return partitions;
    }

}