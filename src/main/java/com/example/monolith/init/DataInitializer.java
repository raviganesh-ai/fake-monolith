package com.example.monolith.init;

import com.example.monolith.domain.Customer;
import com.example.monolith.domain.Product;
import com.example.monolith.repository.CustomerRepository;
import com.example.monolith.repository.ProductRepository;
import com.example.monolith.service.OrderLineRequest;
import com.example.monolith.service.OrderService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Seeds fictional demonstration data at startup: approximately 20 customers,
 * 30 products, and 50 orders. Idempotent — does nothing if data already exists.
 * Run is intentionally not transactional so each seeded order commits on its own.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final String[] FIRST_NAMES = {
            "Ava", "Liam", "Noah", "Emma", "Olivia", "Ethan", "Sophia", "Mason",
            "Isabella", "Lucas", "Mia", "Logan", "Amelia", "Jacob", "Harper",
            "Elijah", "Evelyn", "James", "Abigail", "Benjamin"
    };

    private static final String[] LAST_NAMES = {
            "Carter", "Reed", "Morgan", "Bailey", "Hughes", "Foster", "Bennett",
            "Perry", "Ross", "Barnes", "Coleman", "Jenkins", "Powell", "Long",
            "Patterson", "Flores", "Ward", "Simmons", "Hayes", "Bryant"
    };

    private static final String[] PRODUCT_WORDS = {
            "Widget", "Gadget", "Bracket", "Clamp", "Valve", "Bearing", "Gasket",
            "Fastener", "Coupling", "Flange"
    };

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;

    public DataInitializer(CustomerRepository customerRepository,
                           ProductRepository productRepository,
                           OrderService orderService) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderService = orderService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (customerRepository.count() > 0) {
            return;
        }

        Random random = new Random(42);

        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String name = FIRST_NAMES[i] + " " + LAST_NAMES[i];
            String email = (FIRST_NAMES[i] + "." + LAST_NAMES[i]).toLowerCase() + "@example.com";
            String phone = "555-01" + String.format("%02d", i);
            String address = (100 + i) + " Main Street";
            customers.add(customerRepository.save(new Customer(name, email, phone, address)));
        }

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            String sku = String.format("P%03d", i + 1);
            String name = PRODUCT_WORDS[i % PRODUCT_WORDS.length] + " " + (i + 1);
            BigDecimal price = BigDecimal.valueOf(5 + random.nextInt(96));
            int stock = 50 + random.nextInt(101);
            products.add(productRepository.save(new Product(sku, name, "Fictional sample product", price, stock)));
        }

        for (int i = 0; i < 50; i++) {
            Customer customer = customers.get(random.nextInt(customers.size()));
            int lineCount = 1 + random.nextInt(3);
            List<OrderLineRequest> lines = new ArrayList<>();
            for (int j = 0; j < lineCount; j++) {
                Product product = products.get(random.nextInt(products.size()));
                int quantity = 1 + random.nextInt(3);
                lines.add(new OrderLineRequest(product.getId(), quantity));
            }
            try {
                orderService.placeOrder(customer.getId(), lines);
            } catch (RuntimeException ignored) {
                // Skip the rare case where combined duplicate lines exceed stock.
            }
        }
    }
}
