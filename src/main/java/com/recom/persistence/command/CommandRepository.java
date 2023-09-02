package com.recom.persistence.command;

import com.recom.entity.Account;
import com.recom.entity.Command;
import com.recom.entity.Configuration;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
interface CommandRepository extends JpaRepository<Command, Long> {

    @NonNull
    List<Command> findAllByMapName(@NonNull final String mapName);

}