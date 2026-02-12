import com.ecommerce.Customer;
import com.ecommerce.Product;
import com.ecommerce.ShoppingCart;
import com.ecommerce.orders.Order;
import com.ecommerce.orders.OrderStatus;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

public class Main {

    private static final Scanner SC = new Scanner(System.in);
    private static final Locale LOCALE_NG = new Locale("en", "NG"); // Format money nicely
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(LOCALE_NG);

    public static void main(String[] args) {
        // Seed products (your store catalog)
        List<Product> catalog = List.of(
                new Product("P-1001", "Ultrabook Laptop", new BigDecimal("350000.00")),
                new Product("P-1002", "Wireless Mouse", new BigDecimal("8500.00")),
                new Product("P-1003", "Bluetooth Headset", new BigDecimal("22000.00")),
                new Product("P-1004", "USB-C Dock", new BigDecimal("60000.00"))
        );

        printWelcome();

        // Create a customer (ask for name nicely)
        Customer customer = askForCustomer();

        // Main loop: guide the user with a clear menu
        boolean running = true;
        while (running) {
            printDivider();
            printMenu();
            int choice = readInt("Your choice (1-6): ");
            printDivider();

            try {
                switch (choice) {
                    case 1 -> listProducts(catalog);
                    case 2 -> addToCartFlow(customer, catalog);
                    case 3 -> removeFromCartFlow(customer, catalog);
                    case 4 -> viewCart(customer);
                    case 5 -> placeOrderFlow(customer);
                    case 6 -> {
                        sayGoodbye(customer);
                        running = false;
                    }
                    default -> say("Hmm, I didn’t recognize that option. Please choose 1–6.");
                }
            } catch (Exception ex) {
                
                
                say("Oops! " + ex.getMessage());
            }
        }
    }

    // ---------------------------
    // Greetings
    // ---------------------------

    private static void printWelcome() {
        printDivider();
        say("Welcome to Simple E‑Commerce 👋");
        say("You can browse products, add to cart, and place an order.");
        say("Let’s get started!");
        printDivider();
    }

    private static void sayGoodbye(Customer customer) {
        say("Thanks for shopping with us, " + customer.getName() + "!");
        say("Have a great day ✨");
    }

    private static void printMenu() {
        say("Main Menu");
        say("  1) Browse products");
        say("  2) Add a product to your cart");
        say("  3) Remove a product from your cart");
        say("  4) View your cart");
        say("  5) Place your order");
        say("  6) Exit");
        sayHint("Tip: Start with 1 to see product IDs, then try 2 to add items.");
    }

    private static void printDivider() {
        System.out.println("------------------------------------------------------------");
    }

    private static void say(String text) {
        System.out.println(text);
    }

    private static void sayHint(String text) {
        System.out.println("   • " + text);
    }

    // ---------------------------
    // Qustions flows (add to cart, place order, etc.)
    // ---------------------------

    private static Customer askForCustomer() {
        say("What’s your name?");
        System.out.print("> ");
        String name = SC.nextLine().trim();
        if (name.isEmpty()) name = "Guest";
        say("Hi " + name + "! 👋");
        
        // Create a customer with a generated ID
        return new Customer("C-0001", name);
    }

    private static void listProducts(List<Product> catalog) {
        say("Here’s what’s in stock:");
        for (Product p : catalog) {
            System.out.printf("  %s  %-22s  @ %s%n",
                    p.getProductID(),
                    p.getName(),
                    CURRENCY.format(p.getPrice()));
        }
        sayHint("Use the Product ID (e.g., P-1002) when adding/removing items.");
    }

    private static void addToCartFlow(Customer customer, List<Product> catalog) {
        listProducts(catalog);
        String id = readString("Enter the Product ID to add: ").toUpperCase(Locale.ROOT);

        Product p = findById(catalog, id);
        if (p == null) {
            say("I couldn’t find a product with ID " + id + ". Try option 1 to view products again.");
            return;
        }

        int qty = readPositiveInt("How many would you like to add? ");
        customer.addToCart(p, qty);
        say("✅ Added: " + p.getName() + " × " + qty + " (each " + CURRENCY.format(p.getPrice()) + ")");
        say("Cart total is now: " + CURRENCY.format(customer.getCartTotal()));
    }

