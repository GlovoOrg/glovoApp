package com.api.glovoCRM.Utils.Stripe;

import com.api.glovoCRM.Repositories.UserRepositories.PaymentDetailRepository;
import com.api.glovoCRM.Models.OrderDetailModels.PaymentDetail;
import com.api.glovoCRM.constants.PaymentStatus;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebhookService {
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private final PaymentDetailRepository paymentDetailRepository;

    @Autowired
    public WebhookService(PaymentDetailRepository paymentDetailRepository) {
        this.paymentDetailRepository = paymentDetailRepository;
    }



    public void handleStripeEvent(Event event) {
        log.info("Handling Stripe event: {}", event.getType());

        switch (event.getType()) {
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(event);
                break;
            case "checkout.session.expired":
                handleCheckoutSessionExpired(event);
                break;
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(event);
                break;
            case "payment_intent.created":
                log.info("PaymentIntent created for ID: {}, no action required yet",
                        event.getDataObjectDeserializer().getObject().map(obj -> ((PaymentIntent) obj).getId()).orElse("unknown"));
                break;
            default:
                log.info("Unhandled event type: {}", event.getType());
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null) {
            log.warn("Failed to deserialize checkout session from event: {}", event.getId());
            return;
        }

        log.info("Received checkout.session.completed for sessionId: {}", session.getId());
        PaymentDetail paymentDetail = paymentDetailRepository.findBySessionId(session.getId());
        if (paymentDetail == null) {
            log.warn("PaymentDetail not found for sessionId: {}", session.getId());
            return;
        }

        log.info("Found PaymentDetail with ID: {} for orderId: {}", paymentDetail.getId(), paymentDetail.getOrder().getId());
        paymentDetail.setStatus(PaymentStatus.PAID);
        paymentDetail.setTransactionId(session.getPaymentIntent());
        paymentDetail.setPaid(true);
        paymentDetailRepository.save(paymentDetail);
        log.info("Saved PaymentDetail with status: {}", paymentDetail.getStatus());

        log.info("PaymentDetail updated to PAID for orderId: {}", paymentDetail.getOrder().getId());
    }

    private void handleCheckoutSessionExpired(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null) {
            log.warn("Failed to deserialize checkout session from event: {}", event.getId());
            return;
        }

        PaymentDetail paymentDetail = paymentDetailRepository.findBySessionId(session.getId());
        if (paymentDetail == null) {
            log.warn("PaymentDetail not found for sessionId: {}", session.getId());
            return;
        }

        paymentDetail.setStatus(PaymentStatus.CANCELED);
        paymentDetailRepository.save(paymentDetail);
        log.info("Saved PaymentDetail with status: {}", paymentDetail.getStatus());

        log.info("PaymentDetail updated to CANCELED for orderId: {}", paymentDetail.getOrder().getId());
    }

    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent == null) {
            log.warn("Failed to deserialize PaymentIntent from event: {}", event.getId());
            return;
        }

        // Ищем PaymentDetail по transactionId (PaymentIntent ID)
        PaymentDetail paymentDetail = paymentDetailRepository.findBySessionId(paymentIntent.getId());
        if (paymentDetail == null) {
            log.warn("PaymentDetail not found for PaymentIntent ID: {}", paymentIntent.getId());
            return;
        }

        paymentDetail.setStatus(PaymentStatus.PAID);
        paymentDetail.setTransactionId(paymentIntent.getId());
        paymentDetail.setPaid(true);
        paymentDetailRepository.save(paymentDetail);
        log.info("Saved PaymentDetail with status: {}", paymentDetail.getStatus());

        log.info("PaymentDetail updated to PAID for orderId: {}", paymentDetail.getOrder().getId());
    }
}
