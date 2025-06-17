package org.organization.prices.application.mapper;

import org.organization.prices.application.dto.PriceResponseDto;
import org.organization.prices.domain.execption.PriceNotFoundException;
import org.organization.prices.domain.model.Price;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Mapper for converting Price domain model to PriceResponseDto manually.
 */
@Component
public class PriceMapper {

    /**
     * Formatter for date and time with pattern "dd/MM/yyyy HH:mm:ss".
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Converts a Price to a PriceResponseDto.
     *
     * @param price the Price to convert
     * @return the converted PriceResponseDto, or null if price is null
     */
    public PriceResponseDto toResponse(Price price) {
        if (price == null) {
            throw new PriceNotFoundException("Price not found");
        }

        if(price.getCurrency()==null){
            throw new PriceNotFoundException("Price not found");
        }

        return PriceResponseDto.builder()
                .productId(price.getProductId())
                .brandId(price.getBrandId())
                .priceList(price.getPriceList())
                .startDate(price.getStartDate() != null ? price.getStartDate().format(FORMATTER) : null)
                .endDate(price.getEndDate() != null ? price.getEndDate().format(FORMATTER) : null)
                .price(price.getPrice())
                .build();
    }
}