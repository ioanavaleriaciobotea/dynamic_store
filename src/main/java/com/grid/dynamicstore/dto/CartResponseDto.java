package com.grid.dynamicstore.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
public class CartResponseDto {
    private List<CartItemDto> items;
    private BigDecimal total;

    public CartResponseDto(List<CartItemDto> items) {
        this.items = items;
        this.total = items.stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
