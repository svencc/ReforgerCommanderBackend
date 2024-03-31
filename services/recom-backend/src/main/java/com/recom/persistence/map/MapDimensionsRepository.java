package com.recom.persistence.map;

import com.recom.entity.map.MapDimensions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface MapDimensionsRepository extends JpaRepository<MapDimensions, Long> {

}