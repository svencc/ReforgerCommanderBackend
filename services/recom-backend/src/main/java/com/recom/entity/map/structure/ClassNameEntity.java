package com.recom.entity.map.structure;

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
        @Index(name = "IDX_name", columnList = "name", unique = true)
})
//@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "second-level-entity-region")
public class ClassNameEntity implements Persistable<Long>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = true, updatable = false, nullable = false)
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "className", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MapStructureEntity> mapStructureEntities = new HashSet<>();

    @Nationalized
    @Column(insertable = true, updatable = false, nullable = true, length = 255)
    private String name;


    @Override
    public int hashCode() {
        return ClassNameEntity.class.hashCode();
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
            final ClassNameEntity other = (ClassNameEntity) obj;
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
