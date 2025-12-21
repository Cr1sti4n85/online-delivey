package com.soracel.onlinemenu.controller;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.soracel.onlinemenu.io.OrderDetails;
import com.soracel.onlinemenu.io.OrderRequest;
import com.soracel.onlinemenu.io.OrderResponse;
import com.soracel.onlinemenu.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PostMapping("/payment")
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

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(){
        List<OrderResponse> resp =orderService.getUserOrders();
        return  ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId){
        orderService.removeOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders(){
        List<OrderResponse> resp =orderService.getAllOrdersFromUsers();
        return  ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PatchMapping("/status/{orderId}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable String orderId,
                                                  @RequestParam String status){
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.noContent().build();
    }
}
