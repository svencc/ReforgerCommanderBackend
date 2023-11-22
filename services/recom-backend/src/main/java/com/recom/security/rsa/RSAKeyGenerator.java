package com.recom.security.rsa;

import com.nimbusds.jose.jwk.RSAKey;
import com.recom.property.RECOMSecurityProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class RSAKeyGenerator {

    @NonNull
    private final RECOMSecurityProperties recomSecurityProperties;

    @Bean("RECOMSecurityRsaKey")
    public RSAKey rsaKeyFactory() throws Exception {
        if (recomSecurityProperties.getKeyPath().isPresent()) {
            log.info("* Key path is present");
            final RSAKey rsaKey = hydrateKeyThroughFilesystem(recomSecurityProperties.getKeyPath().get());
            log.info("+--- RSA key hydrated through filesystem: {}", rsaKey.getKeyID());

            return rsaKey;
        } else {
            final String keyUuid = UUID.randomUUID().toString();
            log.info("* Generate temporary RSA key: {}", keyUuid);
            return generateRSAKey(generateKeypair(), keyUuid);
        }
    }

    @NonNull
    private RSAKey hydrateKeyThroughFilesystem(@NonNull final Path keyPath) throws Exception {
        if (keypairExists(keyPath)) {
            log.info("| +- Keys exist");

            final PublicKey publicKey = loadPublicKeyFromFile(keyPath);
            final PrivateKey privateKey = loadPrivateKeyFromFile(keyPath);
            final String uuid = loadUUIDFromFile(keyPath);

            return new RSAKey.Builder((RSAPublicKey) publicKey)
                    .privateKey(privateKey)
                    .keyID(uuid)
                    .build();
        } else {
            log.info("| +- Keys do not exist");
            log.info("| +- Generate key pair");

            final KeyPair keyPair = generateKeypair();
            persistKeyToFile(keyPath, KeyType.PUBLIC, keyPair.getPublic().getEncoded());
            persistKeyToFile(keyPath, KeyType.PRIVATE, keyPair.getPrivate().getEncoded());

            final String keyUuid = UUID.randomUUID().toString();
            persistUUIDToFile(keyPath, keyUuid);

            return generateRSAKey(keyPair, keyUuid);
        }
    }

    @NonNull
    private RSAKey generateRSAKey(
            @NonNull final KeyPair keyPair,
            @NonNull final String keyUuid
    ) {
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .keyID(keyUuid)
                .build();
    }

    @NonNull
    private KeyPair generateKeypair() throws NoSuchAlgorithmException {
        final KeyPairGenerator generator = KeyPairGenerator.getInstance(Algorithm.RSA.name());
        generator.initialize(2048);

        return generator.generateKeyPair();
    }

    private boolean keypairExists(@NonNull final Path keyPath) {
        final File publicKeyFile = new File(Paths.get(keyPath.toString(), KeyType.PUBLIC.name().toLowerCase()).toString());
        final File privateKeyFile = new File(Paths.get(keyPath.toString(), KeyType.PRIVATE.name().toLowerCase()).toString());

        return publicKeyFile.exists() && privateKeyFile.exists();
    }

    @NonNull
    private PublicKey loadPublicKeyFromFile(@NonNull final Path filePath) throws IOException, GeneralSecurityException {
        final Path keyPath = Paths.get(filePath.toString(), KeyType.PUBLIC.name().toLowerCase());
        log.info("| +- Load public key: '{}'", keyPath.toAbsolutePath());

        final FileSystemResource fileSystemResource = new FileSystemResource(keyPath);
        final byte[] keyBytes = fileSystemResource.getContentAsByteArray();
        final X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        final KeyFactory keyFactory = KeyFactory.getInstance(Algorithm.RSA.name());

        return keyFactory.generatePublic(spec);
    }

    @NonNull
    private PrivateKey loadPrivateKeyFromFile(@NonNull final Path filePath) throws IOException, GeneralSecurityException {
        final Path keyPath = Paths.get(filePath.toString(), KeyType.PRIVATE.name().toLowerCase());
        log.info("| +- Load private key: '{}'", keyPath.toAbsolutePath());

        final FileSystemResource fileSystemResource = new FileSystemResource(keyPath);
        final byte[] keyBytes = fileSystemResource.getContentAsByteArray();
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        final KeyFactory keyFactory = KeyFactory.getInstance(Algorithm.RSA.name());

        return keyFactory.generatePrivate(spec);
    }

    @NonNull
    private String loadUUIDFromFile(@NonNull final Path filePath) throws IOException, GeneralSecurityException {
        final Path keyPath = Paths.get(filePath.toString(), "uuid");
        log.info("| +- Load uuid: '{}'", keyPath.toAbsolutePath());

        final byte[] uuidBytes = Files.readAllBytes(keyPath);

        return new String(uuidBytes);
    }

    private void persistKeyToFile(
            @NonNull final Path keyPath,
            @NonNull final KeyType keyType,
            final byte[] keyBytes
    ) throws IOException {
        final Path filePath = Paths.get(keyPath.toString(), keyType.name().toLowerCase());

        final FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        fileSystemResource.getFile().getParentFile().mkdirs();

        try (final OutputStream fos = fileSystemResource.getOutputStream()) {
            log.info("| +- Save {} key: '{}'", keyType.name().toLowerCase(), filePath.toAbsolutePath());
            fos.write(keyBytes);
        } catch (final IOException e) {
            throw new IOException(e);
        }
    }

    private void persistUUIDToFile(
            @NonNull final Path keyPath,
            @NonNull final String uuid
    ) throws IOException {
        final Path filePath = Paths.get(keyPath.toString(), "uuid");

        final FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        fileSystemResource.getFile().getParentFile().mkdir();

        try (final OutputStream fos = fileSystemResource.getOutputStream()) {
            log.info("| +- Save uuid: '{}'", filePath.toAbsolutePath());
            fos.write(uuid.getBytes());
        } catch (final IOException e) {
            throw new IOException(e);
        }
    }

    private enum KeyType {
        PUBLIC, PRIVATE
    }

    private enum Algorithm {
        RSA
    }

}
