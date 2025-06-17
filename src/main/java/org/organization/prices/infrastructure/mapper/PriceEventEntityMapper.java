package org.organization.prices.infrastructure.mapper;

import org.organization.prices.domain.model.PriceEvent;
import org.organization.prices.infrastructure.entity.PriceEventEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting between {@link PriceEventEntity} persistence entities and
 * {@link PriceEvent} domain models using ModelMapper.
 */
@Component
public class PriceEventEntityMapper {

    /**
     * ModelMapper instance used to perform the mapping between domain and entity objects.
     */
    private final ModelMapper modelMapper;

    /**
     * Constructs a new PriceEventEntityMapper with custom ModelMapper configuration.
     * Configures field matching, access level, ambiguity handling, and skips setting the ID on the entity.
     *
     * @param modelMapper the ModelMapper instance to be used
     */
    public PriceEventEntityMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        // Configures to ignore ambiguities that cause errors in TypeMap creation
        this.modelMapper.getConfiguration()
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        this.configureMappings();
    }

    /**
     * Configures explicit mappings between {@link PriceEvent} and {@link PriceEventEntity}.
     * Skips setting the ID field on the entity to avoid overwriting it.
     */
    private void configureMappings() {
        // Explicitly create TypeMap for PriceEvent -> PriceEventEntity and skip id field
        TypeMap<PriceEvent, PriceEventEntity> toEntityTypeMap = modelMapper.createTypeMap(PriceEvent.class, PriceEventEntity.class);
        toEntityTypeMap.addMappings(mapper -> {
            mapper.skip(PriceEventEntity::setId);  // Skip id to avoid overwriting entity's id
        });

        // Explicitly create TypeMap for PriceEventEntity -> PriceEvent (customizations can be added if needed)
        modelMapper.createTypeMap(PriceEventEntity.class, PriceEvent.class);
    }

    /**
     * Converts a {@link PriceEventEntity} to a {@link PriceEvent} domain model.
     *
     * @param entity the PriceEventEntity to convert
     * @return the corresponding PriceEvent domain model, or null if the entity is null
     */
    public PriceEvent toDomain(PriceEventEntity entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, PriceEvent.class);
    }

    /**
     * Converts a {@link PriceEvent} domain model to a {@link PriceEventEntity}.
     *
     * @param domain the PriceEvent domain model to convert
     * @return the corresponding PriceEventEntity, or null if the domain model is null
     */
    public PriceEventEntity toEntity(PriceEvent domain) {
        if (domain == null) {
            return null;
        }
        return modelMapper.map(domain, PriceEventEntity.class);
    }
}