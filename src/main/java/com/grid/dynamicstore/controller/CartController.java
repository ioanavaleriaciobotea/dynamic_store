package com.grid.dynamicstore.controller;

import com.grid.dynamicstore.dto.CartAddRequestDto;
import com.grid.dynamicstore.dto.CartDto;
import com.grid.dynamicstore.model.Product;
import com.grid.dynamicstore.repository.ProductRepository;
import com.grid.dynamicstore.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductRepository productRepository;

    public CartController(CartService cartService, ProductRepository productRepository) {
        this.cartService = cartService;
        this.productRepository = productRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartAddRequestDto request,
                                       HttpSession session) {

        Optional<Product> optionalProduct = productRepository.findById(request.getId());

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found.");
        }

        Product product = optionalProduct.get();

        if (product.getAvailable() < request.getQuantity()) {
            return ResponseEntity.badRequest().body("Not enough quantity in stock.");
        }

        CartDto cart = cartService.getCart(session.getId());
        cart.addProduct(product.getId(), request.getQuantity());

        return ResponseEntity.ok("Product added to cart.");
    }
}
