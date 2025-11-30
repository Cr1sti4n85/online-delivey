package com.soracel.onlinemenu.service;

import com.soracel.onlinemenu.entity.CartEntity;
import com.soracel.onlinemenu.io.CartRequest;
import com.soracel.onlinemenu.io.CartResponse;
import com.soracel.onlinemenu.repository.CartRespository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService{

    private final CartRespository cartRespository;
    private final UserService userService;

    @Override
    public CartResponse addToCart( CartRequest request) {

        String loggedInUser = userService.findByUserId();

        Optional<CartEntity> cart = cartRespository.findByUserId(loggedInUser);
        //If not cart, create new cart
        CartEntity newCart = cart
                .orElseGet(() -> new CartEntity(loggedInUser, new HashMap<>()));
        Map<String, Integer> cartItems = newCart.getItems();
        cartItems.put(request.getFoodId(), cartItems.getOrDefault(request.getFoodId(), 0) + 1);
        newCart.setItems(cartItems);
        newCart =  cartRespository.save(newCart);
        return convertToCartResponse(newCart);
    }

    private CartResponse convertToCartResponse(CartEntity newCart) {
        return CartResponse.builder()
                .id(newCart.getId())
                .userId(newCart.getUserId())
                .items(newCart.getItems())
                .build();
    }
}
