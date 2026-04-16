package com.medflow.repository;

import com.medflow.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findByStatus(String status);

    List<PurchaseOrder> findBySupplierId(Long supplierId);
}
