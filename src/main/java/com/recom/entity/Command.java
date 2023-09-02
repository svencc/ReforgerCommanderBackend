package com.recom.entity;

import com.recom.model.command.CommandType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "IDX_mapName", columnList = "mapName", unique = false),
        @Index(name = "IDX_mapName_commandType", columnList = "mapName, commandType", unique = false),
        @Index(name = "IDX_mapName_commandType_timestamp", columnList = "mapName, commandType, timestamp", unique = true),
        @Index(name = "IDX_mapName_timestamp", columnList = "mapName, timestamp", unique = false),
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Command implements Persistable<Long>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = true, updatable = false, nullable = false)
    private Long id;

    @Nationalized
    @Column(insertable = true, updatable = false, nullable = false, length = 255)
    private String mapName;

    @Enumerated(EnumType.STRING)
    @Column(insertable = true, updatable = false, nullable = false, length = 255)
    private CommandType commandType;

    @Column(insertable = true, updatable = false, nullable = false, columnDefinition="DATETIME(6) DEFAULT NOW(6)")
    private LocalDateTime timestamp;

    @Lob
    @Column(insertable = true, updatable = true, nullable = true, columnDefinition = "LONGTEXT")
    private String payload;

    @Override
    public int hashCode() {
        return Command.class.hashCode();
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
            final Command other = (Command) obj;
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
