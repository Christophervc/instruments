package com.aplication.rest.instruments.product.utils;

import com.aplication.rest.instruments.product.Product;
import com.aplication.rest.instruments.product.dto.ProductSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class ProductSpecification {

    public static Specification<Product> fromCriteria(ProductSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            //1. Filter by name (LIKE %name% - case insensitive)
            if(StringUtils.hasText(criteria.name())) {
                String namePattern = "%" + criteria.name().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), namePattern));
            }

            //2. Filter by price range
            if(criteria.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), criteria.minPrice()));
            }
            if (criteria.maxPrice() != null) {
                predicates.add( cb.lessThanOrEqualTo(root.get("price"), criteria.maxPrice()));
            }

            //3. Filter by manufacturer(brand)
            if(StringUtils.hasText(criteria.manufacturer())){
                String manufacturerPattern = "%" + criteria.manufacturer().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("manufacturer").get("name")),manufacturerPattern));
            }
            //4. Filter by type (exact match)
            if (criteria.type() != null){
                predicates.add(cb.equal(root.get("type"), criteria.type()));
            }
            //  only active products
            predicates.add(cb.isTrue(root.get("active")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
