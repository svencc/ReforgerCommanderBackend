package com.recom.commons.model.maprendererpipeline.report;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class PipelineLogMessage {

    private final String creator;
    private final String message;

    public String toString() {
        return String.format("%1s: %2s", creator, message);
    }

}
