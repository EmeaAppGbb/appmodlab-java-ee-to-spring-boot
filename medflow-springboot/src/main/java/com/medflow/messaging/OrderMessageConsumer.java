package com.medflow.messaging;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderMessageConsumer.class);

    @JmsListener(destination = "OrderQueue")
    public void onMessage(Message message) {
        try {
            String eventType = message.getStringProperty("eventType");
            Long orderId = message.getLongProperty("orderId");

            logger.info("Received message: {} for order {}", eventType, orderId);

            switch (eventType) {
                case "ORDER_CREATED" -> handleOrderCreated(orderId);
                case "ORDER_CONFIRMED" -> handleOrderConfirmed(orderId);
                case "ORDER_BACKORDERED" -> handleOrderBackordered(orderId);
                case "ORDER_SHIPPED" -> handleOrderShipped(orderId);
                default -> logger.warn("Unknown event type: {}", eventType);
            }
        } catch (JMSException e) {
            logger.error("Error processing message", e);
        }
    }

    private void handleOrderCreated(Long orderId) {
        logger.info("Order created: {}", orderId);
    }

    private void handleOrderConfirmed(Long orderId) {
        logger.info("Order confirmed: {}", orderId);
    }

    private void handleOrderBackordered(Long orderId) {
        logger.info("Order backordered: {}", orderId);
    }

    private void handleOrderShipped(Long orderId) {
        logger.info("Order shipped: {}", orderId);
    }
}
