package com.medflow.service;

import com.medflow.entity.Product;
import com.medflow.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CacheManagerService {

    private static final Logger logger = LoggerFactory.getLogger(CacheManagerService.class);

    private final ProductRepository productRepository;

    public CacheManagerService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(value = "products", key = "#id")
    public Optional<Product> getProductById(Long id) {
        logger.debug("Cache miss for product id: {}", id);
        return productRepository.findById(id);
    }

    @Cacheable(value = "productsBySku", key = "#sku")
    public Optional<Product> getProductBySku(String sku) {
        logger.debug("Cache miss for product SKU: {}", sku);
        return productRepository.findBySku(sku);
    }

    @CacheEvict(value = "products", key = "#productId")
    public void evict(Long productId) {
        logger.info("Evicted product {} from cache", productId);
    }

    @CacheEvict(value = "productsBySku", key = "#sku")
    public void evictBySku(String sku) {
        logger.info("Evicted product with SKU {} from cache", sku);
    }

    @CacheEvict(value = {"products", "productsBySku"}, allEntries = true)
    public void clear() {
        logger.info("All product caches cleared");
    }
}
