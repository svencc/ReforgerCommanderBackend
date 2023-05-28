package com.rcb.repository;

import com.rcb.entity.MapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapEntityRepository extends JpaRepository<MapEntity, Long> {

//    @NonNull
//    List<MapEntity> findAllBy(@NonNull String recipient);

}