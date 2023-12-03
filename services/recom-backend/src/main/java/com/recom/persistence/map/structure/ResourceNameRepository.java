package com.recom.persistence.map.structure;

import com.recom.entity.map.structure.ResourceNameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ResourceNameRepository extends JpaRepository<ResourceNameEntity, Long> {

}