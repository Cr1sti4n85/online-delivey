package com.soracel.onlinemenu.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OrderDetails {
    private OrderResponse order;
    private String paymentUrl;
}
