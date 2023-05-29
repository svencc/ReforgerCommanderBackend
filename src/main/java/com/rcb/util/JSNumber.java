package com.rcb.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class JSNumber {

    @NonNull
    public BigDecimal of(long number) {
        final BigDecimal decimal = BigDecimal.valueOf(number);

        return scale(decimal);
    }

    @NonNull
    private BigDecimal scale(@NonNull final BigDecimal decimal) {
        return decimal.setScale(Math.max(2, decimal.scale()));
    }

    @NonNull
    public BigDecimal of(float number) {
        final BigDecimal decimal = BigDecimal.valueOf(number);

        return scale(decimal);
    }

    @NonNull
    public BigDecimal of(double number) {
        final BigDecimal decimal = BigDecimal.valueOf(number);

        return scale(decimal);
    }

    @NonNull
    public BigDecimal of(@NonNull final String number) {
        final BigDecimal decimal = new BigDecimal(number);

        return scale(decimal);
    }

}
