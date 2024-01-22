package com.recom.tacview.engine.input.command.mouse;

import com.recom.tacview.engine.input.command.IsInputCommand;

public interface IsMouseCommand extends IsInputCommand {

    double getPositionX();

    double getPositionY();

}
