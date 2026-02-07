package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.core.audit.AuditableEntity;
import com.aplication.rest.instruments.manufacturer.Manufacturer;
import com.aplication.rest.instruments.product.enums.InstrumentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
@SQLRestriction("is_active = true")
public class Product extends AuditableEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "name")
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private InstrumentType type;
    @Column(name = "description", length = 1000)
    private String description;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name="slug", nullable = false, unique = true)
    private String slug;
    @Column(name = "sku", nullable = false, unique = true)
    private String sku;
    @Column(name = "stock")
    private Integer stock;
    @Column(name = "is_active")
    @Builder.Default
    private Boolean active = true;
    @Column(name = "image_url")
    private String image_url;
    @ManyToOne
    @JoinColumn(name = "manufacturer_id", nullable = false)
    @JsonIgnore
    private Manufacturer manufacturer;
}
