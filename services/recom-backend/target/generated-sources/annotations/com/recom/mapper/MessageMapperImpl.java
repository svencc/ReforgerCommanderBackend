package com.recom.mapper;

import com.recom.dto.message.MessageDto;
import com.recom.entity.Message;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-11-22T23:34:37+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21 (Azul Systems, Inc.)"
)
public class MessageMapperImpl implements MessageMapper {

    @Override
    public MessageDto toDto(Message entity) {
        if ( entity == null ) {
            return null;
        }

        MessageDto.MessageDtoBuilder messageDto = MessageDto.builder();

        if ( entity.getTimestamp() != null ) {
            messageDto.timestampEpochMilliseconds( String.valueOf( MessageMapper.timestampToTimestampEpochMilliseconds( entity.getTimestamp() ) ) );
        }
        messageDto.uuid( entity.getUuid() );
        messageDto.messageType( entity.getMessageType() );
        messageDto.payload( entity.getPayload() );

        return messageDto.build();
    }
}
