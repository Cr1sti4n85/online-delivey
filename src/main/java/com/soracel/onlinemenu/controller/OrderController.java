package com.soracel.onlinemenu.controller;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.soracel.onlinemenu.io.OrderDetails;
import com.soracel.onlinemenu.io.OrderRequest;
import com.soracel.onlinemenu.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


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

    @PostMapping("payment")
    public ResponseEntity<Void> verifyPayment(@RequestParam(name = "data.id") String dataId,
                                              @RequestHeader("x-request-id") String reqId,
                                              @RequestHeader("x-signature") String xSignature) {

        orderService.verifyPayment(xSignature, dataId, reqId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updatePayment(@RequestBody  Map<String, String> paymentData,
                              @RequestBody String status){

        orderService.updatePayment(paymentData, status);

        return ResponseEntity.noContent().build();
    }
}
