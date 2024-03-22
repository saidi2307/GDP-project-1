package com.team01.wholeSaleSystem.dto.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

import com.team01.wholeSaleSystem.entity.Order;
import com.team01.wholeSaleSystem.entity.OrderDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderData {
    private Order order;
    private List<OrderDetails> orderDetails;
    private float totalAmount;
    private int numberOfItems;
    private String paymentMethod;
    private String orderStatus;
    private String paymentStatus;
    private Timestamp orderPlacedTime;

    private String name;
    private String address;
    private String creditCardNumber;
    private String expiryDate;
}
