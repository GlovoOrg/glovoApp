package com.api.glovoCRM.Utils.Stripe;

import com.api.glovoCRM.Models.OrderDetailModels.Order;
import com.api.glovoCRM.Models.OrderDetailModels.PaymentDetail;
import com.api.glovoCRM.constants.PaymentStatus;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {
    private static final String SUCCESS_PATH = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    private static final String CANCEL_PATH = "https://www.youtube.com/watch?v=umTcXqvJ5JU";
    private static final String CURRENCY = "kgs";
    private static final String PROVIDER = "stripe";

    public PaymentDetail createCheckoutSession(Order order) throws StripeException {

        SessionCreateParams params = buildSessionParams(order);

        Session session = Session.create(params);

        return buildPaymentDetail(order, session);
    }

    private SessionCreateParams buildSessionParams(Order order) {
        return SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_PATH)
                .setCancelUrl(CANCEL_PATH)
                .addLineItem(createLineItem(order))
                .build();
    }

    private SessionCreateParams.LineItem createLineItem(Order order) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(buildPriceData(order))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData buildPriceData(Order order) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(CURRENCY)
                .setUnitAmount(order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue())
                .setProductData(buildProductData(order))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData buildProductData(Order order) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName("Order")
                .build();
    }

    private PaymentDetail buildPaymentDetail(Order order, Session session) {
        PaymentDetail paymentDetail = new PaymentDetail();
        paymentDetail.setOrder(order);
        paymentDetail.setSessionId(session.getId());
        paymentDetail.setTransactionId(session.getPaymentIntent());
        paymentDetail.setStatus(PaymentStatus.PENDING);
        paymentDetail.setAmount(order.getTotalAmount());
        paymentDetail.setCurrency(CURRENCY);
        paymentDetail.setProvider(PROVIDER);
        paymentDetail.setPaymentUrl(session.getUrl());
        paymentDetail.setPaid(false);
        return paymentDetail;
    }
}
