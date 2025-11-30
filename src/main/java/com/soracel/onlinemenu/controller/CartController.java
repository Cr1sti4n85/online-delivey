package com.soracel.onlinemenu.controller;

import com.soracel.onlinemenu.io.CartRequest;
import com.soracel.onlinemenu.io.CartResponse;
import com.soracel.onlinemenu.service.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/cart")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody CartRequest cartRequest){

        CartResponse cart = cartService.addToCart(cartRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }
}
