package com.recom.commons.model.maprendererpipeline;

import com.recom.commons.model.SlopeAndAspectMap;
import lombok.*;
import org.springframework.lang.Nullable;

import java.io.ByteArrayOutputStream;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class MapRendererPipelineArtifacts {

    // Results
    // @TODO: need to be much more generic!
    /*
     * Result must contain:
     * render class name
     * order
     * visibility
     * enabled
     * result (byte array)
     */

}
