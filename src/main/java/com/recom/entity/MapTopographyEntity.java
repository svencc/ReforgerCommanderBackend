package com.recom.entity;

import com.recom.event.listener.generic.MapLocatedEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "IDX_mapName", columnList = "mapName", unique = false),
        @Index(name = "IDX_mapName_coordinates", columnList = "mapName, coordinates", unique = true)
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MapTopographyEntity implements Persistable<Long>, Serializable, MapLocatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = true, updatable = false, nullable = false)
    private Long id;

    @Nationalized
    @Column(insertable = true, updatable = false, nullable = false, length = 255)
    private String mapName;

    @Column(insertable = true, updatable = false, nullable = false)
    private Float surfaceHeight;

    @Column(insertable = true, updatable = false, nullable = false)
    private Float oceanHeight;

    @Column(insertable = true, updatable = false, nullable = false)
    private Float oceanBaseHeight;

    @Lob
    @Column(insertable = true, updatable = false, nullable = true)
    private String coordinates;

    @Override
    public int hashCode() {
        return MapTopographyEntity.class.hashCode();
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
            final MapTopographyEntity other = (MapTopographyEntity) obj;
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
