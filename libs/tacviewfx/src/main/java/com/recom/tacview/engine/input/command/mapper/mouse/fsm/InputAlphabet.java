package com.recom.tacview.engine.input.command.mapper.mouse.fsm;

enum InputAlphabet {
    IDLEING,
    CLICK_THRESHOLD_EXCEEDED,
    MOUSE_PRESSED,
    MOUSE_PRESSED_TWICE,
    MOUSE_RELEASED,
    MOUSE_DRAG_STARTED,
    MOUSE_DRAGGING,
    UNHANDLED,
}
