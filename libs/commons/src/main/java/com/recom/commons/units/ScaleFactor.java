package com.recom.commons.units;

import lombok.Getter;

/**
 * The scale factor is NEVER 0
 * ... -3 -2  1  2 3 ...
 */
@Getter
public class ScaleFactor implements Cloneable {

    private int scaleFactor = 1;


    public void zoomIn() {
        switch (scaleFactor) {
            case -2:
                scaleFactor = 1;
                break;
            case -1, 1:
                scaleFactor = 2;
                break;
            default:
                scaleFactor++;
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
                scaleFactor--;
                break;
        }
    }

    public int getSign() {
        if (scaleFactor > 0) {
            return 1;
        } else if (scaleFactor < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public ScaleFactor clone() {
        final ScaleFactor scaleFactorClone = new ScaleFactor();
        scaleFactorClone.scaleFactor = this.scaleFactor;

        return scaleFactorClone;
    }

}
