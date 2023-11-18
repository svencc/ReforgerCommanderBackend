package com.recom.entity;

import com.recom.event.listener.generic.maprelated.MapRelatedEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MapTopography implements Persistable<Long>, Serializable, MapRelatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = true, updatable = false, nullable = false)
    private Long id;

    @NonNull
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    private GameMap gameMap;

    @Lob
    @Column(insertable = true, updatable = true, nullable = false, columnDefinition = "LONGBLOB")
    private byte[] data;


    @Override
    public int hashCode() {
        return MapTopography.class.hashCode();
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
            final MapTopography other = (MapTopography) obj;
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
