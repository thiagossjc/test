package org.organization.prices.infrastructure.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper.
 * <p>
 * Provides a {@link ModelMapper} bean for object mapping purposes.
 * </p>
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Creates and provides a singleton {@link ModelMapper} bean.
     *
     * @return a new instance of {@link ModelMapper}
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
