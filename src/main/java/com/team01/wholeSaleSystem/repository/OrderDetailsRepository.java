package com.team01.wholeSaleSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team01.wholeSaleSystem.entity.Order;
import com.team01.wholeSaleSystem.entity.OrderDetails;

import java.util.List;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {

    List<OrderDetails> findAllByOrder(Order order);
}
