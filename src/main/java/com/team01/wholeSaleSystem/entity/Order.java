package com.team01.wholeSaleSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_date")
    private Timestamp orderDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private float price;

    private String paymentMethod;
    private String orderStatus;
    private String paymentStatus;

    private String name;
    private String address;
    
    private String creditCardNumber;
    private String expiryDate;

}
