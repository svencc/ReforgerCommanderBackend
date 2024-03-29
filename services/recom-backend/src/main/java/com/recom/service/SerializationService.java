package com.recom.service;

import com.recom.exception.DBCachedDeserializationException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Service
public class SerializationService {

    @NonNull
    public ByteArrayOutputStream serializeObject(@NonNull final Serializable object) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
        }

        return byteArrayOutputStream;
    }

    @NonNull
    public <V extends Serializable> Optional<V> deserializeObject(final byte[] serializedValue) throws DBCachedDeserializationException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(serializedValue))) {
            return Optional.ofNullable((V) inputStream.readObject());
        } catch (final IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    @NonNull
    public void writeBytesToFile(
            @NonNull final Path path,
            final byte[] byteArray
    ) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
            fileOutputStream.write(byteArray);
        } catch (IOException e) {
            log.error("Could not write bytes to file ({})!", path.toAbsolutePath());
            throw e;
        }
    }

    @NonNull
    public byte[] readBytesFromFile(@NonNull final Path path) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
            return fileInputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Could not read bytes from file ({})!", path.toAbsolutePath());
            throw e;
        }
    }

}
