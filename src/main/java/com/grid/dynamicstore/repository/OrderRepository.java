package com.grid.dynamicstore.repository;

import com.grid.dynamicstore.model.Order;
import com.grid.dynamicstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findByUserId(Long userId);
}
