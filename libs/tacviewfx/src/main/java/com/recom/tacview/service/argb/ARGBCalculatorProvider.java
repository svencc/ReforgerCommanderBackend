package com.recom.tacview.service.argb;

import com.recom.rendertools.ARGBCalculator;
import jakarta.annotation.PostConstruct;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public final class ARGBCalculatorProvider {

    @Nullable
    private ARGBCalculator instance;

//    @PostConstruct
//    public void postConstruct() {
//        provide();
//    }

    public ARGBCalculator provide() {
        if (instance == null) {
            instance = new ARGBCalculator();
        }

        return instance;
    }

}
