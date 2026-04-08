package com.medflow.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@NamedQueries({
    @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p ORDER BY p.name"),
    @NamedQuery(name = "Product.findBySku", query = "SELECT p FROM Product p WHERE p.sku = :sku"),
    @NamedQuery(name = "Product.findByCategory", query = "SELECT p FROM Product p WHERE p.category = :category ORDER BY p.name"),
    @NamedQuery(name = "Product.findByManufacturer", query = "SELECT p FROM Product p WHERE p.manufacturer = :manufacturer ORDER BY p.name"),
    @NamedQuery(name = "Product.searchByName", query = "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:searchTerm) ORDER BY p.name")
})
public class Product implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String sku;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 100)
    private String manufacturer;
    
    @Column(length = 50)
    private String category;
    
    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "requires_cold_chain")
    private Boolean requiresColdChain = false;
    
    @Column(name = "dea_schedule", length = 10)
    private String deaSchedule;
    
    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Boolean getRequiresColdChain() {
        return requiresColdChain;
    }

    public void setRequiresColdChain(Boolean requiresColdChain) {
        this.requiresColdChain = requiresColdChain;
    }

    public String getDeaSchedule() {
        return deaSchedule;
    }

    public void setDeaSchedule(String deaSchedule) {
        this.deaSchedule = deaSchedule;
    }
}
