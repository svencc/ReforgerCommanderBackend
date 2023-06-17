package com.rcb.entity;

import com.rcb.model.configuration.ConfigurationType;
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
        @Index(name = "IDX_namespace_name", columnList = "namespace, name", unique = false),
        @Index(name = "IDX_mapName_namespace_name", columnList = "mapName, namespace, name", unique = false),
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Configuration implements Persistable<Long>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(insertable = true, updatable = false, nullable = false)
    private Long id;

    @Nationalized
    @Column(insertable = true, updatable = true, nullable = true, length = 255)
    private String mapName;

    @Column(insertable = true, updatable = true, nullable = false, length = 255)
    private String namespace;

    @Column(insertable = true, updatable = true, nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(insertable = true, updatable = true, nullable = false, length = 255)
    private ConfigurationType type;

    @Lob
    @Column(insertable = true, updatable = true, nullable = false)
    private String value;

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
