package com.recom.persistence.message;

import com.recom.entity.Message;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
interface MessageRepository extends JpaRepository<Message, Long> {

    @NonNull
    List<Message> findAllByMapNameAndTimestampAfter(
            @NonNull final String mapName,
            @NonNull final LocalDateTime timestamp
    );

}