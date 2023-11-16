package com.recom.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
    private MapStructureEntity mapStructure;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
    private MapTopography mapTopography;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
    private Configuration configuration;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();


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
