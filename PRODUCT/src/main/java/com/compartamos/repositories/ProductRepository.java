package com.compartamos.repositories;

import com.compartamos.entites.Product;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
   
}