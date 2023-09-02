package com.recom.mapper;

import com.recom.dto.command.CommandDto;
import com.recom.entity.Command;
import lombok.NonNull;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper
public interface CommandMapper {

    CommandMapper INSTANCE = Mappers.getMapper(CommandMapper.class);

    @NonNull
    @Named("timestampToTimestampEpochMilliseconds")
    static long timestampToTimestampEpochMilliseconds(@Nullable final LocalDateTime timestamp) {
        return timestamp.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    @Mapping(source = "timestamp", target = "timestampEpochMilliseconds", qualifiedByName = "timestampToTimestampEpochMilliseconds")
    CommandDto toDto(final Command entity);

}