package com.recom.commons.model.maprendererpipeline.dataprovider.forest;

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

        this.index = (List<T>[][]) new List[nrCellsWidth][nrCellsHeight];
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
            final int x,
            final int y,
            @NonNull final T value
    ) {
        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight) {
            log.warn("Trying to put value outside of map: x={}, y={}\nDiscard value!", x, y);
            return;
        }

        final int cellX = (int) (x / cellSize);
        final int cellY = (int) (y / cellSize);

        index[cellX][cellY].add(value);
    }

    @NonNull
    public List<T> get(
            final int x,
            final int y
    ) {
        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight) {
            log.warn("Trying to get value outside of map: x={}, y={}\nReturn empty list!", x, y);
            return new ArrayList<>();
        } else {
            final int cellX = (int) (x / cellSize);
            final int cellY = (int) (y / cellSize);

            return index[cellX][cellY];
        }
    }

}

