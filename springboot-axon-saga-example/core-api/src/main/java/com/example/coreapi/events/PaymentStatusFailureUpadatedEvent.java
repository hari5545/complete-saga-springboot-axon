package com.example.coreapi.events;

public class PaymentStatusFailureUpadatedEvent {
	public final String orderId;

	public final String status;

	public PaymentStatusFailureUpadatedEvent( String orderId, String status) {
		super();
		this.orderId = orderId;
		this.status = status;
	}
}
