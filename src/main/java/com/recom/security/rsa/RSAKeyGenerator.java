package com.recom.security.rsa;

import com.nimbusds.jose.jwk.RSAKey;
import com.recom.property.RECOMSecurityProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
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
    private RSAKey hydrateKeyThroughFilesystem(@NonNull final String keyPath) throws Exception {
        if (keypairExists(keyPath)) {
            log.info("| +- Keys exist.");

            final PublicKey publicKey = loadPublicKeyFromFile(keyPath);
            final PrivateKey privateKey = loadPrivateKeyFromFile(keyPath);
            final String uuid = loadUUIDFromFile(keyPath);

            return new RSAKey.Builder((RSAPublicKey) publicKey)
                    .privateKey(privateKey)
                    .keyID(uuid)
                    .build();
        } else {
            log.info("| +- Keys do not exist.");
            log.info("| +- Generate key pair.");

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

    private boolean keypairExists(@NonNull final String keyPath) {
        final File publicKeyFile = new File(Paths.get(keyPath, KeyType.PUBLIC.name().toLowerCase()).toString());
        final File privateKeyFile = new File(Paths.get(keyPath, KeyType.PRIVATE.name().toLowerCase()).toString());

        return publicKeyFile.exists() && privateKeyFile.exists();
    }

    @NonNull
    private PublicKey loadPublicKeyFromFile(@NonNull final String filePath) throws IOException, GeneralSecurityException {
        final Path keyPath = Paths.get(filePath, KeyType.PUBLIC.name().toLowerCase());
        log.info("| +- Load public key: '{}'", keyPath);

        final byte[] keyBytes = Files.readAllBytes(keyPath);
        final X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        final KeyFactory keyFactory = KeyFactory.getInstance(Algorithm.RSA.name());

        return keyFactory.generatePublic(spec);
    }

    @NonNull
    private PrivateKey loadPrivateKeyFromFile(@NonNull final String filePath) throws IOException, GeneralSecurityException {
        final Path keyPath = Paths.get(filePath, KeyType.PRIVATE.name().toLowerCase());
        log.info("| +- Load private key: '{}'", keyPath);

        final byte[] keyBytes = Files.readAllBytes(keyPath);
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        final KeyFactory keyFactory = KeyFactory.getInstance(Algorithm.RSA.name());

        return keyFactory.generatePrivate(spec);
    }

    @NonNull
    private String loadUUIDFromFile(@NonNull final String filePath) throws IOException, GeneralSecurityException {
        final Path keyPath = Paths.get(filePath, "uuid");
        log.info("| +- Load uuid: '{}'", keyPath);

        final byte[] uuidBytes = Files.readAllBytes(keyPath);

        return new String(uuidBytes);
    }

    private void persistKeyToFile(
            @NonNull final String keyPath,
            @NonNull final KeyType keyType,
            final byte[] keyBytes
    ) throws IOException {
        final String filePath = Paths.get(keyPath, keyType.name().toLowerCase()).toString();

        new File(filePath).getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            log.info("| +- Save {} key: '{}'", keyType.name().toLowerCase(), filePath);
            fos.write(keyBytes);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    private void persistUUIDToFile(
            @NonNull final String keyPath,
            @NonNull final String uuid
    ) throws IOException {
        final String filePath = Paths.get(keyPath, "uuid").toString();

        new File(filePath).getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            log.info("| +- Save uuid: '{}'", filePath);
            fos.write(uuid.getBytes());
        } catch (IOException e) {
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
