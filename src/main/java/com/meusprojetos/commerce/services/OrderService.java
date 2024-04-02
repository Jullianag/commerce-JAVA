package com.meusprojetos.commerce.services;

import com.meusprojetos.commerce.dto.OrderDTO;
import com.meusprojetos.commerce.entities.Order;
import com.meusprojetos.commerce.repositories.OrderRepository;
import com.meusprojetos.commerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Transactional
    public OrderDTO findById(Long id) {
        Order order = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("id não encontrado!"));
        return new OrderDTO(order);
    }
}
