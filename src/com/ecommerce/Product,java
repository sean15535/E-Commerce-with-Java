package com.ecommerce;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a purchasable product.
 */
public class Product {
    private final String productID;
    private String name;
    private BigDecimal price; // Use BigDecimal for currency

    public Product(String productID, String name, BigDecimal price) {
        if (productID == null || productID.isBlank()) {
            throw new IllegalArgumentException("productID must not be null/blank.");
        }
        setName(name);
        setPrice(price);
        this.productID = productID;
    }

    public String getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be null/blank.");
        }
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public final void setPrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Price must be non-null and >= 0.");
        }
        // Normalize to 2 decimal places for currency
        this.price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return productID.equals(product.productID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productID);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productID='" + productID + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}