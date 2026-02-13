package com.aplication.rest.instruments.product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.QueryHints;
import java.util.stream.Stream;
import static org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Optional<Product> findBySku(String sku);
    @QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "500"))
    @Query("SELECT p FROM Product p JOIN FETCH p.manufacturer") //JPQL explicit or derived
    Stream<Product> streamAll();
}
