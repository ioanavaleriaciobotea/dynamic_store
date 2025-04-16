package com.grid.dynamicstore.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateDto {

    private String title;

    @Min(value = 0, message = "Available stock cannot be negative.")
    private Integer available;

    @Min(value = 0, message = "Price must be non-negative.")
    private BigDecimal price;
}
