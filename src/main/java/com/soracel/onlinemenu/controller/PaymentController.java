package com.soracel.onlinemenu.controller;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    @Value("${payment.token}")
    private String mpAccessToken;

    @GetMapping
    public String pay() throws MPException, MPApiException {
        MercadoPagoConfig.setAccessToken(mpAccessToken);

        PreferenceBackUrlsRequest backUrls =
                PreferenceBackUrlsRequest.builder()
                        .success("localhost:3000/success")
                        .pending("localhost:3000/pending")
                        .failure("localhost:3000/failure")
                        .build();


        PreferenceItemRequest itemRequest =
                PreferenceItemRequest.builder()
                        .id("1234")
                        .title("Games")
                        .description("PS5")
                        .pictureUrl("http://picture.com/PS5")
                        .categoryId("games")
                        .quantity(2)
                        .currencyId("BRL")
                        .unitPrice(new BigDecimal("4000"))
                        .build();
        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items).backUrls(backUrls).build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return preference.getSandboxInitPoint();
    }

}
