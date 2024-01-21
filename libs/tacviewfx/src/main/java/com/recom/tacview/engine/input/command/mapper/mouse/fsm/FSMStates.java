package com.recom.tacview.engine.input.command.mapper.mouse.fsm;

enum FSMStates {
    NEW,
    IDLE,
    CLICK_CANDIDATE,
    MOUSE_DRAGGING,
    STOPPED
}
