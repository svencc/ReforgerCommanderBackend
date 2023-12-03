package com.recom.tacview.engine.input;

import com.recom.tacview.service.InputChannelService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@RequiredArgsConstructor
public class KeyboardInput implements KeyListener {

    @NonNull
    private final InputChannelService inputChannelService;
    private boolean up, down, left, right;
    private boolean[] keys = new boolean[999];


    public void update() {
        up = keys[KeyEvent.VK_W];
        down = keys[KeyEvent.VK_S];
        left = keys[KeyEvent.VK_A];
        right = keys[KeyEvent.VK_D];
    }

    @Override
    public void keyTyped(@NonNull final KeyEvent e) {
    }

    @Override
    public void keyPressed(@NonNull final KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(@NonNull final KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

}
