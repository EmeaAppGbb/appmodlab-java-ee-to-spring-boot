package com.medflow.controller;

import com.medflow.entity.Product;
import com.medflow.service.CacheManagerService;
import com.medflow.service.ProductCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    private static final Logger logger = LoggerFactory.getLogger(ProductRestController.class);

    private final ProductCatalogService productCatalogService;
    private final CacheManagerService cacheManagerService;

    public ProductRestController(ProductCatalogService productCatalogService,
                                 CacheManagerService cacheManagerService) {
        this.productCatalogService = productCatalogService;
        this.cacheManagerService = cacheManagerService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productCatalogService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return cacheManagerService.getProductById(id)
                .or(() -> productCatalogService.findById(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Product> getBySku(@PathVariable String sku) {
        return cacheManagerService.getProductBySku(sku)
                .or(() -> productCatalogService.findBySku(sku))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productCatalogService.findByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(required = false) String name) {
        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body("name parameter required");
        }
        return ResponseEntity.ok(productCatalogService.searchByName(name));
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product created = productCatalogService.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return productCatalogService.findById(id)
                .map(existing -> {
                    product.setId(id);
                    Product updated = productCatalogService.update(product);
                    cacheManagerService.evict(id);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return productCatalogService.findById(id)
                .map(existing -> {
                    productCatalogService.delete(id);
                    cacheManagerService.evict(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
