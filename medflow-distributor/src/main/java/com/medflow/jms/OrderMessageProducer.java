package com.medflow.jms;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class OrderMessageProducer {

    private static final Logger logger = Logger.getLogger(OrderMessageProducer.class.getName());

    @Resource(lookup = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/queue/OrderQueue")
    private Queue orderQueue;

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
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(orderQueue);

            ObjectMessage message = session.createObjectMessage();
            message.setStringProperty("eventType", eventType);
            message.setLongProperty("orderId", orderId);
            message.setLongProperty("timestamp", System.currentTimeMillis());

            producer.send(message);
            logger.log(Level.INFO, "Sent message: " + eventType + " for order " + orderId);

            session.close();
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "Error sending JMS message: " + eventType, e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    logger.log(Level.SEVERE, "Error closing connection", e);
                }
            }
        }
    }
}
