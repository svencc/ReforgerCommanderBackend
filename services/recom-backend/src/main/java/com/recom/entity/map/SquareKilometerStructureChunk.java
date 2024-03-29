package com.recom.entity.map;

import com.recom.entity.map.structure.MapStructureEntity;
import com.recom.event.listener.generic.maprelated.MapRelatedEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SquareKilometerStructureChunk implements Persistable<Long>, Serializable, MapRelatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = true, updatable = false, nullable = false)
    private Long id;

    @NonNull
    @ManyToOne(optional = false)
    private GameMap gameMap;

    @NonNull
    @Column(insertable = true, updatable = false, nullable = false)
    private Long squareCoordinateX; // Left to Right | 0 ---> ...

    @NonNull
    @Column(insertable = true, updatable = false, nullable = false)
    private Long squareCoordinateY; // Down to Up | 0 ---> ...

    @NonNull
    @Builder.Default
    @OneToMany(mappedBy = "squareKilometerStructureChunk", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MapStructureEntity> structureChunk = new HashSet<>();

    @NonNull
    @Enumerated(EnumType.STRING)
    private ChunkStatus status;

    @Nullable
    @Column(insertable = true, updatable = true, nullable = true, columnDefinition = "TIMESTAMP")
    private LocalDateTime lastUpdate;


    @Override
    public int hashCode() {
        return SquareKilometerStructureChunk.class.hashCode();
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
            final SquareKilometerStructureChunk other = (SquareKilometerStructureChunk) obj;
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
