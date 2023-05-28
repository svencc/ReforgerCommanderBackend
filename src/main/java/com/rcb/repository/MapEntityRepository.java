package com.rcb.repository;

import com.rcb.enity.MapEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface MapEntityRepository extends JpaRepository<MapEntity, Long> {

    @NonNull
    List<MapEntity> findAllBy(@NonNull String recipient);

}