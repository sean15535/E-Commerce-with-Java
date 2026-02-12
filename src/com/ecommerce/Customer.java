package com.ecommerce;

import com.ecommerce.orders.Order;
import com.ecommerce.orders.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a customer with an attached shopping cart.
 */
public class Customer {
    private final String customerID;
    private String name;
    private final ShoppingCart cart = new ShoppingCart();

    public Customer(String customerID, String name) {
        if (customerID == null || customerID.isBlank()) {
            throw new IllegalArgumentException("customerID must not be null/blank.");
        }
        setName(name);
        this.customerID = customerID;
    }

    public static Customer withGeneratedId(String name) {
        return new Customer("C-" + UUID.randomUUID(), name);
    }

    public String getCustomerID() { return customerID; }

    public String getName() { return name; }

    public final void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name must not be null/blank.");
        }
        this.name = name;
    }

    public ShoppingCart getCart() { return cart; }

    // Cart operations
    public void addToCart(Product product, int quantity) {
        cart.add(product, quantity);
    }

    public void removeFromCart(Product product, int quantity) {
        cart.remove(product, quantity);
    }

    public BigDecimal getCartTotal() {
        return cart.getTotal();
    }

    /**
     * Places an order from the current cart. Clears the cart upon success.
     */
    public Order placeOrder() {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cannot place order: cart is empty.");
        }

        // Convert cart lines to order lines
        List<Order.OrderLine> orderLines = new ArrayList<>();
        for (ShoppingCart.Line line : cart.getLines()) {
            orderLines.add(new Order.OrderLine(line.getProduct(), line.getQuantity()));
        }

        Order order = Order.createFor(this, orderLines);
        order.setStatus(OrderStatus.NEW); // explicit, though default is NEW
        cart.clear(); // Empty cart after successful order
        return order;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerID='" + customerID + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}