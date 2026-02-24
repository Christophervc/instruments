package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.manufacturer.Manufacturer;
import com.aplication.rest.instruments.manufacturer.ManufacturerRepository;
import com.aplication.rest.instruments.manufacturer.dto.ManufacturerDTO;
import com.aplication.rest.instruments.product.dto.ProductDTO;
import com.aplication.rest.instruments.product.enums.InstrumentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest // 1. Init spring boot test (DB, Services, Controllers)
@AutoConfigureMockMvc // 2. Config mock HTTP client (MockMvc)
@ActiveProfiles("test") // 3. Using application-test.properties (H2)
@Transactional
public class ProductIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // To convert Java objects to JSON String

    @Autowired
    private ProductRepository productRepository; // verify DB
    @Autowired
    private ManufacturerRepository manufacturerRepository;
/*
TODO
    @Test
    void createProduct_ShouldReturnSavedProduct_WhenInputIsValid() throws Exception{
        // --- ARRANGE (Preparing JSON) ---
        //creating manufacturer
        Manufacturer manufacturer = Manufacturer.builder().name("Gibson").build();
        Manufacturer savedManufacturer = manufacturerRepository.save(manufacturer);
        ManufacturerDTO manufacturerDTO = ManufacturerDTO.builder().id(savedManufacturer.getId()).name(savedManufacturer.getName()).build();
        //creating product
        ProductDTO productDTO = ProductDTO.builder()
                .name("Gibson Les Paul Standard")
                .price(BigDecimal.valueOf(3000.00))
                .type(InstrumentType.ELECTRIC_GUITAR)
                .sku("GIB-LP-001")
                .description("The Gibson Les Paul is the world's most desirable electric solid body guitar")
                .active(true)
                .stock(5)
                .manufacturer(manufacturerDTO)
                .build();
        // --- ACT (Execute HTTP request) ---
        ResultActions response = mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)));
        // --- ASSERT (Verify result) ---
        response.andDo(print()) // show logs in console
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess", is(true)))
                .andExpect(jsonPath("$.data.name", is("Gibson Les Paul Standard")))
                .andExpect(jsonPath("$.data.stock", is(5)))
                // Verify that response includes the correct manufacturer
                .andExpect(jsonPath("$.data.manufacturer.id", is(savedManufacturer.getId().toString())));
        // Verify if it exists in DB
        boolean exists = productRepository.findAll().stream().anyMatch(p -> p.getSku().equals("GIB-LP-001"));
        assert(exists);
    }
    */

}
