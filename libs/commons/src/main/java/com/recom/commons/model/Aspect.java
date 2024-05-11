package com.recom.commons.model;

import com.recom.commons.calculator.d8algorithm.D8AspectMatrix;
import lombok.Getter;
import lombok.NonNull;

import java.util.stream.Stream;

@Getter
public enum Aspect {


    NORTH(0, 0),
    NORTH_EAST(1, 45),
    EAST(2, 270),
    SOUTH_EAST(3, 135),
    SOUTH(4, 180),
    SOUTH_WEST(5, 225),
    WEST(6, 90),
    NORTH_WEST(7, 315),
    NULL_ASPECT(8, -1);


    private static Aspect[] iteratorValues;
    final int aspectValue;
    final int angle;
    final boolean isCardinal;

    Aspect(
            final int enumValue,
            final int angle
    ) {
        this.aspectValue = enumValue;
        this.angle = angle;
        this.isCardinal = angle % 90 == 0;
    }

    @NonNull
    public Aspect getOpposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case NORTH_EAST -> SOUTH_WEST;
            case EAST -> WEST;
            case SOUTH_EAST -> NORTH_WEST;
            case SOUTH -> NORTH;
            case SOUTH_WEST -> NORTH_EAST;
            case WEST -> EAST;
            case NORTH_WEST -> SOUTH_EAST;
            default -> NULL_ASPECT;
        };
    }

    @NonNull
    public D8Neighbour getNeighbour(
            final int x,
            final int y
    ) {
        return new D8Neighbour(x + D8AspectMatrix.directionXComponentMatrix[aspectValue], y + D8AspectMatrix.directionYComponentMatrix[aspectValue]);
    }

    @NonNull
    public static Aspect[] iterateValues() {
        if (Aspect.iteratorValues == null) {
            Aspect.iteratorValues = Stream.of(Aspect.values())
                    .filter(aspect -> aspect != NULL_ASPECT)
                    .toArray(Aspect[]::new);
        }

        return Aspect.iteratorValues;
    }

}