package com.grid.dynamicstore.dto;

import com.grid.dynamicstore.model.OrderItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class OrderItemDto {
    private Long id;

    @NotNull(message = "Quantity is required.")
    private int quantity;

    @NotNull(message = "PriceEach is required.")
    private BigDecimal priceEach;

    @NotNull(message = "TotalPrice is required.")
    private BigDecimal totalPrice;

    @NotNull(message = "OrderId is required.")
    private Long orderId;

    @NotNull(message = "ProductId is required")
    private Long productId;

    @NotBlank(message = "ProductTitle is required.")
    private String productTitle;

    public OrderItemDto(OrderItem item) {
        this.id = item.getId();
        this.orderId = item.getOrder() != null ? item.getOrder().getId() : null;
        this.productId = item.getProduct().getId();
        this.productTitle = item.getProduct().getTitle();
        this.quantity = item.getQuantity();
        this.priceEach = item.getPriceEach();
        this.totalPrice = item.getTotalPrice();
    }
}
