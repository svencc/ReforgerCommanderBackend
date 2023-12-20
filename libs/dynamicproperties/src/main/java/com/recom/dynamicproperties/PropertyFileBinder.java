package com.recom.dynamicproperties;//package com.recom.dynamicproperties;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertyFileBinder {

    @NonNull
    private final ConversionService conversionService;

    @NonNull
    public <T extends DynamicProperties> void bindToFilesystem(@NonNull final T dynamicProperties) {
        final DynamicPropertySystem<T> dynamicPropertySystem = new DynamicPropertySystem(
                dynamicProperties,
                conversionService
        );
        dynamicProperties.dynamicPropertySystem = dynamicPropertySystem;
    }

}
