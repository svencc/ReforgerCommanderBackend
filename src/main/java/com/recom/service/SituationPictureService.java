package com.recom.service;

import com.recom.dto.situationpicture.SituationPictureDto;
import com.recom.dto.situationpicture.SituationPictureRequestDto;
import com.recom.service.map.cluster.ClusteringService;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SituationPictureService {

    @NonNull
    private final ClusteringService clusteringService;

    @NonNull
    public SituationPictureDto generateSituationPicture(@NonNull final SituationPictureRequestDto situationPictureRequestDto) {
        return SituationPictureDto.builder()
                .build();
    }

}
