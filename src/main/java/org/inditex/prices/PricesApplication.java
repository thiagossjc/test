package org.inditex.prices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main application class for the Inditex Prices API.
 */
@SpringBootApplication
@ComponentScan(basePackages = "org.inditex.prices")
public class PricesApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PricesApplication.class, args);
    }

}
