package com.ecommerce;

import java.math.BigDecimal;
import java.util.*;

/**
 * Represents a customer's shopping cart with product lines and quantities.
 */
public class ShoppingCart {

    /**
     * Cart line (product + quantity).
     */
    public static class Line {
        private final Product product;
        private int quantity;

        Line(Product product, int quantity) {
            if (product == null) throw new IllegalArgumentException("Product cannot be null.");
            if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0.");
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() { return product; }

        public int getQuantity() { return quantity; }

        void increase(int delta) {
            if (delta <= 0) throw new IllegalArgumentException("Increase delta must be > 0.");
            this.quantity += delta;
        }

        void decrease(int delta) {
            if (delta <= 0) throw new IllegalArgumentException("Decrease delta must be > 0.");
            this.quantity -= delta;
        }

        @Override
        public String toString() {
            return product.getName() + " x " + quantity + " @ " + product.getPrice();
        }
    }

    private final Map<String, Line> linesByProductId = new LinkedHashMap<>();

    public synchronized void add(Product product, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0.");
        Line line = linesByProductId.get(product.getProductID());
        if (line == null) {
            linesByProductId.put(product.getProductID(), new Line(product, quantity));
        } else {
            line.increase(quantity);
        }
    }

    public synchronized void remove(Product product, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0.");
        Line line = linesByProductId.get(product.getProductID());
        if (line == null) {
            throw new NoSuchElementException("Product not found in cart: " + product.getProductID());
        }
        line.decrease(quantity);
        if (line.getQuantity() <= 0) {
            linesByProductId.remove(product.getProductID());
        }
    }

    public synchronized void clear() {
        linesByProductId.clear();
    }

    public synchronized List<Line> getLines() {
        return Collections.unmodifiableList(new ArrayList<>(linesByProductId.values()));
    }

    public synchronized boolean isEmpty() {
        return linesByProductId.isEmpty();
    }

    public synchronized BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        for (Line line : linesByProductId.values()) {
            BigDecimal lineTotal = line.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(line.getQuantity()))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            total = total.add(lineTotal);
        }
        return total;
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder("ShoppingCart{\n");
        for (Line line : linesByProductId.values()) {
            sb.append("  ").append(line).append('\n');
        }
        sb.append("  Total: ").append(getTotal()).append("\n}");
        return sb.toString();
    }
}