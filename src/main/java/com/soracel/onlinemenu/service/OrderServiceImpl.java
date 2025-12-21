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
import com.soracel.onlinemenu.exceptions.OrderNotFoundException;
import com.soracel.onlinemenu.io.OrderDetails;
import com.soracel.onlinemenu.io.OrderRequest;
import com.soracel.onlinemenu.io.OrderResponse;
import com.soracel.onlinemenu.repository.CartRespository;
import com.soracel.onlinemenu.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartRespository cartRespository;

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
        newOrder.setPreferenceId(preference.getId());
        String loggedinUserId = userService.findByUserId();
        newOrder.setUserId(loggedinUserId);
        newOrder = orderRepository.save(newOrder);
        OrderResponse orderResponse = convertToOrderResponse(newOrder);

        return OrderDetails.builder()
                .order(orderResponse)
                .paymentUrl(preference.getSandboxInitPoint())
                .build();
    }

    @Override
    public void verifyPayment(String xSignature, String dataId, String reqId) {

        if (xSignature == null || xSignature.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing x-signature");
        }

        Map<String, String> signatureValues = parseSignature(xSignature);

        String ts = signatureValues.get("ts");
        String v1 = signatureValues.get("v1");

        if (ts == null || v1 == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid x-signature format");
        }

        //encrypt and compare
        String signedTemplate = "id:" + dataId +
                        ";request-id:" + reqId +
                        ";ts:" + ts;


        String cypheredSignature = new HmacUtils("HmacSHA256", "aca va la clave secreta").hmacHex(signedTemplate);

        boolean valid = MessageDigest.isEqual(
                cypheredSignature.getBytes(StandardCharsets.UTF_8),
                v1.getBytes(StandardCharsets.UTF_8)
        );

        if (!valid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid signature");
        }

    }

    @Override
    public void updatePayment(Map<String, String> paymentData, String status){

        String preferenceId = paymentData.get("preference");
        OrderEntity existingOrder = orderRepository
                .findByPreferenceId(preferenceId)
                .orElseThrow(OrderNotFoundException::new);

        existingOrder.setPaymentId(paymentData.get("payment_id"));
        existingOrder.setPaymentStatus(paymentData.get("payment_status"));

        orderRepository.save(existingOrder);
        if (status.equalsIgnoreCase("paid")){
            cartRespository.deleteByUserId(existingOrder.getUserId());
        }
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
                .preferenceId(order.getPreferenceId())
                .orderStatus(order.getOrderStatus())
                .orderStatus(order.getOrderStatus())
                .build();
    }

    private Map<String, String> parseSignature(String header) {
        return Arrays.stream(header.split(","))
                .map(part -> part.split("=", 2))
                .collect(Collectors.toMap(
                        arr -> arr[0],
                        arr -> arr[1]
                ));
    }
}
