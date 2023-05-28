package com.rcb.enity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    private Long entityId;

    @Override
    public Long getId() {
        return entityId;
    }

    @Nationalized
    @Column(insertable = true, updatable = false, nullable = true)
    private String className;

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
    public boolean isNew() {
        return getId() != null;
    }

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

}
