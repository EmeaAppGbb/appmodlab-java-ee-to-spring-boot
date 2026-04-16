package com.medflow.repository;

import com.medflow.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findByRatingGreaterThanEqual(Double rating);

    List<Supplier> findByContactEmail(String contactEmail);
}
