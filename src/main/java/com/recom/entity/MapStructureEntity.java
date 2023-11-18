package com.recom.entity;

import com.recom.event.listener.generic.maplocated.MapLocatedEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
//        @Index(name = "IDX_map", columnList = "mapMeta", unique = false),
//        @Index(name = "IDX_map_className", columnList = "mapMeta, className", unique = false),
//        @Index(name = "IDX_map_prefabName", columnList = "mapMeta, prefabName", unique = false),
//        @Index(name = "IDX_map_resourceName", columnList = "mapMeta, resourceName", unique = false),
//        @Index(name = "IDX_map_mapDescriptorType", columnList = "mapMeta, mapDescriptorType", unique = false),
//        @Index(name = "IDX_map_name", columnList = "mapMeta, name", unique = false),
//        @Index(name = "IDX_map_coordinates", columnList = "mapMeta, coordinateX, coordinateY, coordinateZ", unique = false),
//        @Index(name = "IDX_map_coordinateX", columnList = "mapMeta, coordinateX", unique = false),
//        @Index(name = "IDX_map_coordinateY", columnList = "mapMeta, coordinateY", unique = false),
//        @Index(name = "IDX_map_coordinateZ", columnList = "mapMeta, coordinateZ", unique = false),
//        @Index(name = "IDX_map_coordinate_map", columnList = "mapMeta, coordinateX, coordinateZ,", unique = false)
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MapStructureEntity implements Persistable<Long>, Serializable, MapLocatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = true, updatable = false, nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    private GameMap gameMap;

    @Nationalized
    @Column(insertable = true, updatable = false, nullable = true, length = 255)
    private String entityId;

    @Nationalized
    @Column(insertable = true, updatable = false, nullable = true, length = 255)
    private String name;

    @Nationalized
    @Column(insertable = true, updatable = false, nullable = true, length = 255)
    private String className;

    @Nationalized
    @Column(insertable = true, updatable = false, nullable = true, length = 255)
    private String prefabName;

    @Lob
    @Nationalized
    @Column(insertable = true, updatable = false, nullable = true)
    private String resourceName;

    @Column(insertable = true, updatable = false, nullable = true)
    private String mapDescriptorType;

    @Lob
    @Column(insertable = true, updatable = false, nullable = true)
    private String rotationX;

    @Lob
    @Column(insertable = true, updatable = false, nullable = true)
    private String rotationY;

    @Lob
    @Column(insertable = true, updatable = false, nullable = true)
    private String rotationZ;

    @Column(columnDefinition = "Decimal(15,10)", insertable = true, updatable = false, nullable = true)
    private BigDecimal coordinateX;

    @Column(columnDefinition = "Decimal(15,10)", insertable = true, updatable = false, nullable = true)
    private BigDecimal coordinateY;

    @Column(columnDefinition = "Decimal(15,10)", insertable = true, updatable = false, nullable = true)
    private BigDecimal coordinateZ;

    @Override
    public int hashCode() {
        return MapStructureEntity.class.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        } else {
            final MapStructureEntity other = (MapStructureEntity) obj;
            if (getId() == null) {
                return false;
            } else return getId().equals(other.getId());
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return getId() == null;
    }

}
