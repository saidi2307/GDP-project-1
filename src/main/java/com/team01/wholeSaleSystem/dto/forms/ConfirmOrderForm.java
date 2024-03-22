package com.team01.wholeSaleSystem.dto.forms;

import lombok.Data;

@Data
public class ConfirmOrderForm {

    private Long orderId;
    private String name;
    private String address;
    private String paymentMethod;
    private String creditCardNumber;
    private String expiryDate;
    private String cvv;
}
