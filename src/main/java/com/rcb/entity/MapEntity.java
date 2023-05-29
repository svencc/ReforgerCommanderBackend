package com.rcb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.domain.Persistable;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@Table(
//        indexes = {
//                @Index(name = "recipient", columnList = "recipient", unique = false),
//                @Index(name = "created", columnList = "created", unique = false),
//        })
public class MapEntity implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(insertable = true, updatable = false, nullable = false)
    private Long id;

    @Nationalized
    @Column(insertable = true, updatable = false, nullable = true)
    private String className;

    @Nationalized
    @Column(insertable = true, length = 255, updatable = false, nullable = true)
    private String entityId;

    @Lob
    @Nationalized
    @Column(insertable = true, updatable = false, nullable = true)
    private String resourceName;

    @Lob
    @Column(insertable = true, updatable = false, nullable = true)
    private String rotationX;

    @Lob
    @Column(insertable = true, updatable = false, nullable = true)
    private String rotationY;

    @Lob
    @Column(insertable = true, updatable = false, nullable = true)
    private String rotationZ;

    @Lob
    @Column(insertable = true, updatable = false, nullable = true)
    private String coords;

    @Override
    public int hashCode() {
        return MapEntity.class.hashCode();
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
            MapEntity other = (MapEntity) obj;
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
        return getId() != null;
    }

}