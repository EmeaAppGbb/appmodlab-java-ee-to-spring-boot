package com.medflow.jsf;

import com.medflow.ejb.ProductCatalogBean;
import com.medflow.ejb.OrderProcessingBean;
import com.medflow.ejb.InventoryBean;
import com.medflow.entity.Order;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(DashboardBean.class.getName());

    @EJB
    private ProductCatalogBean productCatalogBean;

    @EJB
    private OrderProcessingBean orderProcessingBean;

    @EJB
    private InventoryBean inventoryBean;

    private int totalProducts;
    private int pendingOrders;
    private int expiringItemsCount;
    private List<Order> recentOrders;

    @PostConstruct
    public void init() {
        logger.info("Initializing dashboard");
        
        // Calculate total products
        totalProducts = productCatalogBean.findAll().size();
        
        // Calculate pending orders
        pendingOrders = orderProcessingBean.findByStatus("PENDING").size();
        
        // Calculate expiring items (within 30 days)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        expiringItemsCount = inventoryBean.findExpiringItems(cal.getTime()).size();
        
        // Get recent orders (top 10)
        List<Order> allOrders = orderProcessingBean.findAll();
        recentOrders = allOrders.size() > 10 ? 
                       allOrders.subList(0, 10) : 
                       allOrders;
        
        logger.info("Dashboard initialized - Total Products: " + totalProducts + 
                   ", Pending Orders: " + pendingOrders + 
                   ", Expiring Items: " + expiringItemsCount);
    }

    // Getters
    public int getTotalProducts() {
        return totalProducts;
    }

    public int getPendingOrders() {
        return pendingOrders;
    }

    public int getExpiringItemsCount() {
        return expiringItemsCount;
    }

    public List<Order> getRecentOrders() {
        return recentOrders;
    }
}
