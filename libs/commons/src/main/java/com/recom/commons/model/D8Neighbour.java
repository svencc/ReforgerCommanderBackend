package com.recom.commons.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class D8Neighbour {

    private final int x;
    private final int y;

    public boolean isInCellRange(
            final int nrCellsWidthX,
            final int nrCellsHeightY
    ) {
        return x >= 0 && x < nrCellsWidthX && y >= 0 && y < nrCellsHeightY;
    }

    public boolean isOutCellRange(
            final int nrCellsWidthX,
            final int nrCellsHeightY
    ) {
        return !isInCellRange(nrCellsWidthX, nrCellsHeightY);
    }

}