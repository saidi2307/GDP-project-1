package com.team01.wholeSaleSystem.dto.forms;

import lombok.Data;

@Data
public class SignUpForm {

    private String userName;
    private String password;
    private String confirmPassword;
}
