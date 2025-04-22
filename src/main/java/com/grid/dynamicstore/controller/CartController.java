package com.grid.dynamicstore.controller;

import com.grid.dynamicstore.dto.*;
import com.grid.dynamicstore.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartAddDto request,
                                       HttpSession session) {

        cartService.addProduct(session.getId(), request.getId(), request.getQuantity());

        return ResponseEntity.ok("Product added to cart.");
    }

    @GetMapping
    public ResponseEntity<CartResponseDto> viewCart(HttpSession session) {

        return ResponseEntity.ok(cartService.viewCart(session.getId()));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long productId, HttpSession session) {

        cartService.removeProduct(session.getId(), productId);

        return ResponseEntity.ok("Product removed from cart.");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCartItem(@Valid @RequestBody CartUpdateDto request, HttpSession session) {

        CartItemDto updated = cartService.updateProduct(session.getId(), request.getId(), request.getQuantity());

        if (request.getQuantity() == 0) {
            return ResponseEntity.ok("Item removed from cart.");
        }

        return ResponseEntity.ok(updated);
    }
}
