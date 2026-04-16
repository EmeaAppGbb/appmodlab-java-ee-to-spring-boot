package com.medflow.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "lot_tracking")
@NamedQueries({
    @NamedQuery(name = "LotTracking.findByProduct", query = "SELECT l FROM LotTracking l WHERE l.product.id = :productId"),
    @NamedQuery(name = "LotTracking.findByLotNumber", query = "SELECT l FROM LotTracking l WHERE l.lotNumber = :lotNumber"),
    @NamedQuery(name = "LotTracking.findExpiring", query = "SELECT l FROM LotTracking l WHERE l.expiryDate <= :expiryDate AND l.quantityDistributed < l.quantityReceived")
})
public class LotTracking implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "lot_number", nullable = false)
    private String lotNumber;

    @Column(name = "manufacturer_date")
    @Temporal(TemporalType.DATE)
    private Date manufacturerDate;

    @Column(name = "expiry_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    @Column(name = "quantity_received", nullable = false)
    private Integer quantityReceived;

    @Column(name = "quantity_distributed", nullable = false)
    private Integer quantityDistributed;

    public LotTracking() {
    }

    public LotTracking(Product product, String lotNumber, Date manufacturerDate, Date expiryDate, Integer quantityReceived) {
        this.product = product;
        this.lotNumber = lotNumber;
        this.manufacturerDate = manufacturerDate;
        this.expiryDate = expiryDate;
        this.quantityReceived = quantityReceived;
        this.quantityDistributed = 0;
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

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public Date getManufacturerDate() {
        return manufacturerDate;
    }

    public void setManufacturerDate(Date manufacturerDate) {
        this.manufacturerDate = manufacturerDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Integer quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public Integer getQuantityDistributed() {
        return quantityDistributed;
    }

    public void setQuantityDistributed(Integer quantityDistributed) {
        this.quantityDistributed = quantityDistributed;
    }
}
