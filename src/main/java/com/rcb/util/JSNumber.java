package com.rcb.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class JSNumber {

    @NonNull
    public BigDecimal of(@NonNull long number) {
        final BigDecimal decimal = BigDecimal.valueOf(number);

        return scale(decimal);
    }

    @NonNull
    public BigDecimal of(@NonNull float number) {
        final BigDecimal decimal = BigDecimal.valueOf(number);

        return scale(decimal);
    }

    @NonNull
    public BigDecimal of(@NonNull double number) {
        final BigDecimal decimal = BigDecimal.valueOf(number);

        return scale(decimal);
    }

    @NonNull
    public BigDecimal of(@NonNull String number) {
        final BigDecimal decimal = new BigDecimal(number);

        return scale(decimal);
    }

    @NonNull
    private BigDecimal scale(@NonNull BigDecimal decimal) {
        return decimal.setScale(Math.max(2, decimal.scale()));
    }

}
