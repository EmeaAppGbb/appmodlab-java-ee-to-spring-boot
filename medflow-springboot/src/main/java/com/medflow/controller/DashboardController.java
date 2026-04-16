package com.medflow.controller;

import com.medflow.entity.Order;
import com.medflow.service.InventoryService;
import com.medflow.service.OrderProcessingService;
import com.medflow.service.ProductCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Calendar;
import java.util.List;

@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    private final ProductCatalogService productCatalogService;
    private final OrderProcessingService orderProcessingService;
    private final InventoryService inventoryService;

    public DashboardController(ProductCatalogService productCatalogService,
                               OrderProcessingService orderProcessingService,
                               InventoryService inventoryService) {
        this.productCatalogService = productCatalogService;
        this.orderProcessingService = orderProcessingService;
        this.inventoryService = inventoryService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        logger.info("Loading dashboard");

        int totalProducts = productCatalogService.findAll().size();
        int pendingOrders = orderProcessingService.findByStatus("PENDING").size();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        int expiringItemsCount = inventoryService.findExpiringItems(cal.getTime()).size();

        List<Order> allOrders = orderProcessingService.findAll();
        List<Order> recentOrders = allOrders.size() > 10
                ? allOrders.subList(0, 10)
                : allOrders;

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("expiringItemsCount", expiringItemsCount);
        model.addAttribute("recentOrders", recentOrders);

        logger.info("Dashboard loaded - Total Products: {}, Pending Orders: {}, Expiring Items: {}",
                totalProducts, pendingOrders, expiringItemsCount);

        return "dashboard";
    }
}
