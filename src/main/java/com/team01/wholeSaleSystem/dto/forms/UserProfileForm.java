package com.team01.wholeSaleSystem.dto.forms;

import lombok.Data;

@Data
public class UserProfileForm {

    private Long id;
    private String name;
    private String phoneNumber;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    private String code;
    private String userName;

}
