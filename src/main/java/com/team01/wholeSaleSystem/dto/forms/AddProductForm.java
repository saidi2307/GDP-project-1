package com.team01.wholeSaleSystem.dto.forms;

import lombok.Data;

@Data
public class AddProductForm {

    private Long productCategoryId;
    private String name;
    private String description;
    private float price;
    private Integer quantity;
    private String oldProductName;

}
