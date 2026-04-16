package com.medflow.controller;

import com.medflow.entity.Customer;
import com.medflow.entity.Order;
import com.medflow.entity.OrderItem;
import com.medflow.entity.Product;
import com.medflow.repository.CustomerRepository;
import com.medflow.service.OrderProcessingService;
import com.medflow.service.ProductCatalogService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/order-wizard")
public class OrderWizardController {

    private static final Logger logger = LoggerFactory.getLogger(OrderWizardController.class);
    private static final String SESSION_ORDER = "wizardOrder";
    private static final String SESSION_STEP = "wizardStep";

    private final OrderProcessingService orderProcessingService;
    private final ProductCatalogService productCatalogService;
    private final CustomerRepository customerRepository;

    public OrderWizardController(OrderProcessingService orderProcessingService,
                                 ProductCatalogService productCatalogService,
                                 CustomerRepository customerRepository) {
        this.orderProcessingService = orderProcessingService;
        this.productCatalogService = productCatalogService;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public String showWizard(HttpSession session, Model model) {
        Order order = getOrCreateOrder(session);
        int step = getStep(session);

        populateModel(model, order, step);
        logger.info("Showing order wizard step {}", step);
        return "order-wizard";
    }

    @PostMapping("/start")
    public String startOrder(HttpSession session) {
        session.setAttribute(SESSION_ORDER, new Order());
        session.setAttribute(SESSION_STEP, 1);
        logger.info("Order wizard started");
        return "redirect:/order-wizard";
    }

    @PostMapping("/select-customer")
    public String selectCustomer(@RequestParam Long customerId, HttpSession session) {
        Order order = getOrCreateOrder(session);
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            order.setCustomer(customer);
            logger.info("Customer selected: {}", customer.getName());
        }
        session.setAttribute(SESSION_ORDER, order);
        session.setAttribute(SESSION_STEP, 2);
        return "redirect:/order-wizard";
    }

    @PostMapping("/add-item")
    public String addItem(@RequestParam Long productId,
                          @RequestParam Integer quantity,
                          @RequestParam(required = false) String lotNumber,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        Order order = getOrCreateOrder(session);

        if (productId == null || quantity == null || quantity <= 0) {
            redirectAttributes.addFlashAttribute("error", "Invalid product or quantity");
            return "redirect:/order-wizard";
        }

        Product product = productCatalogService.findById(productId).orElse(null);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Product not found");
            return "redirect:/order-wizard";
        }

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setLotNumber(lotNumber);
        item.setQuantity(quantity);
        item.setUnitPrice(product.getUnitPrice());
        order.addItem(item);

        session.setAttribute(SESSION_ORDER, order);
        logger.info("Item added to order: product={}, qty={}", product.getName(), quantity);
        return "redirect:/order-wizard";
    }

    @PostMapping("/remove-item")
    public String removeItem(@RequestParam int itemIndex, HttpSession session) {
        Order order = getOrCreateOrder(session);
        if (itemIndex >= 0 && itemIndex < order.getItems().size()) {
            order.getItems().remove(itemIndex);
            order.recalculateTotal();
            logger.info("Item removed from order at index {}", itemIndex);
        }
        session.setAttribute(SESSION_ORDER, order);
        return "redirect:/order-wizard";
    }

    @PostMapping("/set-details")
    public String setOrderDetails(@RequestParam(required = false) String shippingAddress,
                                  @RequestParam(required = false) String priority,
                                  HttpSession session) {
        Order order = getOrCreateOrder(session);
        order.setShippingAddress(shippingAddress);
        order.setPriority(priority);
        session.setAttribute(SESSION_ORDER, order);
        session.setAttribute(SESSION_STEP, 3);
        logger.info("Order details set: address={}, priority={}", shippingAddress, priority);
        return "redirect:/order-wizard";
    }

    @PostMapping("/next")
    public String nextStep(HttpSession session) {
        int step = getStep(session);
        if (step < 5) {
            session.setAttribute(SESSION_STEP, step + 1);
        }
        return "redirect:/order-wizard";
    }

    @PostMapping("/previous")
    public String previousStep(HttpSession session) {
        int step = getStep(session);
        if (step > 1) {
            session.setAttribute(SESSION_STEP, step - 1);
        }
        return "redirect:/order-wizard";
    }

    @PostMapping("/submit")
    public String submitOrder(HttpSession session, RedirectAttributes redirectAttributes) {
        Order order = getOrCreateOrder(session);
        if (order.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cannot submit empty order");
            return "redirect:/order-wizard";
        }

        try {
            Order created = orderProcessingService.createOrder(order);
            session.removeAttribute(SESSION_ORDER);
            session.removeAttribute(SESSION_STEP);
            redirectAttributes.addFlashAttribute("success", "Order submitted: " + created.getId());
            logger.info("Order submitted: {}", created.getId());
            return "redirect:/dashboard";
        } catch (Exception e) {
            logger.error("Error submitting order: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error submitting order: " + e.getMessage());
            return "redirect:/order-wizard";
        }
    }

    private Order getOrCreateOrder(HttpSession session) {
        Order order = (Order) session.getAttribute(SESSION_ORDER);
        if (order == null) {
            order = new Order();
            session.setAttribute(SESSION_ORDER, order);
        }
        return order;
    }

    private int getStep(HttpSession session) {
        Integer step = (Integer) session.getAttribute(SESSION_STEP);
        return step != null ? step : 1;
    }

    private void populateModel(Model model, Order order, int step) {
        model.addAttribute("order", order);
        model.addAttribute("step", step);
        model.addAttribute("orderTotal", order.getTotalAmount() != null
                ? order.getTotalAmount() : java.math.BigDecimal.ZERO);
        model.addAttribute("availableProducts", productCatalogService.findAll());
        model.addAttribute("customers", customerRepository.findAll());
    }
}
