package com.recom.commons.model.maprendererpipeline.dataprovider.forest;

import com.recom.commons.math.Round;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class SpacialIndex<T> {

    @NonNull
    private final List<T>[][] index;
    @Getter
    private final int mapWidth;
    @Getter
    private final int mapHeight;
    @Getter
    final int nrCellsWidth;
    @Getter
    final int nrCellsHeight;
    @Getter
    private final double cellSize;


    @SuppressWarnings("unchecked")
    public SpacialIndex(
            final int mapWidth,
            final int mapHeight,
            final double cellSize
    ) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.cellSize = cellSize;

        this.nrCellsWidth = (int) Math.ceil(mapWidth / cellSize);
        this.nrCellsHeight = (int) Math.ceil(mapHeight / cellSize);

        this.index = (List<T>[][]) new List[nrCellsWidth + 1][nrCellsHeight + 1];
        preInitializeIndex();
    }

    private void preInitializeIndex() {
        for (int x = 0; x < nrCellsWidth; x++) {
            for (int y = 0; y < nrCellsHeight; y++) {
                this.index[x][y] = new ArrayList<T>();
            }
        }
    }

    public void put(
            final double x,
            final double y,
            @NonNull final T value
    ) {
        if (x < 0 || x > mapWidth || y < 0 || y > mapHeight) {
            log.warn("Trying to put value outside of map: x={}, y={} | discard value!", x, y);
            return;
        }

        final int cellX = Round.halfUp(x / cellSize);
        final int cellY = Round.halfUp(y / cellSize);

        index[cellX][cellY].add(value);
    }

    @NonNull
    public List<T> getCell(
            final int x,
            final int y
    ) {
        if (x < 0 || x > nrCellsWidth || y < 0 || y > nrCellsHeight) {
            return new ArrayList<>();
        } else {
            return index[x][y];
        }
    }


    @NonNull
    public List<T> getInSpace(
            final int x,
            final int y
    ) {
        if (x < 0 || x > mapWidth || y < 0 || y > mapHeight) {
            log.warn("Trying to get value outside of map: x={}, y={}\nReturn empty list!", x, y);
            return new ArrayList<>();
        } else {
            final int cellX = (int) (x / cellSize);
            final int cellY = (int) (y / cellSize);

            return index[cellX][cellY];
        }
    }

    @NonNull
    public List<T> getInSpace(
            final double x,
            final double y
    ) {
        final int cellX = Round.halfUp(x / cellSize);
        final int cellY = Round.halfUp(y / cellSize);

        return getInSpace(cellX, cellY);
    }

    public int count() {
        int count = 0;
        for (int x = 0; x < nrCellsWidth; x++) {
            for (int y = 0; y < nrCellsHeight; y++) {
                count += index[x][y].size();
            }
        }

        return count;
    }

}

