package com.example.coreapi.events;

public class PaymentStatusSucessUpadatedEvent {

	public final String paymentId;
	public final String orderId;
	
	public final String status;

	public PaymentStatusSucessUpadatedEvent(String paymentId, String orderId, String status) {
		super();
		this.paymentId = paymentId;
		this.orderId = orderId;
		this.status = status;
	}
	
	
}
