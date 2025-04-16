package com.grid.dynamicstore.dto;

import com.grid.dynamicstore.model.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class CartItemDto {

    private Long productId;

    private String title;

    private BigDecimal price;

    private int quantity;

    private BigDecimal subtotal;

    public CartItemDto(Product product, int quantity) {
        this.productId = product.getId();
        this.title = product.getTitle();
        this.price = product.getPrice();
        this.quantity = quantity;
        this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }
}

