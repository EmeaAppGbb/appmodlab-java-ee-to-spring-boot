package com.medflow.ejb;

import com.medflow.entity.Order;
import com.medflow.entity.OrderItem;
import com.medflow.jms.OrderMessageProducer;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class OrderProcessingBean {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private InventoryBean inventoryBean;

    @EJB
    private OrderMessageProducer messageProducer;

    public Order findById(Long id) {
        return em.find(Order.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<Order> findAll() {
        Query query = em.createNamedQuery("Order.findAll");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Order> findByCustomer(Long customerId) {
        Query query = em.createNamedQuery("Order.findByCustomer");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Order> findByStatus(String status) {
        Query query = em.createNamedQuery("Order.findByStatus");
        query.setParameter("status", status);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Order> findByPriority(String priority) {
        Query query = em.createNamedQuery("Order.findByPriority");
        query.setParameter("priority", priority);
        return query.getResultList();
    }

    public Order createOrder(Order order) {
        order.setOrderDate(new Date());
        order.setStatus("PENDING");
        order.recalculateTotal();
        em.persist(order);
        messageProducer.sendOrderCreatedMessage(order.getId());
        return order;
    }

    public Order processOrder(Long orderId) {
        Order order = findById(orderId);
        if (order == null) {
            return null;
        }

        boolean allAvailable = true;
        for (OrderItem item : order.getItems()) {
            boolean available = inventoryBean.checkAvailability(
                    item.getProduct().getId(),
                    1L,
                    item.getQuantity()
            );
            if (!available) {
                allAvailable = false;
                break;
            }
        }

        if (allAvailable) {
            for (OrderItem item : order.getItems()) {
                inventoryBean.reduceStock(
                        item.getProduct().getId(),
                        1L,
                        item.getQuantity(),
                        item.getLotNumber()
                );
            }
            order.setStatus("CONFIRMED");
            messageProducer.sendOrderConfirmedMessage(orderId);
        } else {
            order.setStatus("BACKORDERED");
            messageProducer.sendOrderBackorderedMessage(orderId);
        }

        return em.merge(order);
    }

    public Order shipOrder(Long orderId) {
        Order order = findById(orderId);
        if (order != null) {
            order.setStatus("SHIPPED");
            em.merge(order);
            messageProducer.sendOrderShippedMessage(orderId);
        }
        return order;
    }

    public Order cancelOrder(Long orderId) {
        Order order = findById(orderId);
        if (order != null) {
            order.setStatus("CANCELLED");
            em.merge(order);
        }
        return order;
    }
}
