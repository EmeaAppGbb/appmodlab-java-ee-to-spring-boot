package com.medflow.ejb;

import com.medflow.entity.Product;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
@Startup
public class CacheManagerBean {

    private static final Logger logger = Logger.getLogger(CacheManagerBean.class.getName());

    @EJB
    private ProductCatalogBean productCatalogBean;

    private Map<Long, Product> productCache;
    private Map<String, Product> skuCache;

    @PostConstruct
    public void refreshCache() {
        logger.info("Initializing product caches");
        productCache = new HashMap<>();
        skuCache = new HashMap<>();

        List<Product> allProducts = productCatalogBean.findAll();
        for (Product product : allProducts) {
            if (product.getId() != null) {
                productCache.put(product.getId(), product);
            }
            if (product.getSku() != null) {
                skuCache.put(product.getSku(), product);
            }
        }
        logger.info("Cache initialized with " + productCache.size() + " products");
    }

    public Product getProductById(Long id) {
        return productCache.get(id);
    }

    public Product getProductBySku(String sku) {
        return skuCache.get(sku);
    }

    public void evict(Long productId) {
        Product product = productCache.remove(productId);
        if (product != null && product.getSku() != null) {
            skuCache.remove(product.getSku());
        }
        logger.info("Evicted product " + productId + " from cache");
    }

    public void evictBySku(String sku) {
        Product product = skuCache.remove(sku);
        if (product != null && product.getId() != null) {
            productCache.remove(product.getId());
        }
        logger.info("Evicted product with SKU " + sku + " from cache");
    }

    public int getCacheSize() {
        return productCache.size();
    }

    public void clear() {
        productCache.clear();
        skuCache.clear();
        logger.info("Cache cleared");
    }
}
