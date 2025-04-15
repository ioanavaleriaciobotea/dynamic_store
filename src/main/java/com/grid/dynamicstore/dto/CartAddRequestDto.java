package com.grid.dynamicstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartAddRequestDto {

    @NotNull(message = "Product ID is required.")
    private Long id;

    @Min(value = 1, message = "Quantity must be at least 1.")
    private int quantity;
}
