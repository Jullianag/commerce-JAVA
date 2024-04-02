package com.meusprojetos.commerce.repositories;

import com.meusprojetos.commerce.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
