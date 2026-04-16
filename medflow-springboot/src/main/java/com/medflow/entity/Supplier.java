package com.medflow.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "supplier")
@NamedQueries({
    @NamedQuery(name = "Supplier.findAll", query = "SELECT s FROM Supplier s ORDER BY s.name"),
    @NamedQuery(name = "Supplier.findByRating", query = "SELECT s FROM Supplier s WHERE s.rating >= :rating ORDER BY s.rating DESC")
})
public class Supplier implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "lead_time_days")
    private Integer leadTimeDays;

    @Column(name = "rating")
    private Double rating;

    public Supplier() {
    }

    public Supplier(String name, String contactEmail, Integer leadTimeDays, Double rating) {
        this.name = name;
        this.contactEmail = contactEmail;
        this.leadTimeDays = leadTimeDays;
        this.rating = rating;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
