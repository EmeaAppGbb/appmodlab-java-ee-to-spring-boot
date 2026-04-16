package com.medflow.repository;

import com.medflow.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(String status);

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByPriority(String priority);
}
