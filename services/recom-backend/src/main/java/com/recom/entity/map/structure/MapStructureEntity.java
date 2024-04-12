package com.recom.entity.map.structure;

import com.recom.entity.map.GameMap;
import com.recom.entity.map.SquareKilometerStructureChunk;
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
        @Index(name = "IDX_gameMap_name", columnList = "game_map_id, name", unique = false),
        @Index(name = "IDX_gameMap_className", columnList = "game_map_id, class_name_id", unique = false),
        @Index(name = "IDX_gameMap_prefabName", columnList = "game_map_id, prefab_name_id", unique = false),
        @Index(name = "IDX_gameMap_resourceName", columnList = "game_map_id, resource_name_id", unique = false),
        @Index(name = "IDX_gameMap_mapDescriptorType", columnList = "game_map_id, map_descriptor_type_id", unique = false),
        @Index(name = "IDX_squareKilometerStructureChunk", columnList = "square_kilometer_structure_chunk_id", unique = false)
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "entityCacheRegion")
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

    @ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private ClassNameEntity className;

    @ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private PrefabNameEntity prefabName;

    @ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private ResourceNameEntity resourceName;

    @ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private MapDescriptorTypeEntity mapDescriptorType;

    @ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private SquareKilometerStructureChunk squareKilometerStructureChunk;

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
