package com.recom.tacview.strategy;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@Builder
@RequiredArgsConstructor
public class SetJFrameTitleStrategy {

    @NonNull
    private final Frame frame;

    public void execute(@NonNull final String newTitle) {
        frame.setTitle(newTitle);
    }

}
