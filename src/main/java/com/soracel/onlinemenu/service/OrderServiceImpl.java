package com.soracel.onlinemenu.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.soracel.onlinemenu.entity.OrderEntity;
import com.soracel.onlinemenu.io.OrderDetails;
import com.soracel.onlinemenu.io.OrderRequest;
import com.soracel.onlinemenu.io.OrderResponse;
import com.soracel.onlinemenu.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final UserService userService;
    @Value("${payment.token}")
    private String mpAccessToken;

    @Override
    public OrderDetails createOrderWithPayment(OrderRequest request) throws MPException, MPApiException {
        OrderEntity newOrder = convertToOrderEntity(request);
        newOrder = orderRepository.save(newOrder);

        //create mercadopago payment order
        MercadoPagoConfig.setAccessToken(mpAccessToken);

        PreferenceBackUrlsRequest backUrls =
                PreferenceBackUrlsRequest.builder()
                        .success("localhost:3000/success")
                        .pending("localhost:3000/pending")
                        .failure("localhost:3000/failure")
                        .build();

        PreferenceItemRequest itemRequest =
                PreferenceItemRequest.builder()
                        .id(newOrder.getId())
                        .title("Food delivery")
                        .description("El mejor delivery")
                        .pictureUrl("http://picture.com/PS5")
                        .categoryId("Food")
                        .quantity(1)
                        .currencyId("CLP")
                        .unitPrice(new BigDecimal(newOrder.getAmount()))
                        .build();
        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items).backUrls(backUrls).build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);
        newOrder.setPaymentOrderId(preference.getId());
        String loggedinUserId = userService.findByUserId();
        newOrder.setUserId(loggedinUserId);
        newOrder = orderRepository.save(newOrder);
        OrderResponse orderResponse = convertToOrderResponse(newOrder);

        return OrderDetails.builder()
                .order(orderResponse)
                .paymentUrl(preference.getSandboxInitPoint())
                .build();
    }

    private OrderEntity convertToOrderEntity(OrderRequest req){
        return OrderEntity.builder()
                .userAddress(req.getUserAddress())
                .amount(req.getAmount())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .orderStatus(req.getOrderStatus())
                .orderItems(req.getOrderedItems())
                .build();
    }

    private OrderResponse convertToOrderResponse(OrderEntity order){
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .email(order.getEmail())
                .phoneNumber(order.getPhoneNumber())
                .amount(order.getAmount())
                .userAddress(order.getUserAddress())
                .paymentOrderId(order.getPaymentOrderId())
                .orderStatus(order.getOrderStatus())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
