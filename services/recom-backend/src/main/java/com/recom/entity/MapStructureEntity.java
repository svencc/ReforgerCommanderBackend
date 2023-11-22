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
        @Index(name = "IDX_gameMap_className", columnList = "game_map_id, className", unique = false),
        @Index(name = "IDX_gameMap_prefabName", columnList = "game_map_id, prefabName", unique = false),
        @Index(name = "IDX_gameMap_resourceName", columnList = "game_map_id, resourceName", unique = false),
        @Index(name = "IDX_gameMap_mapDescriptorType", columnList = "game_map_id, mapDescriptorType", unique = false),
        @Index(name = "IDX_gameMap_name", columnList = "game_map_id, name", unique = false),
//        @Index(name = "IDX_gameMap_coordinates", columnList = "game_map_id, coordinateX, coordinateY, coordinateZ", unique = false),
//        @Index(name = "IDX_gameMap_coordinateX", columnList = "game_map_id, coordinateX", unique = false),
//        @Index(name = "IDX_gameMap_coordinateY", columnList = "game_map_id, coordinateY", unique = false),
//        @Index(name = "IDX_gameMap_coordinateZ", columnList = "game_map_id, coordinateZ", unique = false),
//        @Index(name = "IDX_gameMap_coordinate_xz", columnList = "game_map_id, coordinateX, coordinateZ,", unique = false)
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
