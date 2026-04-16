package com.medflow.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderMessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderMessageProducer.class);
    private static final String ORDER_QUEUE = "OrderQueue";

    private final JmsTemplate jmsTemplate;

    public OrderMessageProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendOrderCreatedMessage(Long orderId) {
        sendMessage(orderId, "ORDER_CREATED");
    }

    public void sendOrderConfirmedMessage(Long orderId) {
        sendMessage(orderId, "ORDER_CONFIRMED");
    }

    public void sendOrderBackorderedMessage(Long orderId) {
        sendMessage(orderId, "ORDER_BACKORDERED");
    }

    public void sendOrderShippedMessage(Long orderId) {
        sendMessage(orderId, "ORDER_SHIPPED");
    }

    private void sendMessage(Long orderId, String eventType) {
        try {
            jmsTemplate.convertAndSend(ORDER_QUEUE, orderId, message -> {
                message.setStringProperty("eventType", eventType);
                message.setLongProperty("orderId", orderId);
                message.setLongProperty("timestamp", System.currentTimeMillis());
                return message;
            });
            logger.info("Sent message: {} for order {}", eventType, orderId);
        } catch (Exception e) {
            logger.error("Error sending JMS message: {}", eventType, e);
        }
    }
}
