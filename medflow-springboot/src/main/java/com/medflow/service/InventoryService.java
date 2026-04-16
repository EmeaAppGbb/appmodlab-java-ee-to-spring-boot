package com.medflow.service;

import com.medflow.entity.Inventory;
import com.medflow.repository.InventoryRepository;
import com.medflow.repository.LotTrackingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;
    private final LotTrackingRepository lotTrackingRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            LotTrackingRepository lotTrackingRepository) {
        this.inventoryRepository = inventoryRepository;
        this.lotTrackingRepository = lotTrackingRepository;
    }

    public Optional<Inventory> findById(Long id) {
        logger.debug("Finding inventory by id: {}", id);
        return inventoryRepository.findById(id);
    }

    public List<Inventory> findByProduct(Long productId) {
        logger.debug("Finding inventory by product: {}", productId);
        return inventoryRepository.findByProductId(productId);
    }

    public List<Inventory> findByWarehouse(Long warehouseId) {
        logger.debug("Finding inventory by warehouse: {}", warehouseId);
        return inventoryRepository.findByWarehouseId(warehouseId);
    }

    public List<Inventory> findExpiringItems(Date expiryDate) {
        logger.debug("Finding expiring items before: {}", expiryDate);
        return inventoryRepository.findByExpiryDateLessThanEqualAndQuantityGreaterThan(expiryDate, 0);
    }

    public Optional<Inventory> findByLotNumber(String lotNumber) {
        logger.debug("Finding inventory by lot number: {}", lotNumber);
        return inventoryRepository.findByLotNumber(lotNumber);
    }

    public boolean checkAvailability(Long productId, Long warehouseId, Integer requiredQuantity) {
        List<Inventory> inventoryList = inventoryRepository.findByProductId(productId);
        int totalAvailable = inventoryList.stream()
                .filter(i -> i.getWarehouseId().equals(warehouseId))
                .mapToInt(Inventory::getQuantity)
                .sum();
        logger.debug("Availability check for product {} in warehouse {}: {} available, {} required",
                productId, warehouseId, totalAvailable, requiredQuantity);
        return totalAvailable >= requiredQuantity;
    }

    @Transactional
    public void reduceStock(Long productId, Long warehouseId, Integer quantity, String lotNumber) {
        logger.info("Reducing stock for product {} in warehouse {} by {}", productId, warehouseId, quantity);
        List<Inventory> inventoryList = inventoryRepository.findByProductId(productId);
        int remaining = quantity;

        for (Inventory inv : inventoryList) {
            if (remaining <= 0) {
                break;
            }
            if (inv.getWarehouseId().equals(warehouseId)
                    && (lotNumber == null || lotNumber.equals(inv.getLotNumber()))) {
                int reduction = Math.min(inv.getQuantity(), remaining);
                inv.setQuantity(inv.getQuantity() - reduction);
                inventoryRepository.save(inv);
                remaining -= reduction;
            }
        }
    }

    @Transactional
    public Inventory create(Inventory inventory) {
        logger.info("Creating inventory record for product: {}", inventory.getProduct().getId());
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory update(Inventory inventory) {
        logger.info("Updating inventory record: {}", inventory.getId());
        return inventoryRepository.save(inventory);
    }
}
