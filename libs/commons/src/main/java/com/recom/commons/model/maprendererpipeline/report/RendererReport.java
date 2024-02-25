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
    private final List<PipelineLogMessage> messages = new ArrayList<>();

    public void addMessage(@NonNull final String message) {
        PipelineLogMessage entry = PipelineLogMessage.builder()
                .creator(getClass().getSimpleName())
                .message(message)
                .build();
        messages.add(entry);
    }

    public void logException(@NonNull final Throwable throwable) {
        success = false;
        addMessage(throwable.getMessage());
    }

}
