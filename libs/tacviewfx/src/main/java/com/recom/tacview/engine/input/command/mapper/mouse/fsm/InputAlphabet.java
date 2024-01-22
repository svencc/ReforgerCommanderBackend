package com.recom.tacview.engine.input.command.mapper.mouse.fsm;

enum InputAlphabet {
    IDLEING,
    CLICK,
    MOUSE_PRESSED,
    DOUBLECLICK,
    MOUSE_RELEASED,
    MOUSE_DRAG_STARTED,
    MOUSE_DRAGGING,
    UNHANDLED,
}
