package com.recom.entity;

import com.recom.event.listener.generic.MapLocatedEntity;
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
        @Index(name = "IDX_mapName", columnList = "mapName", unique = false),
        @Index(name = "IDX_mapName_className", columnList = "mapName, className", unique = false),
        @Index(name = "IDX_mapName_prefabName", columnList = "mapName, prefabName", unique = false),
        @Index(name = "IDX_mapName_resourceName", columnList = "mapName, resourceName", unique = false),
        @Index(name = "IDX_mapName_mapDescriptorType", columnList = "mapName, mapDescriptorType", unique = false),
        @Index(name = "IDX_mapName_name", columnList = "mapName, name", unique = false),
        @Index(name = "IDX_mapName_coordinates", columnList = "mapName, coordinateX, coordinateY, coordinateZ", unique = false),
        @Index(name = "IDX_mapName_coordinateX", columnList = "mapName, coordinateX", unique = false),
        @Index(name = "IDX_mapName_coordinateY", columnList = "mapName, coordinateY", unique = false),
        @Index(name = "IDX_mapName_coordinateZ", columnList = "mapName, coordinateZ", unique = false),
        @Index(name = "IDX_mapName_coordinate_map", columnList = "mapName, coordinateX, coordinateZ,", unique = false)
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MapEntity implements Persistable<Long>, Serializable, MapLocatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = true, updatable = false, nullable = false)
    private Long id;

    @Nationalized
    @Column(insertable = true, updatable = false, nullable = false, length = 255)
    private String mapName;

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

    @Lob
    @Column(insertable = true, updatable = false, nullable = true)
    private String coordinates;

    @Column(insertable = true, updatable = false, nullable = true)
    private BigDecimal coordinateX;

    @Column(insertable = true, updatable = false, nullable = true)
    private BigDecimal coordinateY;

    @Column(insertable = true, updatable = false, nullable = true)
    private BigDecimal coordinateZ;

    @Override
    public int hashCode() {
        return MapEntity.class.hashCode();
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
            final MapEntity other = (MapEntity) obj;
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
