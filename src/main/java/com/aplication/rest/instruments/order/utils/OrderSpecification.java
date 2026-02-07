package com.aplication.rest.instruments.order.utils;
import com.aplication.rest.instruments.order.Order;
import com.aplication.rest.instruments.order.dto.OrderSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> fromCriteria(OrderSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(criteria.status() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.status()));
            }

            if(criteria.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.startDate()));
            }

            if(criteria.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.endDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));

        };

    }
}
