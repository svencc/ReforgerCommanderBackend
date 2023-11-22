package com.recom.testhelper;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@UtilityClass
public class SerializeObjectHelper {

    @NonNull
    @SneakyThrows
    public ByteArrayOutputStream serializeObjectHelper(@NonNull final Serializable object) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
        }
        return byteArrayOutputStream;
    }

}
