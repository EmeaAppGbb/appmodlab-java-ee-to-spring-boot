package com.medflow.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "inventory")
@NamedQueries({
    @NamedQuery(name = "Inventory.findByProduct", query = "SELECT i FROM Inventory i WHERE i.product.id = :productId"),
    @NamedQuery(name = "Inventory.findByWarehouse", query = "SELECT i FROM Inventory i WHERE i.warehouseId = :warehouseId"),
    @NamedQuery(name = "Inventory.findExpiringItems", query = "SELECT i FROM Inventory i WHERE i.expiryDate <= :expiryDate AND i.quantity > 0"),
    @NamedQuery(name = "Inventory.findByLotNumber", query = "SELECT i FROM Inventory i WHERE i.lotNumber = :lotNumber")
})
public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "lot_number", nullable = false)
    private String lotNumber;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "expiry_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    @Column(name = "received_date")
    @Temporal(TemporalType.DATE)
    private Date receivedDate;

    public Inventory() {
    }

    public Inventory(Product product, Long warehouseId, String lotNumber, Integer quantity, Date expiryDate, Date receivedDate) {
        this.product = product;
        this.warehouseId = warehouseId;
        this.lotNumber = lotNumber;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.receivedDate = receivedDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }
}
