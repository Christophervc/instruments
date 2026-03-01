package com.aplication.rest.instruments.config;

import com.aplication.rest.instruments.manufacturer.Manufacturer;
import com.aplication.rest.instruments.manufacturer.ManufacturerRepository;
import com.aplication.rest.instruments.product.Product;
import com.aplication.rest.instruments.product.ProductRepository;
import com.aplication.rest.instruments.product.enums.InstrumentType;
import com.aplication.rest.instruments.product.utils.ProductHelper;
import com.aplication.rest.instruments.user.User;
import com.aplication.rest.instruments.user.UserRepository;
import com.aplication.rest.instruments.user.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductHelper productHelper;
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Generate products
        if (productRepository.count() == 0) {
            // 1. create manufacturers
            Manufacturer fender = manufacturerRepository.save(Manufacturer.builder().name("Fender").build());
            Manufacturer gibson = manufacturerRepository.save(Manufacturer.builder().name("Gibson").build());

            // 2. Generate 100 mock products
            List<Product> products = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                UUID productId = UUID.randomUUID();
                Product p = Product.builder()
                        .id(productId)
                        .name("Guitar model " + i)
                        .price(new BigDecimal(100 + (i * 40)))
                        .type(InstrumentType.ELECTRIC_GUITAR)
                        .manufacturer(i % 2 == 0 ? fender : gibson)
                        .active(true)
                        .stock(3)
                        .description("Guitar description " + i)
                        .image_url("https://placehold.co/600x400")
                        .build();
                p.setSku(productHelper.generateSku(p));
                p.setSlug(productHelper.generateSlug(p.getName(), p.getId()));
                products.add(p);
            }
            productRepository.saveAll(products);
            System.out.println("--> DATABASE WITH 100 PRODUCTS ENTRIES<--");
        }

        // Generate base users
        if (userRepository.count() == 0) {
            String adminPassword = passwordEncoder.encode("admin_password123");
            String staffPassword = passwordEncoder.encode("staff_password123");
            String defaultPassword = passwordEncoder.encode("password123");

            User admin = User.builder()
                    .firstName("Christopher")
                    .lastName("Admin")
                    .email("admin@instruments.com")
                    .password(adminPassword)
                    .dni("75757575")
                    .role(Role.ROLE_ADMIN)
                    .phone("+51 970194321")
                    .active(true)
                    .build();

            User staff = User.builder()
                    .firstName("Staff").lastName("Worker")
                    .email("staff@instruments.com")
                    .password(staffPassword)
                    .dni("25252525")
                    .role(Role.ROLE_STAFF)
                    .active(true)
                    .build();

            User customer1 = User.builder()
                    .firstName("Juan").lastName("Perez")
                    .email("juan@email.com").password(defaultPassword)
                    .dni("12345678").role(Role.ROLE_CUSTOMER).active(true).build();

            User customer2 = User.builder()
                    .firstName("Maria").lastName("Lopez")
                    .email("maria@email.com").password(defaultPassword)
                    .dni("87654321").role(Role.ROLE_CUSTOMER).active(true).build();

            userRepository.saveAll(List.of(admin, staff, customer1, customer2));
            System.out.println("USERS CREATED");
        }
    }
}