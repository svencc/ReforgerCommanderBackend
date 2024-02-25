package com.recom.commons.model.maprendererpipeline.report;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class RendererReport {

    @NonNull
    private boolean success = true;

    @NonNull
    private final List<String> messages = new ArrayList<>();

    public void addMessage(@NonNull final String message) {
        messages.add(String.format("%1s: %2s", getClass().getSimpleName(), message));
    }

    public void logException(@NonNull final Throwable throwable) {
        success = false;
        addMessage(throwable.getMessage());
    }

}
