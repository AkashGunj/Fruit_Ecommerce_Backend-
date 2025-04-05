package com.fruitshop.ecommerce.service.impl;

import com.fruitshop.ecommerce.model.*;
import com.fruitshop.ecommerce.repository.OrderRepository;
import com.fruitshop.ecommerce.repository.UserRepository;
import com.fruitshop.ecommerce.service.CartService;
import com.fruitshop.ecommerce.service.OrderService;
import com.fruitshop.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public Order createOrder(Long userId, String shippingAddress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        Cart cart = cartService.getCartByUser(userId);
        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Validate stock and update product quantities
        for (CartItem cartItem : cart.getCartItems()) {
            if (!productService.updateStock(cartItem.getProduct().getId(), cartItem.getQuantity())) {
                throw new IllegalStateException("Insufficient stock for product: " + cartItem.getProduct().getName());
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setShippingAddress(shippingAddress);
        order.setTotalAmount(cart.getTotalAmount());

        // Create order items from cart items
        order.setOrderItems(cart.getCartItems().stream()
                .map(cartItem -> createOrderItem(cartItem, order))
                .collect(Collectors.toSet()));

        Order savedOrder = orderRepository.save(order);
        
        // Clear the cart after successful order creation
        cartService.clearCart(userId);

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Order does not belong to user");
        }

        return order;
    }

    @Override
    public List<Order> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    private OrderItem createOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getProduct().getPrice());
        return orderItem;
    }
} 