    private static void removeFromCartFlow(Customer customer, List<Product> catalog) {
        if (customer.getCart().isEmpty()) {
            say("Your cart is currently empty.");
            sayHint("Choose 2 to add something to your cart.");
            return;
        }

        viewCart(customer);
        String id = readString("Enter the Product ID to remove: ").toUpperCase(Locale.ROOT);

        Product p = findById(catalog, id);
        if (p == null) {
            say("I couldn’t find a product with ID " + id + ".");
            return;
        }

        int qty = readPositiveInt("How many should I remove? ");
        try {
            customer.removeFromCart(p, qty);
            say("✅ Removed: " + p.getName() + " × " + qty);
            if (customer.getCart().isEmpty()) {
                say("Your cart is now empty.");
            } else {
                say("Cart total is now: " + CURRENCY.format(customer.getCartTotal()));
            }
        } catch (NoSuchElementException ex) {
            say("That product isn’t in your cart yet.");
        }
    }

    private static void viewCart(Customer customer) {
        ShoppingCart cart = customer.getCart();
        if (cart.isEmpty()) {
            say("Your cart is empty.");
            sayHint("Choose 1 to browse products, then 2 to add something you like.");
            return;
        }
        say("🛒 Your Cart");
        System.out.println(cartToHumanString(cart));
    }

    private static void placeOrderFlow(Customer customer) {
        if (customer.getCart().isEmpty()) {
            say("Your cart is empty. Add something before placing an order.");
            return;
        }

        say("Here’s your cart before we place the order:");
        System.out.println(cartToHumanString(customer.getCart()));

        String sure = readString("Ready to place the order? (y/n): ").toLowerCase(Locale.ROOT);
        if (!sure.startsWith("y")) {
            say("No problem—take your time.");
            return;
        }

        Order order = customer.placeOrder();
        say("🎉 Order placed successfully!");
        System.out.println(order.getSummary());

        say("Would you like to update the order status?");
        say("  1) Leave as NEW");
        say("  2) Mark as PAID");
        say("  3) Mark as SHIPPED (requires PAID)");
        say("  4) CANCELLED");
        int st = readInt("Choose (1-4): ");

        try {
            switch (st) {
                case 2 -> order.setStatus(OrderStatus.PAID);
                case 3 -> {
                    if (order.getStatus() != OrderStatus.PAID) {
                        say("You must pay before shipping. Marking as PAID first…");
                        order.setStatus(OrderStatus.PAID);
                    }
                    order.setStatus(OrderStatus.SHIPPED);
                }
                case 4 -> order.setStatus(OrderStatus.CANCELLED);
                default -> {} // keep NEW
            }
            say("Current Order Status: " + order.getStatus());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            say("Couldn’t update status: " + ex.getMessage());
        }
    }

    // ---------------------------
    // Utility methods
    // ---------------------------

    private static Product findById(List<Product> catalog, String id) {
        for (Product p : catalog) {
            if (p.getProductID().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                say("Please enter a number (e.g., 1, 2, 3).");
            }
        }
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            int v = readInt(prompt);
            if (v > 0) return v;
            say("Please enter a value greater than 0.");
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return SC.nextLine().trim();
    }

    private static String cartToHumanString(ShoppingCart cart) {
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------------------------------\n");
        sb.append(String.format("%-26s %8s %16s%n", "Item", "Qty", "Line Total"));
        sb.append("------------------------------------------------------------\n");
        for (ShoppingCart.Line line : cart.getLines()) {
            String item = line.getProduct().getName();
            int qty = line.getQuantity();
            BigDecimal price = line.getProduct().getPrice();
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));
            sb.append(String.format("%-26s %8d %16s%n", item, qty, CURRENCY.format(lineTotal)));
        }
        sb.append("------------------------------------------------------------\n");
        sb.append(String.format("%-26s %8s %16s%n", "Cart Total", "", CURRENCY.format(cart.getTotal())));
        sb.append("------------------------------------------------------------");
        return sb.toString();
    }
}