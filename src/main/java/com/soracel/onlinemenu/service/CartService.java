package com.soracel.onlinemenu.service;

import com.soracel.onlinemenu.io.CartRequest;
import com.soracel.onlinemenu.io.CartResponse;

public interface CartService {
    CartResponse addToCart(CartRequest req);
}
