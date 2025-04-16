package com.grid.dynamicstore.dto;

import com.grid.dynamicstore.model.Order;
import com.grid.dynamicstore.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class OrderDto {

    private Long id;

    private LocalDateTime createdAt;

    @NotNull(message = "Total is required.")
    private BigDecimal total;

    @NotNull(message = "Status is required.")
    private OrderStatus status;

    @NotNull(message = "UserId is required.")
    private Long userId;

    public OrderDto(Order order) {
        this.id = order.getId();
        this.createdAt = order.getCreatedAt();
        this.total = order.getTotal();
        this.status = order.getStatus();
        this.userId = order.getUser().getId();
    }
}
