package com.recom.model.message;

import com.recom.entity.GameMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageContainer {

    private GameMap gameMap;
    private List<SingleMessage> messages;

}
