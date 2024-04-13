package com.recom.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Account implements Persistable<UUID>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = true, updatable = false, nullable = false)
    private UUID accountUuid;

    @Nationalized
    @Column(insertable = true, updatable = false, nullable = true, length = 255)
    private String accessKey;


    @Override
    public int hashCode() {
        return Account.class.hashCode();
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
            final Account other = (Account) obj;
            if (getId() == null) {
                return false;
            } else return getId().equals(other.getId());
        }
    }

    @Override
    public UUID getId() {
        return accountUuid;
    }

    @Override
    public boolean isNew() {
        return getId() == null;
    }

}
