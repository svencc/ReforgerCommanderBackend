package com.recom.tacview.service.argb;

import com.recom.rendertools.ARGBCalculator;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public final class ARGBCalculatorProvider {

    @Nullable
    private ARGBCalculator instance;

//    @PostConstruct
//    public void postConstruct() {
//      provide(); <<<<
//    }
// @TODO Load in advance

    public ARGBCalculator provide() {
        if (instance == null) {
            instance = new ARGBCalculator();
        }

        return instance;
    }

}
