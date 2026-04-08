package com.medflow.jms;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(
        name = "OrderMessageConsumer",
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/OrderQueue"),
                @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
        }
)
public class OrderMessageConsumer implements MessageListener {

    private static final Logger logger = Logger.getLogger(OrderMessageConsumer.class.getName());

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objMessage = (ObjectMessage) message;
                String eventType = objMessage.getStringProperty("eventType");
                Long orderId = objMessage.getLongProperty("orderId");

                logger.log(Level.INFO, "Received message: " + eventType + " for order " + orderId);

                switch (eventType) {
                    case "ORDER_CREATED":
                        handleOrderCreated(orderId);
                        break;
                    case "ORDER_CONFIRMED":
                        handleOrderConfirmed(orderId);
                        break;
                    case "ORDER_BACKORDERED":
                        handleOrderBackordered(orderId);
                        break;
                    case "ORDER_SHIPPED":
                        handleOrderShipped(orderId);
                        break;
                    default:
                        logger.log(Level.WARNING, "Unknown event type: " + eventType);
                }
            }
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "Error processing message", e);
        }
    }

    private void handleOrderCreated(Long orderId) {
        logger.log(Level.INFO, "Order created: " + orderId);
    }

    private void handleOrderConfirmed(Long orderId) {
        logger.log(Level.INFO, "Order confirmed: " + orderId);
    }

    private void handleOrderBackordered(Long orderId) {
        logger.log(Level.INFO, "Order backordered: " + orderId);
    }

    private void handleOrderShipped(Long orderId) {
        logger.log(Level.INFO, "Order shipped: " + orderId);
    }
}
