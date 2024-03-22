package com.team01.wholeSaleSystem.dto.forms;

import lombok.Data;

@Data
public class AddToCartForm {

    private Long productId;
    private int quantity;
}
