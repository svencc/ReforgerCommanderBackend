package com.recom.entity;

import com.recom.model.configuration.ConfigurationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "IDX_namespace_name", columnList = "namespace, name", unique = false),
//        @Index(name = "IDX_gameMap_namespace_name", columnList = "gameMap, namespace, name", unique = false),
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Configuration implements Persistable<Long>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = true, updatable = false, nullable = false)
    private Long id;

    @Nullable
    @ManyToOne(optional = true)
    private GameMap gameMap;

    @Column(insertable = true, updatable = true, nullable = false, length = 255)
    private String namespace;

    @Column(insertable = true, updatable = true, nullable = true, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(insertable = true, updatable = true, nullable = false, length = 255)
    private ConfigurationType type;

    @Lob
    @Column(insertable = true, updatable = true, nullable = false, columnDefinition = "LONGTEXT")
    private String value;

    @Nullable
    public String getMapName() {
        return gameMap.getName();
    }

    @Override
    public int hashCode() {
        return Configuration.class.hashCode();
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
            final Configuration other = (Configuration) obj;
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
