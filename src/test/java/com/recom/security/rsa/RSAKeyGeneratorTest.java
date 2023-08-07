package com.recom.security.rsa;

import com.recom.property.RECOMSecurityProperties;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RSAKeyGeneratorTest {

    @Mock
    private RECOMSecurityProperties recomSecurityProperties;
    @InjectMocks
    private RSAKeyGenerator rsaKeyGenerator;


    @Test
    @Disabled
    void rsaKeyFactory() {
        //        @TODO
        // extract file operations to a separate service before I can write tests for this
    }

}