package org.organization.prices.infrastructure.mapper;

import org.organization.prices.domain.model.Price;
import org.organization.prices.infrastructure.entity.PriceEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.stereotype.Component;

/**
 * Mapper component to convert between {@link Price} domain model and {@link PriceEntity} persistence entity.
 */
@Component
public class PriceEntityMapper {

    /**
     * ModelMapper instance used for mapping between domain and entity objects.
     */
    private final ModelMapper modelMapper;

    /**
     * Constructs a new PriceEntityMapper with custom ModelMapper configuration.
     * Configures field matching, access level, ambiguity handling, and skips setting ID on entity.
     */
    public PriceEntityMapper() {
        this.modelMapper = new ModelMapper();

        // Configuration to avoid ambiguities and enforce private field mapping
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setAmbiguityIgnored(true);

        // Explicitly create a TypeMap to skip setting the ID on the destination entity
        var typeMap = modelMapper.createTypeMap(Price.class, PriceEntity.class);
        typeMap.addMappings(mapper -> mapper.skip(PriceEntity::setId));
    }

    /**
     * Converts a {@link PriceEntity} to a {@link Price} domain model.
     *
     * @param entity the PriceEntity to convert
     * @return the corresponding Price domain model, or null if the entity is null
     */
    public Price toDomain(PriceEntity entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, Price.class);
    }

    /**
     * Converts a {@link Price} domain model to a {@link PriceEntity}.
     *
     * @param domain the Price domain model to convert
     * @return the corresponding PriceEntity, or null if the domain model is null
     */
    public PriceEntity toEntity(Price domain) {
        if (domain == null) return null;
        return modelMapper.map(domain, PriceEntity.class);
    }
}