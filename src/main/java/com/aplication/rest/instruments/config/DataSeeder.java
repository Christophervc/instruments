package com.aplication.rest.instruments.config;

import com.aplication.rest.instruments.manufacturer.Manufacturer;
import com.aplication.rest.instruments.manufacturer.ManufacturerRepository;
import com.aplication.rest.instruments.product.Product;
import com.aplication.rest.instruments.product.ProductRepository;
import com.aplication.rest.instruments.product.enums.InstrumentType;
import com.aplication.rest.instruments.product.utils.ProductHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductHelper productHelper;
    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {

            // 1. create manufacturers
            Manufacturer fender = manufacturerRepository.save(Manufacturer.builder().name("Fender").build());
            Manufacturer gibson = manufacturerRepository.save(Manufacturer.builder().name("Gibson").build());

            // 2. Generate 100 mock products
            List<Product> products = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                Product p = Product.builder()
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
                p.setSlug(productHelper.generateSlug(p.getName()));
                products.add(p);
            }
            productRepository.saveAll(products);
            System.out.println("--> DATABASE WITH 100 PRODUCTS ENTRIES<--");
        }
    }
}