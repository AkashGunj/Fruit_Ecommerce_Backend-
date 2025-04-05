package com.fruitshop.ecommerce.service;

import com.fruitshop.ecommerce.model.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(Long userId, String shippingAddress);
    Order getOrderById(Long orderId, Long userId);
    List<Order> getUserOrders(Long userId);
    Order updateOrderStatus(Long orderId, Order.OrderStatus status);
    List<Order> getOrdersByStatus(Order.OrderStatus status);
} 