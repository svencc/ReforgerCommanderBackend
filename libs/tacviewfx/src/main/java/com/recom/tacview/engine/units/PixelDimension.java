package com.recom.tacview.engine.units;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

@Getter
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PixelDimension {

    @NonNull
    private static final HashMap<String, PixelDimension> INSTANCE_LIST = new HashMap<>();

    private final int widthX;
    private final int heightY;

    @NonNull
    public static PixelDimension of(
            final int widthX,
            final int heightY
    ) {
        final String hashKey = String.format("%d#%d", widthX, heightY);
        if (!INSTANCE_LIST.containsKey(hashKey)) {
            INSTANCE_LIST.put(hashKey, new PixelDimension(widthX, heightY));
        }

        return INSTANCE_LIST.get(hashKey);
    }

}
