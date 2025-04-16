package com.grid.dynamicstore.dto;

import com.grid.dynamicstore.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class ProductDto {

    private Long id;

    @NotBlank(message = "Title is required.")
    private String title;

    private int available;

    @NotNull(message = "Price is required.")
    private BigDecimal price;

    public ProductDto(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.available = product.getAvailable();
        this.price = product.getPrice();
    }
}
