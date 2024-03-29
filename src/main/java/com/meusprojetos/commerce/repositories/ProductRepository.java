package com.meusprojetos.commerce.repositories;

import com.meusprojetos.commerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
