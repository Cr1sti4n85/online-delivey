package com.soracel.onlinemenu.service;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.soracel.onlinemenu.io.OrderDetails;
import com.soracel.onlinemenu.io.OrderRequest;

public interface OrderService {

    OrderDetails createOrderWithPayment(OrderRequest request) throws MPException, MPApiException;
}
