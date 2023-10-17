package com.recom.mapper;

import com.recom.dto.message.MessageDto;
import com.recom.entity.Message;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper
public interface MessageMapper {

    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    @Named("timestampToTimestampEpochMilliseconds")
    static long timestampToTimestampEpochMilliseconds(@Nullable final LocalDateTime timestamp) {
        return timestamp.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    @Mapping(source = "timestamp", target = "timestampEpochMilliseconds", qualifiedByName = "timestampToTimestampEpochMilliseconds")
    MessageDto toDto(final Message entity);

}