package com.recom.tacview.strategy;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class ProfileFPSStrategy {

    @NonNull
    private final FPSProfilable runnable;

    public void execute(@NonNull final String profiled) {
        runnable.setFPS(profiled);
    }

}
