package com.recom.entity;

import com.recom.model.message.MessageType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "IDX_mapName", columnList = "mapName", unique = false),
        @Index(name = "IDX_mapName_messageType", columnList = "mapName, messageType", unique = false),
        @Index(name = "IDX_mapName_messageType_timestamp", columnList = "mapName, messageType, timestamp", unique = true),
        @Index(name = "IDX_mapName_timestamp", columnList = "mapName, timestamp", unique = false),
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Message implements Persistable<UUID>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = true, updatable = false, nullable = false)
    private UUID uuid;

    @Nationalized
    @Column(insertable = true, updatable = false, nullable = false, length = 255)
    private String mapName;

    @Enumerated(EnumType.STRING)
    @Column(insertable = true, updatable = false, nullable = false, length = 255)
    private MessageType messageType;

    @Lob
    @Column(insertable = true, updatable = true, nullable = true, columnDefinition = "LONGTEXT")
    private String payload;

    @Column(insertable = true, updatable = false, nullable = true, columnDefinition = "DATETIME(6) DEFAULT NOW(6)")
    private LocalDateTime timestamp;


    @Override
    public int hashCode() {
        return Message.class.hashCode();
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
            final Message other = (Message) obj;
            if (getId() == null) {
                return false;
            } else return getId().equals(other.getId());
        }
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public boolean isNew() {
        return getId() == null;
    }

}
