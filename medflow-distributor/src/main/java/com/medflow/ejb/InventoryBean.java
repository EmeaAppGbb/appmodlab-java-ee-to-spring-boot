package com.medflow.ejb;

import com.medflow.entity.Inventory;
import com.medflow.entity.Product;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class InventoryBean {

    @PersistenceContext
    private EntityManager em;

    public Inventory findById(Long id) {
        return em.find(Inventory.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<Inventory> findByProduct(Long productId) {
        Query query = em.createNamedQuery("Inventory.findByProduct");
        query.setParameter("productId", productId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Inventory> findByWarehouse(Long warehouseId) {
        Query query = em.createNamedQuery("Inventory.findByWarehouse");
        query.setParameter("warehouseId", warehouseId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Inventory> findExpiringItems(Date expiryDate) {
        Query query = em.createNamedQuery("Inventory.findExpiringItems");
        query.setParameter("expiryDate", expiryDate);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Inventory> findByLotNumber(String lotNumber) {
        Query query = em.createNamedQuery("Inventory.findByLotNumber");
        query.setParameter("lotNumber", lotNumber);
        return query.getResultList();
    }

    public boolean checkAvailability(Long productId, Long warehouseId, Integer requiredQuantity) {
        List<Inventory> inventoryList = findByProduct(productId);
        int totalAvailable = inventoryList.stream()
                .filter(i -> i.getWarehouseId().equals(warehouseId))
                .mapToInt(Inventory::getQuantity)
                .sum();
        return totalAvailable >= requiredQuantity;
    }

    public void reduceStock(Long productId, Long warehouseId, Integer quantity, String lotNumber) {
        List<Inventory> inventoryList = findByProduct(productId);
        int remaining = quantity;

        for (Inventory inv : inventoryList) {
            if (remaining <= 0) {
                break;
            }
            if (inv.getWarehouseId().equals(warehouseId) && 
                (lotNumber == null || inv.getLotNumber().equals(lotNumber))) {
                
                int reduction = Math.min(inv.getQuantity(), remaining);
                inv.setQuantity(inv.getQuantity() - reduction);
                em.merge(inv);
                remaining -= reduction;
            }
        }
    }

    public Inventory create(Inventory inventory) {
        em.persist(inventory);
        return inventory;
    }

    public Inventory update(Inventory inventory) {
        return em.merge(inventory);
    }
}
