package com.medflow.jsf;

import com.medflow.ejb.OrderProcessingBean;
import com.medflow.ejb.ProductCatalogBean;
import com.medflow.entity.Order;
import com.medflow.entity.OrderItem;
import com.medflow.entity.Product;
import javax.ejb.EJB;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;

@Named
@ConversationScoped
public class OrderWizardBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(OrderWizardBean.class.getName());

    @Inject
    private javax.enterprise.context.Conversation conversation;

    @EJB
    private OrderProcessingBean orderProcessingBean;

    @EJB
    private ProductCatalogBean productCatalogBean;

    private Order order;
    private List<Product> availableProducts;
    private Long selectedProductId;
    private Integer itemQuantity;
    private String lotNumber;
    private int step = 1;

    public void startOrder() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
        order = new Order();
        availableProducts = productCatalogBean.findAll();
        step = 1;
        logger.info("Order wizard started");
    }

    public void nextStep() {
        if (step < 5) {
            step++;
            logger.info("Moving to step: " + step);
        }
    }

    public void previousStep() {
        if (step > 1) {
            step--;
            logger.info("Moving to step: " + step);
        }
    }

    public void addItem() {
        if (selectedProductId == null || itemQuantity == null || itemQuantity <= 0) {
            logger.warning("Invalid product selection or quantity");
            return;
        }

        Product product = productCatalogBean.findById(selectedProductId);
        if (product == null) {
            logger.warning("Product not found: " + selectedProductId);
            return;
        }

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setLotNumber(lotNumber);
        item.setQuantity(itemQuantity);
        item.setUnitPrice(product.getUnitPrice());

        order.addItem(item);
        
        selectedProductId = null;
        itemQuantity = null;
        lotNumber = null;
        
        logger.info("Item added to order");
    }

    public void removeItem(OrderItem item) {
        order.getItems().remove(item);
        order.recalculateTotal();
        logger.info("Item removed from order");
    }

    public void submitOrder() {
        if (order.getItems().isEmpty()) {
            logger.warning("Cannot submit empty order");
            return;
        }
        try {
            orderProcessingBean.createOrder(order);
            conversation.end();
            logger.info("Order submitted: " + order.getId());
        } catch (Exception e) {
            logger.severe("Error submitting order: " + e.getMessage());
        }
    }

    public BigDecimal getOrderTotal() {
        return order != null ? order.getTotalAmount() : BigDecimal.ZERO;
    }

    // Getters and Setters
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<Product> getAvailableProducts() {
        return availableProducts;
    }

    public void setAvailableProducts(List<Product> availableProducts) {
        this.availableProducts = availableProducts;
    }

    public Long getSelectedProductId() {
        return selectedProductId;
    }

    public void setSelectedProductId(Long selectedProductId) {
        this.selectedProductId = selectedProductId;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
