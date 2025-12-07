package com.soracel.onlinemenu.service;

import com.soracel.onlinemenu.io.CartRequest;
import com.soracel.onlinemenu.io.CartResponse;

public interface CartService {
    CartResponse addToCart(CartRequest req);

    CartResponse getCart();

    void clearCart();

    CartResponse removeFromCart(CartRequest request);
}
