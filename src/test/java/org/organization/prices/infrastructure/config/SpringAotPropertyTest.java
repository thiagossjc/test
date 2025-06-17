package org.organization.prices.infrastructure.config;

import org.junit.jupiter.api.Test;

/**
 * Test class to check the value of the {@code spring.aot.enabled} system property.
 * <p>
 * This is useful for verifying whether the application is running with Spring AOT (Ahead-of-Time) optimizations enabled.
 * </p>
 */
public class SpringAotPropertyTest {

    /**
     * Prints the value of the {@code spring.aot.enabled} system property to the standard output.
     * <p>
     * This test helps determine whether Spring AOT is currently enabled during the test execution.
     * </p>
     */
    @Test
    void checkSpringAotProperty() {
        System.out.println("spring.aot.enabled = " + System.getProperty("spring.aot.enabled"));
    }
}
