package com.grid.dynamicstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductAddRequestDto {
    @NotBlank(message = "Title is required.")
    private String title;

    @NotNull(message = "Availability is required.")
    @Min(value = 0, message = "Available stock cannot be negative.")
    private Integer available;

    @NotNull(message = "Price is required.")
    @Min(value = 0, message = "Price must be non-negative.")
    private BigDecimal price;
}
