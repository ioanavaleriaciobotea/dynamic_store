package com.grid.dynamicstore.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"order"})
@ToString
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orderItem_generator")
    @SequenceGenerator(name = "orderItem_generator", sequenceName = "orderItem_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal priceEach;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    // Many order items -> one order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // Many order items -> one product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
