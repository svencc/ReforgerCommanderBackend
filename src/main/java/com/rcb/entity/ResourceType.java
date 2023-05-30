package com.rcb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.domain.Persistable;

import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
//        @Index(name = "IDX_mapName", columnList = "mapName", unique = false),
//        @Index(name = "IDX_className", columnList = "className", unique = false),
//        @Index(name = "IDX_resourceName", columnList = "resourceName", unique = false),
//        @Index(name = "IDX_mapName_className", columnList = "mapName, className", unique = false),
//        @Index(name = "IDX_mapName_resourceName", columnList = "mapName, resourceName", unique = false)
})
public class ResourceType implements Persistable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(insertable = true, updatable = false, nullable = false)
    private Long id;
//
//    @Id
    @Lob
    @Nationalized
    @Column(insertable = true, updatable = false, nullable = true)
    private String resourceName; // -> @ TODO add reference to classification table?

//    @OneToMany(mappedBy="resourceName")
//    private Set<MapEntity> mapEntities;

    @Override
    public int hashCode() {
        return ResourceType.class.hashCode();
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
            final ResourceType other = (ResourceType) obj;
            if (getId() == null) {
                return false;
            } else return getId().equals(other.getId());
        }
    }

    @Override
    public String getId() {
        return resourceName;
    }
//    @Override
//    public Long getId() {
//        return id;
//    }

    @Override
    public boolean isNew() {
        return getId() != null;
    }

}
