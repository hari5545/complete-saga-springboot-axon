package com.example.orderservice.aggregate;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.example.coreapi.commands.CreateOrderCommand;
import com.example.coreapi.commands.PaymentFailureCommand;
import com.example.coreapi.commands.PaymentSucessCommand;
import com.example.coreapi.commands.UpdateOrderStatusCommand;
import com.example.coreapi.events.OrderCreatedEvent;
import com.example.coreapi.events.OrderUpdatedEvent;
import com.example.coreapi.events.PaymentStatusFailureUpadatedEvent;
import com.example.coreapi.events.PaymentStatusSucessUpadatedEvent;
import com.example.orderservice.commandservice.OrderCommandService;
import com.sun.istack.logging.Logger;

@Aggregate
public class OrderAggregate {

	Logger logger = Logger.getLogger(OrderAggregate.class);

	@AggregateIdentifier
	protected String orderId;

	protected ItemType itemType;

	protected BigDecimal price;

	protected String currency;

	protected String paymentId;

	protected OrderStatus orderStatus;

	public OrderAggregate() {
	}


	@Inject
	protected OrderCommandService orderCommandService;

	@CommandHandler
	public OrderAggregate(CreateOrderCommand createOrderCommand){

		this.orderId=createOrderCommand.orderId;
		AggregateLifecycle.apply(new OrderCreatedEvent(createOrderCommand.orderId, createOrderCommand.itemType,
				createOrderCommand.price, createOrderCommand.currency, createOrderCommand.orderStatus));
		logger.info("OrderService CreateOrderCommand");
	}

	@EventSourcingHandler
	protected void on(OrderCreatedEvent orderCreatedEvent){
		this.orderId = orderCreatedEvent.orderId;
		this.itemType = ItemType.valueOf(orderCreatedEvent.itemType);
		this.price = orderCreatedEvent.price;
		this.currency = orderCreatedEvent.currency;
		this.orderStatus = OrderStatus.valueOf(orderCreatedEvent.orderStatus);
		logger.info("OrderService OrderCreatedEvent \t");
	}

	@CommandHandler
	protected void on(PaymentSucessCommand paymentSucessCommand) {
		logger.info("payment sucess comamnd in orderservice");
		logger.info("status"+paymentSucessCommand.status);
		orderCommandService.setOrderInfoById(paymentSucessCommand.status, paymentSucessCommand.orderId);
		logger.info("ordercommandService"+orderCommandService.hashCode());

		logger.info("orderservice is updated");

		AggregateLifecycle.apply(new PaymentStatusSucessUpadatedEvent(paymentSucessCommand.paymentId, paymentSucessCommand.orderId, paymentSucessCommand.status));
		logger.info("payment status is updated in db");
	}

	@EventSourcingHandler
	protected void on(PaymentStatusSucessUpadatedEvent paymentStatusSucessUpadatedEvent) {
		logger.info("paymentsucessevent in orderService");
		this.paymentId=paymentStatusSucessUpadatedEvent.paymentId;
		this.orderId=paymentStatusSucessUpadatedEvent.orderId;
	}

	@CommandHandler
	protected void on(PaymentFailureCommand paymentFailureCommand) {
		logger.info("paymentFailed ciommand");
		logger.info("status"+paymentFailureCommand.status);
		orderCommandService.setOrderInfoById(paymentFailureCommand.status, paymentFailureCommand.orderId);
		AggregateLifecycle.apply(new PaymentStatusFailureUpadatedEvent( paymentFailureCommand.orderId, paymentFailureCommand.status));
		logger.info("payment failed status is updated in db");
	}

	@EventSourcingHandler
	protected void on(PaymentStatusFailureUpadatedEvent paymentStatusFailureUpadatedEvent) {
		logger.info("paymentFaiere event ");
		this.orderId=paymentStatusFailureUpadatedEvent.orderId;
	}


	@CommandHandler
	protected void on(UpdateOrderStatusCommand updateOrderStatusCommand){
		AggregateLifecycle.apply(new OrderUpdatedEvent(updateOrderStatusCommand.orderId, updateOrderStatusCommand.orderStatus));
		logger.info("OrderService UpdateOrderStatusCommand \t");
	}

	@EventSourcingHandler
	protected void on(OrderUpdatedEvent orderUpdatedEvent){
		this.orderId =orderUpdatedEvent.orderId;
		this.orderStatus = OrderStatus.valueOf(orderUpdatedEvent.orderStatus);
		logger.info("OrderService UpdateOrderStatusEvent\t");
	}
}
