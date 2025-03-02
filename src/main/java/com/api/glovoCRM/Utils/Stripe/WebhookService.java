package com.api.glovoCRM.Utils.Stripe;

import com.api.glovoCRM.DAOs.UserDAOs.PaymentDetailDAO;
import com.api.glovoCRM.DTOs.OrderDTOs.PaymentDetailDTO;
import com.api.glovoCRM.Models.OrderDetailModels.PaymentDetail;
import com.api.glovoCRM.constants.PaymentStatus;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebhookService {
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private final PaymentDetailDAO paymentDetailDAO;

    @Autowired
    public WebhookService(PaymentDetailDAO paymentDetailDAO) {
        this.paymentDetailDAO = paymentDetailDAO;
    }



    public void handleStripeEvent(Event event) {
        logger.info("Handling Stripe event: {}", event.getType());

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
                logger.info("PaymentIntent created for ID: {}, no action required yet",
                        event.getDataObjectDeserializer().getObject().map(obj -> ((PaymentIntent) obj).getId()).orElse("unknown"));
                break;
            default:
                logger.info("Unhandled event type: {}", event.getType());
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null) {
            logger.warn("Failed to deserialize checkout session from event: {}", event.getId());
            return;
        }

        logger.info("Received checkout.session.completed for sessionId: {}", session.getId());
        PaymentDetail paymentDetail = paymentDetailDAO.findBySessionId(session.getId());
        if (paymentDetail == null) {
            logger.warn("PaymentDetail not found for sessionId: {}", session.getId());
            return;
        }

        logger.info("Found PaymentDetail with ID: {} for orderId: {}", paymentDetail.getId(), paymentDetail.getOrder().getId());
        paymentDetail.setStatus(PaymentStatus.PAID);
        paymentDetail.setTransactionId(session.getPaymentIntent());
        paymentDetail.setPaid(true);
        paymentDetailDAO.save(paymentDetail);
        logger.info("Saved PaymentDetail with status: {}", paymentDetail.getStatus());

        logger.info("PaymentDetail updated to PAID for orderId: {}", paymentDetail.getOrder().getId());
    }

    private void handleCheckoutSessionExpired(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null) {
            logger.warn("Failed to deserialize checkout session from event: {}", event.getId());
            return;
        }

        PaymentDetail paymentDetail = paymentDetailDAO.findBySessionId(session.getId());
        if (paymentDetail == null) {
            logger.warn("PaymentDetail not found for sessionId: {}", session.getId());
            return;
        }

        paymentDetail.setStatus(PaymentStatus.CANCELED);
        paymentDetailDAO.save(paymentDetail);
        logger.info("Saved PaymentDetail with status: {}", paymentDetail.getStatus());

        logger.info("PaymentDetail updated to CANCELED for orderId: {}", paymentDetail.getOrder().getId());
    }

    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent == null) {
            logger.warn("Failed to deserialize PaymentIntent from event: {}", event.getId());
            return;
        }

        // Ищем PaymentDetail по transactionId (PaymentIntent ID)
        PaymentDetail paymentDetail = paymentDetailDAO.findBySessionId(paymentIntent.getId());
        if (paymentDetail == null) {
            logger.warn("PaymentDetail not found for PaymentIntent ID: {}", paymentIntent.getId());
            return;
        }

        paymentDetail.setStatus(PaymentStatus.PAID);
        paymentDetail.setTransactionId(paymentIntent.getId());
        paymentDetail.setPaid(true);
        paymentDetailDAO.save(paymentDetail);
        logger.info("Saved PaymentDetail with status: {}", paymentDetail.getStatus());

        logger.info("PaymentDetail updated to PAID for orderId: {}", paymentDetail.getOrder().getId());
    }
}
