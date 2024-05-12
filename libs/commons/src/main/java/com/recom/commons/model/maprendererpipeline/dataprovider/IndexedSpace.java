package com.recom.commons.model.maprendererpipeline.dataprovider;

import lombok.NonNull;

import java.util.List;


public interface IndexedSpace<T> {

    int getMapWidthInMeter();

    int getMapHeightInMeter();

    int getNrCellsWidth();

    int getNrCellsHeight();

    double getCellSizeInMeter();

    double getItemsPerSquareMeterThreshold();

    double getCellSizeInSquareMeter();


    void put(
            final double spaceX,
            final double spaceY,
            @NonNull final T value
    );

    @NonNull
    List<T> getItemsInCell(
            final int x,
            final int y
    );


    @NonNull
    List<T> getItemsInSpace(
            final int x,
            final int y
    );

    @NonNull
    List<T> getItemsInSpace(
            final double spaceX,
            final double spaceY
    );

    int countItemsInIndex();

    boolean isItemThresholdExceeded(
            final int cellX,
            final int cellY
    );

    boolean isInCellRange(
            final int cellX,
            final int cellY
    );

    boolean isOutCellRange(
            final int cellX,
            final int cellY
    );

    boolean isInRange(
            final double spaceX,
            final double spaceY
    );

    boolean isOutRange(
            final double spaceX,
            final double spaceY
    );

}

