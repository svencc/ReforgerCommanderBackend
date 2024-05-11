package com.recom.commons.model.maprendererpipeline.dataprovider;

import com.recom.commons.math.Round;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class SpacialIndex<T> implements IndexedSpace<T> {

    @NonNull
    private final List<T>[][] index;
    @Getter
    private final int mapWidthInMeter;
    @Getter
    private final int mapHeightInMeter;
    @Getter
    final int nrCellsWidth;
    @Getter
    final int nrCellsHeight;
    @Getter
    private final double cellSizeInMeter;
    @Getter
    private final double itemsPerSquareMeterThreshold;


    @SuppressWarnings("unchecked")
    public SpacialIndex(
            final int mapWidthInMeter,
            final int mapHeightInMeter,
            final double cellSizeInMeter,
            final double itemsPerSquareMeterThreshold
    ) {
        this.mapWidthInMeter = mapWidthInMeter;
        this.mapHeightInMeter = mapHeightInMeter;
        this.cellSizeInMeter = cellSizeInMeter;
        this.itemsPerSquareMeterThreshold = itemsPerSquareMeterThreshold;

        this.nrCellsWidth = (int) Math.ceil(mapWidthInMeter / cellSizeInMeter);
        this.nrCellsHeight = (int) Math.ceil(mapHeightInMeter / cellSizeInMeter);

        this.index = (List<T>[][]) new List[nrCellsWidth + 1][nrCellsHeight + 1];
        preInitializeIndex();
    }

    @Deprecated
    public SpacialIndex(
            final int mapWidthInMeter,
            final int mapHeightInMeter,
            final double cellSizeInMeter
    ) {
        this(mapWidthInMeter, mapHeightInMeter, cellSizeInMeter, 0);
    }

    private void preInitializeIndex() {
        for (int x = 0; x <= nrCellsWidth; x++) {
            for (int y = 0; y <= nrCellsHeight; y++) {
                this.index[x][y] = new ArrayList<T>();
            }
        }
    }

    public void put(
            final double spaceX,
            final double spaceY,
            @NonNull final T value
    ) {
        if (isInRange(spaceX, spaceY)) {
            final int roundedCellX = Round.halfUp(spaceX / cellSizeInMeter);
            final int roundedCellY = Round.halfUp(spaceY / cellSizeInMeter);
            index[roundedCellX][roundedCellY].add(value);
        } else {
            log.warn("Trying to put value outside of map: x={}, y={} | discard value!", spaceX, spaceY);
        }
    }

    @NonNull
    public List<T> getItemsInCell(
            final int cellX,
            final int cellY
    ) {
        if (isInCellRange(cellX, cellY)) {
            return index[cellX][cellY];
        } else {
            log.warn("Trying to get value outside of map: x={}, y={}\nReturn empty list!", cellX, cellY);
            return new ArrayList<>();
        }
    }

    @NonNull
    public List<T> getItemsInSpace(
            final int spaceX,
            final int spaceY
    ) {
        if (isOutRange(spaceX, spaceY)) {
            log.warn("Trying to get value outside of map: x={}, y={}\nReturn empty list!", spaceX, spaceY);
            return new ArrayList<>();
        } else {
            final int cellX = (int) (spaceX / cellSizeInMeter);
            final int cellY = (int) (spaceY / cellSizeInMeter);

            return index[cellX][cellY];
        }
    }

    @NonNull
    public List<T> getItemsInSpace(
            final double spaceX,
            final double spaceY
    ) {
        final int cellX = Round.halfUp(spaceX);
        final int cellY = Round.halfUp(spaceY);

        return getItemsInSpace(cellX, cellY);
    }

    @Override
    public int countItemsInIndex() {
        int count = 0;
        for (int x = 0; x < nrCellsWidth; x++) {
            for (int y = 0; y < nrCellsHeight; y++) {
                count += index[x][y].size();
            }
        }

        return count;
    }

    @Override
    public boolean isItemThresholdExceeded(
            final int cellX,
            final int cellY
    ) {
        if (isInCellRange(cellX, cellY)) {
            final int amountItems = index[cellX][cellY].size();
            final double size = cellSizeInMeter * cellSizeInMeter;
            final double itemsPerSquareMeter = amountItems / size;

            return itemsPerSquareMeter > itemsPerSquareMeterThreshold;
        } else {
            log.warn("Trying to get value outside of map: x={}, y={}\nReturn false!", cellX, cellY);
            return false;
        }
    }

    @Override
    public boolean isInCellRange(
            final int cellX,
            final int cellY
    ) {
        return cellX >= 0 && cellX < nrCellsWidth && cellY >= 0 && cellY < nrCellsHeight;
    }

    @Override
    public boolean isOutCellRange(
            final int cellX,
            final int cellY
    ) {
        return cellX < 0 || cellX > nrCellsWidth || cellY < 0 || cellY > nrCellsHeight;
    }

    @Override
    public boolean isInRange(
            final double spaceX,
            final double spaceY
    ) {
        return spaceX >= 0 && spaceX < mapWidthInMeter && spaceY >= 0 && spaceY < mapHeightInMeter;
    }

    @Override
    public boolean isOutRange(
            final double spaceX,
            final double spaceY
    ) {
        return spaceX < 0 || spaceX > mapWidthInMeter || spaceY < 0 || spaceY > mapHeightInMeter;
    }

}

