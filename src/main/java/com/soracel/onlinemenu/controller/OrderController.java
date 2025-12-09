package com.soracel.onlinemenu.controller;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.order.Order;
import com.mercadopago.resources.preference.Preference;
import com.soracel.onlinemenu.io.OrderDetails;
import com.soracel.onlinemenu.io.OrderRequest;
import com.soracel.onlinemenu.io.OrderResponse;
import com.soracel.onlinemenu.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDetails > createOrder(@RequestBody OrderRequest request) throws MPException, MPApiException {
        OrderDetails response = orderService.createOrderWithPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
