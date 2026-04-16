package com.medflow.service;

import com.medflow.entity.Order;
import com.medflow.entity.OrderItem;
import com.medflow.repository.OrderItemRepository;
import com.medflow.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryService inventoryService;

    public OrderProcessingService(OrderRepository orderRepository,
                                  OrderItemRepository orderItemRepository,
                                  InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        logger.debug("Finding order by id: {}", id);
        return orderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        logger.debug("Finding all orders");
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Order> findByCustomer(Long customerId) {
        logger.debug("Finding orders by customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Order> findByStatus(String status) {
        logger.debug("Finding orders by status: {}", status);
        return orderRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Order> findByPriority(String priority) {
        logger.debug("Finding orders by priority: {}", priority);
        return orderRepository.findByPriority(priority);
    }

    public Order createOrder(Order order) {
        order.setOrderDate(new Date());
        order.setStatus("PENDING");
        order.recalculateTotal();
        Order saved = orderRepository.save(order);
        logger.info("Created order: {}", saved.getId());
        return saved;
    }

    public Order processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            logger.warn("Order not found: {}", orderId);
            return null;
        }

        boolean allAvailable = true;
        for (OrderItem item : order.getItems()) {
            boolean available = inventoryService.checkAvailability(
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
                inventoryService.reduceStock(
                        item.getProduct().getId(),
                        1L,
                        item.getQuantity(),
                        item.getLotNumber()
                );
            }
            order.setStatus("CONFIRMED");
            logger.info("Order {} confirmed", orderId);
        } else {
            order.setStatus("BACKORDERED");
            logger.info("Order {} backordered", orderId);
        }

        return orderRepository.save(order);
    }

    public Order shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus("SHIPPED");
            orderRepository.save(order);
            logger.info("Order {} shipped", orderId);
        }
        return order;
    }

    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus("CANCELLED");
            orderRepository.save(order);
            logger.info("Order {} cancelled", orderId);
        }
        return order;
    }
}
