package com.medflow.service;

import com.medflow.entity.PurchaseOrder;
import com.medflow.repository.PurchaseOrderRepository;
import com.medflow.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseOrderService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                                SupplierRepository supplierRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public Optional<PurchaseOrder> findById(Long id) {
        logger.debug("Finding purchase order by id: {}", id);
        return purchaseOrderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrder> findAll() {
        logger.debug("Finding all purchase orders");
        return purchaseOrderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrder> findBySupplier(Long supplierId) {
        logger.debug("Finding purchase orders by supplier: {}", supplierId);
        return purchaseOrderRepository.findBySupplierId(supplierId);
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrder> findByStatus(String status) {
        logger.debug("Finding purchase orders by status: {}", status);
        return purchaseOrderRepository.findByStatus(status);
    }

    @Transactional
    public PurchaseOrder create(PurchaseOrder purchaseOrder) {
        logger.info("Creating purchase order");
        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public PurchaseOrder update(PurchaseOrder purchaseOrder) {
        logger.info("Updating purchase order: {}", purchaseOrder.getId());
        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public PurchaseOrder approve(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElse(null);
        if (po != null) {
            po.setStatus("APPROVED");
            purchaseOrderRepository.save(po);
            logger.info("Purchase order {} approved", id);
        }
        return po;
    }

    @Transactional
    public PurchaseOrder receive(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElse(null);
        if (po != null) {
            po.setStatus("RECEIVED");
            purchaseOrderRepository.save(po);
            logger.info("Purchase order {} received", id);
        }
        return po;
    }
}
