package com.recom.commons.units;

import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

/**
 * The scale factor is NEVER 0
 * ... -3 -2  1  2 3 ...
 */
@Getter
public class ScaleFactor implements Cloneable {

    private int scaleFactor = 1;
    @NonNull
    private Optional<Integer> maybeMaxScaleFactor = Optional.empty();
    @NonNull
    private Optional<Integer> maybeMinScaleFactor = Optional.empty();


    public ScaleFactor() {

    }

    public ScaleFactor(
            final int minScaleFactor,
            final int maxScaleFactor
    ) {
        this.scaleFactor = scaleFactor;
        this.maybeMaxScaleFactor = Optional.of(maxScaleFactor);
        this.maybeMinScaleFactor = Optional.of(minScaleFactor);
    }

    public void zoomIn() {
        switch (scaleFactor) {
            case -2:
                scaleFactor = 1;
                break;
            case -1, 1:
                scaleFactor = 2;
                break;
            default:
                if (maybeMaxScaleFactor.isPresent() && scaleFactor + 1 > maybeMaxScaleFactor.get()) {
                    scaleFactor = maybeMaxScaleFactor.get();
                } else {
                    scaleFactor++;
                }
                break;
        }
    }

    public void zoomOut() {
        switch (scaleFactor) {
            case 2:
                scaleFactor = 1;
                break;
            case 1, -1:
                scaleFactor = -2;
                break;
            default:
                if (maybeMinScaleFactor.isPresent() && scaleFactor - 1 < maybeMinScaleFactor.get()) {
                    scaleFactor = maybeMinScaleFactor.get();
                } else {
                    scaleFactor--;
                }
                break;
        }
    }

    @Override
    public ScaleFactor clone() {
        final ScaleFactor scaleFactorClone = new ScaleFactor();
        scaleFactorClone.scaleFactor = this.scaleFactor;
        scaleFactorClone.maybeMaxScaleFactor = this.maybeMaxScaleFactor;
        scaleFactorClone.maybeMinScaleFactor = this.maybeMinScaleFactor;

        return scaleFactorClone;
    }

}
