package com.dipal.NextCart.repository;


import com.dipal.NextCart.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
}