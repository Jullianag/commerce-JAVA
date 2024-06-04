package com.meusprojetos.commerce.services;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.meusprojetos.commerce.dto.OrderDTO;
import com.meusprojetos.commerce.entities.Order;
import com.meusprojetos.commerce.entities.OrderItem;
import com.meusprojetos.commerce.entities.Product;
import com.meusprojetos.commerce.entities.User;
import com.meusprojetos.commerce.repositories.OrderItemRepository;
import com.meusprojetos.commerce.repositories.OrderRepository;
import com.meusprojetos.commerce.repositories.ProductRepository;
import com.meusprojetos.commerce.services.exceptions.ForbiddenException;
import com.meusprojetos.commerce.services.exceptions.ResourceNotFoundException;
import com.meusprojetos.commerce.tests.OrderFactory;
import com.meusprojetos.commerce.tests.ProductFactory;
import com.meusprojetos.commerce.tests.UserFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuthService authService;

    private Order order;
    private OrderDTO orderDTO;
    private User admin, client;
    private Product product;

    private Long existingOrderId, nonExistingOrderId;
    private Long existingProductId, nonExistingProductId;

    @BeforeEach
    void setUp() throws Exception {

        existingOrderId = 1L;
        nonExistingOrderId = 2L;

        existingProductId = 1L;
        nonExistingProductId = 2L;

        admin = UserFactory.createCustomAdminUser(1L, "Jef");
        client = UserFactory.createCustomClientnUser(2L, "Bob");

        order = OrderFactory.createOrder(client);
        orderDTO = new OrderDTO(order);

        product = ProductFactory.createProduct();
        product.setId(existingProductId);

        Mockito.when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.findById(nonExistingOrderId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged() {

        Mockito.doNothing().when(authService).validateForAdmin(any());

        OrderDTO result = orderService.findById(existingOrderId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingOrderId);
    }

    @Test
    public void findByIdShouldReturnForbiddenExceptionWhenIdExistsAndOrderClientLogged() {

        Mockito.doThrow(ForbiddenException.class).when(authService).validateForAdmin(any());

        Assertions.assertThrows(ForbiddenException.class, () -> {
            orderService.findById(existingProductId);
        });
    }

    @Test
    public void findByIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {

        Mockito.doThrow(ResourceNotFoundException.class).when(authService).validateForAdmin(any());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            orderService.findById(nonExistingOrderId);
        });
    }

    @Test
    public void insertShouldReturnOrderDTOWhenAdminLogged() {

        Mockito.when(userService.authenticated()).thenReturn(admin);

        OrderDTO result = orderService.insert(orderDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    public void insertShouldReturnOrderDTOWhenClientLogged() {

        Mockito.when(userService.authenticated()).thenReturn(client);

        OrderDTO result = orderService.insert(orderDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    public void insertShouldThrowsUsernameNotFoundExceptionWhenUserNotLogged() {

        Mockito.doThrow(UsernameNotFoundException.class).when(userService).authenticated();

        order.setClient(new User());
        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            OrderDTO result = orderService.insert(orderDTO);
        });
    }

    @Test
    public void insertShouldThrowsEntityNotFoundExceptionWhenOrderProductIdDoesNotExist() {

        Mockito.when(userService.authenticated()).thenReturn(client);

        product.setId(nonExistingProductId);
        OrderItem orderItem = new OrderItem(product, order, 2, 10.0);
        order.getItems().add(orderItem);

        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            OrderDTO result = orderService.insert(orderDTO);
        });
    }
}
