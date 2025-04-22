package com.grid.dynamicstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartUpdateDto {

    @NotNull(message = "Product ID is required.")
    private Long id;

    @Min(value = 0, message = "Quantity cannot be negative.")
    private int quantity;
}
