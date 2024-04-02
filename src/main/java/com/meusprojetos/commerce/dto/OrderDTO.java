package com.meusprojetos.commerce.dto;

import com.meusprojetos.commerce.entities.Order;
import com.meusprojetos.commerce.entities.OrderItem;
import com.meusprojetos.commerce.entities.OrderStatus;
import com.meusprojetos.commerce.entities.Payment;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {

    private Long id;
    private Instant moment;
    private OrderStatus status;
    private ClientDTO client;
    private PaymentDTO paymen;

    @NotEmpty(message = "A lista não pode ser vazia")
    private List<OrderItemDTO> items = new ArrayList<>();

    public OrderDTO(Long id, Instant moment, OrderStatus status, ClientDTO client, PaymentDTO paymen) {
        this.id = id;
        this.moment = moment;
        this.status = status;
        this.client = client;
        this.paymen = paymen;
    }

    public OrderDTO(Order entity) {
        id = entity.getId();
        moment = entity.getMoment();
        status = entity.getStatus();
        client = new ClientDTO(entity.getClient());
        paymen = (entity.getPayment() == null) ? null : new PaymentDTO(entity.getPayment());
        for (OrderItem item : entity.getItems()) {
            OrderItemDTO itemDTO = new OrderItemDTO(item);
            items.add(itemDTO);
        }
    }

    public Long getId() {
        return id;
    }

    public Instant getMoment() {
        return moment;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public ClientDTO getClient() {
        return client;
    }

    public PaymentDTO getPaymen() {
        return paymen;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public Double getTotal() {
        double sum = 0.0;
        for (OrderItemDTO item : items) {
            sum += item.getSubtotal();
        }
        return sum;
    }
}
