package com.medflow.repository;

import com.medflow.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByProductId(Long productId);

    List<Inventory> findByWarehouseId(Long warehouseId);

    List<Inventory> findByExpiryDateLessThanEqualAndQuantityGreaterThan(Date expiryDate, Integer quantity);

    Optional<Inventory> findByLotNumber(String lotNumber);
}
