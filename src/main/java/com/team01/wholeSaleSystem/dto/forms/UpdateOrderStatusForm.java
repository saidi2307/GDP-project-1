package com.team01.wholeSaleSystem.dto.forms;

import lombok.Data;

@Data
public class UpdateOrderStatusForm {

    private Long orderId;
    private String orderStatus;
}
