package com.rcb.repository.resourceType;

import com.rcb.entity.MapEntity;
import com.rcb.entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.lang.model.type.ReferenceType;

@Repository
interface ResourceTypeRepository extends JpaRepository<ResourceType, String> {

//    @NonNull
//    List<MapEntity> findAllBy(@NonNull String recipient);

}