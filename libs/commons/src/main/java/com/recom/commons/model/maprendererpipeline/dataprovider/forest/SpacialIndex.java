package com.recom.commons.model.maprendererpipeline.dataprovider.forest;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;


public class SpacialIndex<T> {

    @NonNull
    private final List<T>[][] index;
    @Getter
    private final int width;
    @Getter
    private final int height;


    @SuppressWarnings("unchecked")
    public SpacialIndex(
            final int width,
            final int height
    ) {
        this.width = width;
        this.height = height;
        this.index = (List<T>[][]) new List[width][height];

        preInitializeIndex(width, height);
    }

    private void preInitializeIndex(
            final int width,
            final int height
    ) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.index[x][y] = new ArrayList<T>();
            }
        }
    }

    public void put(
            final int x,
            final int y,
            @NonNull final T value
    ) {
        index[x][y].add(value);
    }

    @NonNull
    public List<T> get(
            final int x,
            final int y
    ) {
        return index[x][y];
    }
    
}

