package com.medflow.ejb;

import com.medflow.entity.Product;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ProductCatalogBean {

    @PersistenceContext
    private EntityManager em;

    public Product findById(Long id) {
        return em.find(Product.class, id);
    }

    public Product findBySku(String sku) {
        Query query = em.createNamedQuery("Product.findBySku");
        query.setParameter("sku", sku);
        try {
            return (Product) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Product> findAll() {
        Query query = em.createNamedQuery("Product.findAll");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> findByCategory(String category) {
        Query query = em.createNamedQuery("Product.findByCategory");
        query.setParameter("category", category);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> findByManufacturer(String manufacturer) {
        Query query = em.createNamedQuery("Product.findByManufacturer");
        query.setParameter("manufacturer", manufacturer);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> searchByName(String name) {
        String searchPattern = "%" + name.toLowerCase() + "%";
        Query query = em.createQuery("SELECT p FROM Product p WHERE LOWER(p.name) LIKE :name");
        query.setParameter("name", searchPattern);
        return query.getResultList();
    }

    public Product create(Product product) {
        em.persist(product);
        return product;
    }

    public Product update(Product product) {
        return em.merge(product);
    }

    public void delete(Long id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            em.remove(product);
        }
    }
}
