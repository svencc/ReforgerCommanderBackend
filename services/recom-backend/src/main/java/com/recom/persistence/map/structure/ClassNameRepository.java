package com.recom.persistence.map.structure;

import com.recom.entity.map.structure.ClassNameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ClassNameRepository extends JpaRepository<ClassNameEntity, Long> {

}