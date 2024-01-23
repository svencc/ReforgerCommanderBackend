package com.recom.tacview.engine.input.command.mousebutton;

import com.recom.tacview.engine.input.command.IsCommand;
import javafx.scene.input.MouseEvent;

public interface IsMouseCommand<T extends MouseEvent> extends IsCommand<T> {

    double getPositionX();

    double getPositionY();

}
