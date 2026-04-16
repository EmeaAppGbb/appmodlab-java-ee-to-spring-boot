package com.medflow.controller;

import com.medflow.entity.Product;
import com.medflow.service.ProductCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class ProductSearchController {

    private static final Logger logger = LoggerFactory.getLogger(ProductSearchController.class);

    private static final List<String> CATEGORIES = Arrays.asList(
            "Prescription", "Over-the-Counter", "Medical Devices", "Vaccines", "Supplements");

    private final ProductCatalogService productCatalogService;

    public ProductSearchController(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    @GetMapping("/product-search")
    public String showSearchForm(Model model) {
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("products", List.of());
        return "product-search";
    }

    @PostMapping("/product-search")
    public String search(@RequestParam(required = false) String searchTerm,
                         @RequestParam(required = false) String selectedCategory,
                         Model model) {
        logger.info("Searching with term: {} and category: {}", searchTerm, selectedCategory);

        List<Product> products;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            products = productCatalogService.searchByName(searchTerm);
        } else if (selectedCategory != null && !selectedCategory.isEmpty()) {
            products = productCatalogService.findByCategory(selectedCategory);
        } else {
            products = productCatalogService.findAll();
        }

        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("products", products);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("selectedCategory", selectedCategory);

        return "product-search";
    }
}
