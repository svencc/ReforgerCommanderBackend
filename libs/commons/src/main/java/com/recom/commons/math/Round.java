package com.recom.commons.math;

public class Round {

    public static int halfUp(final double number) {
        return (int) Math.ceil(number);
        /*
        final int integerComponent = (int) Math.floor(number);
        final double decimalComponent = number - integerComponent;

        if (decimalComponent >= 0.5) {
            return integerComponent + 1;
        } else {
            return integerComponent;
        }
         */
    }

}
