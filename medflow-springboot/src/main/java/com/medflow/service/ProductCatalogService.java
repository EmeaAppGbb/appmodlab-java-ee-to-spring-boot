package com.medflow.service;

import com.medflow.entity.Product;
import com.medflow.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCatalogService {

    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogService.class);

    private final ProductRepository productRepository;

    public ProductCatalogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> findById(Long id) {
        logger.debug("Finding product by id: {}", id);
        return productRepository.findById(id);
    }

    public Optional<Product> findBySku(String sku) {
        logger.debug("Finding product by SKU: {}", sku);
        return productRepository.findBySku(sku);
    }

    public List<Product> findAll() {
        logger.debug("Finding all products");
        return productRepository.findAll();
    }

    public List<Product> findByCategory(String category) {
        logger.debug("Finding products by category: {}", category);
        return productRepository.findByCategory(category);
    }

    public List<Product> findByManufacturer(String manufacturer) {
        logger.debug("Finding products by manufacturer: {}", manufacturer);
        return productRepository.findByManufacturer(manufacturer);
    }

    public List<Product> searchByName(String name) {
        logger.debug("Searching products by name: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Product create(Product product) {
        logger.info("Creating product: {}", product.getSku());
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Product product) {
        logger.info("Updating product: {}", product.getId());
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Deleting product: {}", id);
        productRepository.findById(id).ifPresent(productRepository::delete);
    }
}
