package com.ecommerce.orders;

import com.ecommerce.Customer;
import com.ecommerce.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents an order placed by a customer.
 */
public class Order {

    /**
     * Immutable order line.
     */
    public static class OrderLine {
        private final Product product;
        private final int quantity;

        public OrderLine(Product product, int quantity) {
            if (product == null) throw new IllegalArgumentException("Product cannot be null.");
            if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0.");
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() { return product; }

        public int getQuantity() { return quantity; }

        public BigDecimal getLineTotal() {
            return product.getPrice()
                    .multiply(BigDecimal.valueOf(quantity))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        @Override
        public String toString() {
            return product.getName() + " x " + quantity + " = " + getLineTotal();
        }
    }

    private final String orderID;
    private final Customer customer;
    private final List<OrderLine> lines;
    private final LocalDateTime createdAt;
    private BigDecimal total;
    private OrderStatus status;

    private Order(String orderID, Customer customer, List<OrderLine> lines) {
        if (orderID == null || orderID.isBlank()) {
            throw new IllegalArgumentException("orderID must not be null/blank.");
        }
        if (customer == null) throw new IllegalArgumentException("customer cannot be null.");
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one line.");
        }
        this.orderID = orderID;
        this.customer = customer;
        this.lines = Collections.unmodifiableList(new ArrayList<>(lines));
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.NEW;
        this.total = computeTotal();
    }

    public static Order createFor(Customer customer, List<OrderLine> lines) {
        String id = "O-" + UUID.randomUUID();
        return new Order(id, customer, lines);
    }

    private BigDecimal computeTotal() {
        BigDecimal t = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        for (OrderLine line : lines) {
            t = t.add(line.getLineTotal());
        }
        return t;
    }

    public String getOrderID() { return orderID; }

    public Customer getCustomer() { return customer; }

    public List<OrderLine> getLines() { return lines; }

    public BigDecimal getTotal() { return total; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public OrderStatus getStatus() { return status; }

    public void setStatus(OrderStatus newStatus) {
        if (newStatus == null) throw new IllegalArgumentException("Status cannot be null.");
        // (Simple rule set; could be expanded to enforce transitions)
        this.status = newStatus;
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order Summary\n");
        sb.append("-------------\n");
        sb.append("Order ID: ").append(orderID).append('\n');
        sb.append("Customer: ").append(customer.getName())
          .append(" (").append(customer.getCustomerID()).append(")\n");
        sb.append("Created: ").append(createdAt).append('\n');
        sb.append("Status : ").append(status).append('\n');
        sb.append("\nItems:\n");
        for (OrderLine line : lines) {
            sb.append("  - ").append(line).append('\n');
        }
        sb.append("\nTotal: ").append(total).append('\n');
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID='" + orderID + '\'' +
                ", customer=" + customer +
                ", total=" + total +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}