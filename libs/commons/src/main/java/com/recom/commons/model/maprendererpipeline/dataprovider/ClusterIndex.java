package com.recom.commons.model.maprendererpipeline.dataprovider;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class ClusterIndex<T> implements IndexedSpace<T> {

    @Getter
    @NonNull
    private final SpacialIndex<T> spacialIndex;
    @NonNull
    private final ClusterNode[][] nodeIndex;
    @NonNull
    private final Map<Integer, ClusterNode> clusters = new HashMap<>();
    @NonNull
    private final Map<ClusterNode, Integer> reverseClusterLookupIndex = new HashMap<>();


    @NonNull
    public static <U> ClusterIndex<U> from(@NonNull final SpacialIndex<U> spacialIndex
    ) {
        final ClusterNode[][] nodeIndex = new ClusterNode[spacialIndex.getNrCellsWidth() + 1][spacialIndex.getNrCellsHeight() + 1];

        return new ClusterIndex<U>(spacialIndex, nodeIndex);
    }

    @NonNull
    public ClusterNode getNode(
            final int x,
            final int y
    ) {
        return nodeIndex[x][y];
    }

    public ClusterNode getNode(
            final long x,
            final long y
    ) {
        return nodeIndex[(int) x][(int) y];
    }

    @Override
    public int getMapWidthInMeter() {
        return spacialIndex.getMapWidthInMeter();
    }

    @Override
    public int getMapHeightInMeter() {
        return spacialIndex.getMapHeightInMeter();
    }

    @Override
    public int getNrCellsWidth() {
        return spacialIndex.getNrCellsWidth();
    }

    @Override
    public int getNrCellsHeight() {
        return spacialIndex.getNrCellsHeight();
    }

    @Override
    public double getCellSizeInMeter() {
        return spacialIndex.getCellSizeInMeter();
    }

    @Override
    public void put(
            final double spaceX,
            final double spaceY,
            @NonNull final T value
    ) {
        spacialIndex.put(spaceX, spaceY, value);
    }

    @NonNull
    @Override
    public List<T> getItemsInCell(
            final int x,
            final int y
    ) {
        return spacialIndex.getItemsInCell(x, y);
    }

    @NonNull
    @Override
    public List<T> getItemsInSpace(
            final int x,
            final int y
    ) {
        return spacialIndex.getItemsInSpace(x, y);
    }

    @NonNull
    @Override
    public List<T> getItemsInSpace(
            final double spaceX,
            final double spaceY
    ) {
        return spacialIndex.getItemsInSpace(spaceX, spaceY);
    }

    @Override
    public int countItemsInIndex() {
        return spacialIndex.countItemsInIndex();
    }

    @Override
    public boolean isItemThresholdExceeded(
            final int cellX,
            final int cellY
    ) {
        return spacialIndex.isItemThresholdExceeded(cellX, cellY);
    }

    @Override
    public boolean isInCellRange(
            final int cellX,
            final int cellY
    ) {
        return spacialIndex.isInCellRange(cellX, cellY);
    }

    @Override
    public boolean isOutCellRange(
            final int cellX,
            final int cellY
    ) {
        return spacialIndex.isOutCellRange(cellX, cellY);
    }

    @Override
    public boolean isInRange(
            final double spaceX,
            final double spaceY
    ) {
        return spacialIndex.isInRange(spaceX, spaceY);
    }

    @Override
    public boolean isOutRange(
            final double spaceX,
            final double spaceY
    ) {
        return spacialIndex.isOutRange(spaceX, spaceY);
    }

}

