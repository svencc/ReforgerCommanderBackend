package com.recom.tacview.engine.input.command.mapper.mouse.fsm;

enum InputAlphabet {
    IDLING,
    CLICK_THRESHOLD_EXCEEDED,
    DOUBLECLICK_THRESHOLD_EXCEEDED,
    CLICK_COMMAND,
    DOUBLE_CLICK_COMMAND,
    MOUSE_PRESSED,
    MOUSE_RELEASED,
    MOUSE_DRAG_STARTED,
    MOUSE_DRAGGING,
    UNHANDLED,
    STOP
}
