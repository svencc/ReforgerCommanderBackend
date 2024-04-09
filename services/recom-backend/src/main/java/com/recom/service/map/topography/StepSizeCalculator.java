package com.recom.service.map.topography;

import com.recom.commons.model.DEMDescriptor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class StepSizeCalculator {

    @NonNull
    public BigDecimal calculateStepSize(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull BigDecimal scaleFactor
    ) {
        if (scaleFactor.compareTo(BigDecimal.ONE) > 0) {
            return demDescriptor.getStepSize().divide(scaleFactor, 10, RoundingMode.HALF_UP).setScale(10, RoundingMode.HALF_UP);
        } else if (scaleFactor.compareTo(BigDecimal.ONE) < 0) {
            return demDescriptor.getStepSize().multiply(scaleFactor.multiply(BigDecimal.valueOf(100))).setScale(10, RoundingMode.HALF_UP);
        } else {
            return demDescriptor.getStepSize().setScale(10, RoundingMode.HALF_UP);
        }
    }

}
