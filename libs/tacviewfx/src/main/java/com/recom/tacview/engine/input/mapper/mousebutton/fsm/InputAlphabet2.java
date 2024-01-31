package com.recom.tacview.engine.input.mapper.mousebutton.fsm;

enum InputAlphabet2 {
    IDLEING,
    CLICK,
    DOUBLECLICK,
    MOUSE_PRESSED__DRAG_START,
    MOUSE_DRAGGING,
    MOUSE_RELEASED__DRAG_STOPPED,
    UNHANDLED,
}
