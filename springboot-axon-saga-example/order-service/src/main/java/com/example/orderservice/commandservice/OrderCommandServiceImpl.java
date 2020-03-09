package com.example.orderservice.commandservice;

import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.coreapi.commands.CreateOrderCommand;
import com.example.orderservice.aggregate.OrderStatus;
import com.example.orderservice.dao.OrderRepositery;
import com.example.orderservice.dto.OrderCreateDTO;
import com.example.orderservice.entities.Order;

@Service
public class OrderCommandServiceImpl implements OrderCommandService {

	private final CommandGateway commandGateway;
	private final OrderRepositery orderRepositery;


	public OrderCommandServiceImpl(CommandGateway commandGateway,OrderRepositery orderRepositery) {
		this.commandGateway = commandGateway;
		this.orderRepositery=orderRepositery;
	}



	@Override 
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public CompletableFuture<Object> createOrder(OrderCreateDTO orderCreateDTO) {

		String status=String.valueOf(OrderStatus.CREATED);
		Order  order=orderRepositery.saveAndFlush(new Order(orderCreateDTO.getItemType(),
				orderCreateDTO.getPrice(), orderCreateDTO.getCurrency(), status)); 
		return commandGateway.send(new CreateOrderCommand(order.getOrderId(),
				order.getItemType(), order.getPrice(), order.getCurrency(),
				String.valueOf(OrderStatus.CREATED)));

		//return commandGateway.send(new CreateOrderCommand(UUID.randomUUID().toString(), orderCreateDTO.getItemType(),
		//	orderCreateDTO.getPrice(), orderCreateDTO.getCurrency(), String.valueOf(OrderStatus.CREATED)));
	}

	
	@Override
	public void deleteOrder(String orderId) {
		orderRepositery.deleteById(orderId);		
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@Override
	public void setOrderInfoById(String orderStatus, String orderId) {
		 System.out.println(orderStatus+"orderId\t"+orderId);
		 orderRepositery.setOrderInfoById(orderStatus, orderId);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@Override
	public Order updateOrder(Order order) {
	 return orderRepositery.save(order);		
	}	
}
