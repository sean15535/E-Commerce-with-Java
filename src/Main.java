import com.ecommerce.Customer;
import com.ecommerce.Product;
import com.ecommerce.ShoppingCart;
import com.ecommerce.orders.Order;
import com.ecommerce.orders.OrderStatus;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        try {
            // Create products (browseable inventory)
            Product laptop = new Product("P-1001", "Ultrabook Laptop", new BigDecimal("350000.00"));
            Product mouse = new Product("P-1002", "Wireless Mouse", new BigDecimal("8500.00"));
            Product headset = new Product("P-1003", "Bluetooth Headset", new BigDecimal("22000.00"));

            System.out.println("Available Products:");
            System.out.println(" - " + laptop);
            System.out.println(" - " + mouse);
            System.out.println(" - " + headset);
            System.out.println();

            // Create a customer (using your name for the demo 😊)
            Customer customer = new Customer("C-0001", "Oluwaseun Alli");

            // Add items to cart
            customer.addToCart(laptop, 1);
            customer.addToCart(mouse, 2);
            customer.addToCart(headset, 1);

            // Show cart details and total
            ShoppingCart cart = customer.getCart();
            System.out.println("Cart Contents:");
            System.out.println(cart);
            System.out.println();

            // Place order
            Order order = customer.placeOrder();
            System.out.println(order.getSummary());

            // Update order status to PAID
            order.setStatus(OrderStatus.PAID);
            System.out.println("Updated Status: " + order.getStatus());

            // Update order status to SHIPPED
            order.setStatus(OrderStatus.SHIPPED);
            System.out.println("Updated Status: " + order.getStatus());

        } catch (Exception ex) {
            // Basic error handling for demo; in real apps, use finer-grained exceptions/logging
            System.err.println("An error occurred: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
``