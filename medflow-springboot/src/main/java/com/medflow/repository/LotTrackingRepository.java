package com.medflow.repository;

import com.medflow.entity.LotTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface LotTrackingRepository extends JpaRepository<LotTracking, Long> {

    List<LotTracking> findByProductId(Long productId);

    List<LotTracking> findByExpiryDateBefore(Date expiryDate);

    Optional<LotTracking> findByLotNumber(String lotNumber);
}
