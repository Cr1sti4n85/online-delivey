package com.soracel.onlinemenu.service;

import com.soracel.onlinemenu.entity.CartEntity;
import com.soracel.onlinemenu.io.CartRequest;
import com.soracel.onlinemenu.io.CartResponse;
import com.soracel.onlinemenu.repository.CartRespository;
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

    @Override
    public CartResponse getCart() {
        String loggedInUser = userService.findByUserId();
        CartEntity cart = cartRespository
                .findByUserId(loggedInUser)
                .orElse(new CartEntity(loggedInUser, new HashMap<>()));

        return convertToCartResponse(cart);

    }

    @Override
    public void clearCart() {
        String loggedInUser = userService.findByUserId();
        cartRespository.deleteByUserId(loggedInUser);
    }

    @Override
    public CartResponse removeFromCart(CartRequest request) {
        String loggedInUser = userService.findByUserId();
        CartEntity cartEntity = cartRespository.findByUserId(loggedInUser)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        Map<String, Integer> cartItems = cartEntity.getItems();
        if (cartItems.containsKey(request.getFoodId())){
            int currentQty = cartItems.get(request.getFoodId());
            if(currentQty > 0) cartItems.put(request.getFoodId(), currentQty - 1);
            currentQty -= 1;
            if (currentQty == 0) cartItems.remove(request.getFoodId());

            cartEntity = cartRespository.save(cartEntity);

        }

        return convertToCartResponse(cartEntity);
    }


    private CartResponse convertToCartResponse(CartEntity newCart) {
        return CartResponse.builder()
                .id(newCart.getId())
                .userId(newCart.getUserId())
                .items(newCart.getItems())
                .build();
    }
}
