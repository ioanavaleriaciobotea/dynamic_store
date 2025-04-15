package com.grid.dynamicstore.repository;

import com.grid.dynamicstore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
