package com.meusprojetos.commerce.repositories;

import com.meusprojetos.commerce.entities.OrderItem;
import com.meusprojetos.commerce.entities.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {
}
