package com.recom.model.message;

import com.recom.dto.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleMessage {

    private MessageType messageType;
    private Object payload;

}
