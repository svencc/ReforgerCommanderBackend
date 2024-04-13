package com.recom.entity.map;

import com.recom.event.listener.generic.maprelated.MapRelatedEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MapDimensions implements Persistable<Long>, Serializable, MapRelatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = true, updatable = false, nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    private GameMap gameMap;

    @Column(insertable = true, updatable = false, nullable = false)
    private Long dimensionX;

    @Column(insertable = true, updatable = false, nullable = false)
    private Long dimensionY;

    @Column(insertable = true, updatable = false, nullable = false)
    private Long dimensionZ;

    @Column(columnDefinition = "Decimal(15,10)", insertable = true, updatable = false, nullable = true)
    private BigDecimal oceanBaseHeight;

    @Override
    public int hashCode() {
        return MapDimensions.class.hashCode();
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
            final MapDimensions other = (MapDimensions) obj;
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
