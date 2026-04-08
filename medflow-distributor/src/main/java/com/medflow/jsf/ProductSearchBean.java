package com.medflow.jsf;

import com.medflow.ejb.ProductCatalogBean;
import com.medflow.entity.Product;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Named
@ViewScoped
public class ProductSearchBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ProductSearchBean.class.getName());

    @EJB
    private ProductCatalogBean productCatalogBean;

    private String searchTerm;
    private String selectedCategory;
    private List<Product> products;
    private List<String> categories;

    @PostConstruct
    public void init() {
        products = new ArrayList<>();
        categories = new ArrayList<>();
        categories.add("Prescription");
        categories.add("Over-the-Counter");
        categories.add("Medical Devices");
        categories.add("Vaccines");
        categories.add("Supplements");
    }

    public void search() {
        logger.info("Searching with term: " + searchTerm + " and category: " + selectedCategory);
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            products = productCatalogBean.searchByName(searchTerm);
        } else if (selectedCategory != null && !selectedCategory.isEmpty()) {
            products = productCatalogBean.findByCategory(selectedCategory);
        } else {
            products = productCatalogBean.findAll();
        }
    }

    public void clear() {
        searchTerm = null;
        selectedCategory = null;
        products = new ArrayList<>();
        logger.info("Search cleared");
    }

    // Getters and Setters
    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
