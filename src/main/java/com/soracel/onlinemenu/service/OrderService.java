package com.soracel.onlinemenu.service;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.soracel.onlinemenu.io.OrderDetails;
import com.soracel.onlinemenu.io.OrderRequest;
import com.soracel.onlinemenu.io.OrderResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface OrderService {

    OrderDetails createOrderWithPayment(OrderRequest request) throws MPException, MPApiException;

    void verifyPayment(String xSignature, String dataId, String reqId);

    void updatePayment(Map<String, String> paymentData, String status);

    List<OrderResponse> getUserOrders();

    void removeOrder(String orderId);

    List<OrderResponse> getAllOrdersFromUsers();

    void updateOrderStatus(String orderId, String status);
}
