package com.recom.tacview.engine.input.command.mouse;

import com.recom.tacview.engine.input.command.IsInputCommand;
import javafx.scene.input.MouseEvent;

public interface IsMouseCommand<T extends MouseEvent> extends IsInputCommand<T> {

    double getPositionX();

    double getPositionY();

}
