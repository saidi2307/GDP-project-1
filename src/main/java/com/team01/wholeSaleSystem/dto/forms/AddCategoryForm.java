package com.team01.wholeSaleSystem.dto.forms;

import lombok.Data;

@Data
public class AddCategoryForm {

    private String oldCategoryName;
    private String name;
    private String description;
}
