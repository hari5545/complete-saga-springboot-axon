package com.example.paymentservice.aggregate;

import java.util.logging.Level;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.example.coreapi.commands.CreateInvoiceCommand;
import com.example.coreapi.events.PaymentFailureEvent;
import com.example.coreapi.events.PaymentSucessEvent;
import com.example.paymentservice.entities.Payment;
import com.example.paymentservice.service.PaymentServices;
import com.sun.istack.logging.Logger;

@Aggregate
public class InvoiceAggregate {
	@AggregateIdentifier
	protected String paymentId;

	protected String orderId;

	protected InvoiceStatus invoiceStatus;

	public InvoiceAggregate() {	}

	protected PaymentServices paymentServices;
	Logger logger = Logger.getLogger(InvoiceAggregate.class);

	@CommandHandler
	public InvoiceAggregate(CreateInvoiceCommand createInvoiceCommand,PaymentServices paymentServices) {
		this.paymentServices=paymentServices;
		System.out.println("starting payment service.............");
		Payment payment=paymentServices.createPayment(new Payment(createInvoiceCommand.paymentId,"UPI",createInvoiceCommand.price,createInvoiceCommand.orderId)); 
		this.paymentId=payment.getPaymentId();
		logger.log(Level.INFO, "payment object",payment);
		if(payment.getPaymentId()==null||payment.getPaymentId().isEmpty()) {
			AggregateLifecycle.apply(new PaymentFailureEvent(payment.getPaymentId(),payment.getPrice(),payment.getOrderId(),String.valueOf(InvoiceStatus.PAYMENT_FAILED)));
			logger.info("payment failure service InvoiceCreatedcommand ");
			
		}else {
			AggregateLifecycle.apply(new PaymentSucessEvent(payment.getPaymentId(),payment.getPrice(),payment.getOrderId(),String.valueOf(InvoiceStatus.PAID)));
			logger.info("payment sucess service InvoiceCreatedcommand "+payment.getPaymentId());
		}
		//AggregateLifecycle.apply(new InvoiceCreatedEvent(payment.getPaymentId(),payment.getOrderId(),payment.getPrice()));
	}

	@EventSourcingHandler
	protected void on(PaymentSucessEvent paymentSucessEvent) {
		this.paymentId = paymentSucessEvent.paymentId;
		this.orderId =paymentSucessEvent.orderId;
		this.invoiceStatus =InvoiceStatus.PAID;
		logger.info("payment sucess event  ");
	}
	
	@EventSourcingHandler
	protected void on(PaymentFailureEvent paymentFailureEvent){
		this.paymentId = paymentFailureEvent.paymentId;
		this.orderId =paymentFailureEvent.orderId;
		this.invoiceStatus =InvoiceStatus.PAYMENT_FAILED;
		logger.info("payment failure event ");
	}

	/*
	 * @EventSourcingHandler protected void on(InvoiceCreatedEvent
	 * invoiceCreatedEvent){ this.paymentId = invoiceCreatedEvent.paymentId;
	 * this.orderId =invoiceCreatedEvent.orderId; this.invoiceStatus
	 * =InvoiceStatus.PAID; logger.info("paymentservice InvoiceCreatedEvent "); }
	 */

}
