package com.example.orderservice.commandservice;

import java.util.concurrent.CompletableFuture;

import com.example.orderservice.dto.OrderCreateDTO;
import com.example.orderservice.entities.Order;

public interface OrderCommandService {

	public CompletableFuture<Object> createOrder(OrderCreateDTO orderCreateDTO);
	public void setOrderInfoById(String orderStatus, String orderId);
	public void deleteOrder(String orderId);
	public Order updateOrder(Order order);
}
