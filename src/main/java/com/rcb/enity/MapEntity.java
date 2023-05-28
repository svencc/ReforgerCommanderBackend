package com.rcb.enity;

import jakarta.persistence.*;
import lombok.*;
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

    @Override
    public Long getId() {
        return id;
    }

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
