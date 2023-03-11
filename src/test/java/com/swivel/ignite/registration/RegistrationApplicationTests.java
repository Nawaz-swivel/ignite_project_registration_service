package com.swivel.ignite.registration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class tests {@link RegistrationApplication} class
 */
@SpringBootTest
class RegistrationApplicationTests {

    /**
     * This method tests Spring application main run method
     */
    @Test
    void Should_RunSpringApplication() {
        RegistrationApplication.main(new String[]{});

        assertTrue(true, "Spring Application Context Loaded Successfully");
    }
}
