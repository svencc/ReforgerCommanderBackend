package com.recom.persistence.map;

import com.recom.entity.map.MapMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface MapMetaRepository extends JpaRepository<MapMeta, Long> {

}