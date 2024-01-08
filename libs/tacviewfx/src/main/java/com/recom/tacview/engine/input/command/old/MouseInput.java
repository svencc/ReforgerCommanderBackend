package com.recom.tacview.engine.input.command.old;

import com.recom.tacview.service.InputChannelService;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON3;

@RequiredArgsConstructor
public class MouseInput implements MouseListener, MouseMotionListener {

    @NonNull
    private final InputChannelService inputChannelService;
    @Getter
    private int mouseX = -1;
    @Getter
    private int mouseY = -1;
    @Getter
    private boolean mousePrimaryButton = false;
    @Getter
    private boolean mouseSecondaryButton = false;

    @Override
    public void mouseClicked(@NonNull final MouseEvent e) {

    }

    @Override
    public void mousePressed(@NonNull final MouseEvent e) {
        if (e.getButton() == BUTTON1) mousePrimaryButton = true;
        if (e.getButton() == BUTTON3) mouseSecondaryButton = true;
    }

    @Override
    public void mouseReleased(@NonNull final MouseEvent e) {
        if (e.getButton() == BUTTON1) mousePrimaryButton = false;
        if (e.getButton() == BUTTON3) mouseSecondaryButton = false;
    }

    @Override
    public void mouseEntered(@NonNull final MouseEvent e) {

    }

    @Override
    public void mouseExited(@NonNull final MouseEvent e) {

    }

    @Override
    public void mouseDragged(@NonNull final MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(@NonNull final MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

}
