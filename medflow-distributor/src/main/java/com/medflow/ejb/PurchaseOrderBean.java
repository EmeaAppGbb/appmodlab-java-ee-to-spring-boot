package com.medflow.ejb;

import com.medflow.entity.PurchaseOrder;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PurchaseOrderBean {

    @PersistenceContext
    private EntityManager em;

    public PurchaseOrder findById(Long id) {
        return em.find(PurchaseOrder.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<PurchaseOrder> findAll() {
        Query query = em.createNamedQuery("PurchaseOrder.findAll");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<PurchaseOrder> findBySupplier(Long supplierId) {
        Query query = em.createNamedQuery("PurchaseOrder.findBySupplier");
        query.setParameter("supplierId", supplierId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<PurchaseOrder> findByStatus(String status) {
        Query query = em.createNamedQuery("PurchaseOrder.findByStatus");
        query.setParameter("status", status);
        return query.getResultList();
    }

    public PurchaseOrder create(PurchaseOrder purchaseOrder) {
        em.persist(purchaseOrder);
        return purchaseOrder;
    }

    public PurchaseOrder update(PurchaseOrder purchaseOrder) {
        return em.merge(purchaseOrder);
    }

    public PurchaseOrder approve(Long id) {
        PurchaseOrder po = findById(id);
        if (po != null) {
            po.setStatus("APPROVED");
            em.merge(po);
        }
        return po;
    }

    public PurchaseOrder receive(Long id) {
        PurchaseOrder po = findById(id);
        if (po != null) {
            po.setStatus("RECEIVED");
            em.merge(po);
        }
        return po;
    }
}
