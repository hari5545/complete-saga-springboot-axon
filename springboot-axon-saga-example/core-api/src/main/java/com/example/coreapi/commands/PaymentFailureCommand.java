package com.example.coreapi.commands;

import java.math.BigDecimal;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class PaymentFailureCommand {
	
	public final BigDecimal price;
	@TargetAggregateIdentifier
	public final String orderId;

	public final String status;
	
	public PaymentFailureCommand( BigDecimal price, String orderId, String status) {
		super();
		
		this.price = price;
		this.orderId = orderId;
		
		this.status = status;
	}
	
	
}
