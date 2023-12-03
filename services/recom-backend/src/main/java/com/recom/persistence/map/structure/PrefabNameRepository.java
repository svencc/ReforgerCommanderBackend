package com.recom.persistence.map.structure;

import com.recom.entity.map.structure.PrefabNameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface PrefabNameRepository extends JpaRepository<PrefabNameEntity, Long> {

}