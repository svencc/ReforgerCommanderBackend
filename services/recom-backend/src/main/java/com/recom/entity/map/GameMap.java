package com.recom.entity.map;

import com.recom.entity.Configuration;
import com.recom.entity.Message;
import com.recom.entity.map.structure.MapStructureEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "IDX_name", columnList = "name", unique = false)
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class GameMap implements Persistable<Long>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = true, updatable = false, nullable = false)
    private Long id;

    @Nationalized
    @Column(insertable = true, updatable = false, nullable = false, length = 255)
    private String name;

    @OneToOne(mappedBy = "gameMap", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private MapMeta mapMeta;

    @Builder.Default
    @OneToMany(mappedBy = "gameMap", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MapStructureEntity> mapStructures = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "gameMap", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SquareKilometerTopographyChunk> topographyChunks = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "gameMap", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Configuration> configurations = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "gameMap", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messages = new HashSet<>();


    @Override
    public int hashCode() {
        return GameMap.class.hashCode();
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
            final GameMap other = (GameMap) obj;
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
