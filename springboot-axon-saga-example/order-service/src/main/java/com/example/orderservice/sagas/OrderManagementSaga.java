package com.example.orderservice.sagas;

import java.util.UUID;

import javax.inject.Inject;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;

import com.example.coreapi.commands.CreateInvoiceCommand;
import com.example.coreapi.commands.PaymentFailureCommand;
import com.example.coreapi.commands.PaymentSucessCommand;
import com.example.coreapi.events.OrderCreatedEvent;
import com.example.coreapi.events.PaymentFailureEvent;
import com.example.coreapi.events.PaymentSucessEvent;

@Saga
public class OrderManagementSaga {

	@Inject
	private transient CommandGateway commandGateway;

	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderCreatedEvent orderCreatedEvent){
		String paymentId = UUID.randomUUID().toString();
		System.out.println("Saga invoked");

		//associate Saga
		SagaLifecycle.associateWith("paymentId", paymentId);

		System.out.println("order id" + orderCreatedEvent.orderId);

		//send the commands
		commandGateway.send(new CreateInvoiceCommand(paymentId, orderCreatedEvent.orderId,orderCreatedEvent.price));
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void handle(PaymentSucessEvent paymentSucessEvent) {
		System.out.println("Saga continued");

		//associate Saga with shipping 
		SagaLifecycle.associateWith("orderId",paymentSucessEvent.orderId);
		System.out.println("paymentId \t "+paymentSucessEvent.paymentId); 
		//send the create shipping command 

		commandGateway.send(new PaymentSucessCommand(paymentSucessEvent.paymentId,paymentSucessEvent.price,paymentSucessEvent.orderId,
				paymentSucessEvent.status));
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void handle(PaymentFailureEvent paymentFailureEvent) {

		System.out.println("Saga continued");

		//associate Saga with shipping 
		SagaLifecycle.associateWith("orderId",paymentFailureEvent.orderId);
		System.out.println("payment Id \t "+paymentFailureEvent.paymentId);  
		//send the create shipping command 
		//commandGateway.send(new UpdateOrderStatusCommand(paymentFailureEvent.orderId,String.valueOf(OrderStatus.REJECTED)));
		commandGateway.send(new PaymentFailureCommand(paymentFailureEvent.price,paymentFailureEvent.orderId,
				paymentFailureEvent.status));
	}

	/*
	 * @SagaEventHandler(associationProperty = "orderId") public void
	 * handleShipping(PaymentStatusSucessUpadatedEvent
	 * paymentStatusSucessUpadatedEvent){ String shippingId =
	 * UUID.randomUUID().toString();
	 * 
	 * System.out.println("Saga continued");
	 * 
	 * //associate Saga with shipping
	 * SagaLifecycle.associateWith("shipping",shippingId);
	 * System.out.println("payment Id \t "+paymentStatusSucessUpadatedEvent.
	 * paymentId); //send the create shipping command commandGateway.send(new
	 * CreateShippingCommand(shippingId, paymentStatusSucessUpadatedEvent.orderId,
	 * paymentStatusSucessUpadatedEvent.paymentId)); }
	 * 
	 * 
	 * @SagaEventHandler(associationProperty = "orderId") public void
	 * handle(OrderShippedEvent orderShippedEvent){
	 * 
	 * commandGateway.send(new UpdateOrderStatusCommand(orderShippedEvent.orderId,
	 * String.valueOf(OrderStatus.SHIPPED)));
	 * System.out.println("saga upadte command "); }
	 * 
	 * @SagaEventHandler(associationProperty = "orderId") public void
	 * handle(OrderUpdatedEvent orderUpdatedEvent) { SagaLifecycle.end(); }
	 */
	@SagaEventHandler(associationProperty = "orderId")
	public void handleSaga(PaymentSucessEvent paymentSucessEvent){
		SagaLifecycle.end();
		System.out.println("saga ends in failure way");
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void handleSaga(PaymentFailureEvent paymentFailureEvent){
		SagaLifecycle.end();
		System.out.println("saga ends in failure way");
	}
}