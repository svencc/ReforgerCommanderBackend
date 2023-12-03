package com.recom.persistence.map.structure;

import com.recom.entity.map.structure.MapDescriptorTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface MapDescriptorTypeRepository extends JpaRepository<MapDescriptorTypeEntity, Long> {

}