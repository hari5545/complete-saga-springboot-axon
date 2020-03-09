package com.example.orderservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.orderservice.entities.Order;

@Repository
public interface OrderRepositery extends JpaRepository<Order, String>{
	
	@Modifying
	@Query("update Order u set u.orderStatus = :orderStatus where u.orderId = :orderId")
	void setOrderInfoById(String orderStatus, String orderId);

}