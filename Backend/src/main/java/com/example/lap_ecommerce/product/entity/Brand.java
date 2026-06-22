package com.example.lap_ecommerce.product.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Long brandId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;


}