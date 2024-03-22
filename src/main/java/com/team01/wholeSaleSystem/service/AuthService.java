package com.team01.wholeSaleSystem.service;

import com.team01.wholeSaleSystem.dto.helper.AssistanceForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import com.team01.wholeSaleSystem.dto.auth.UserDto;
import com.team01.wholeSaleSystem.dto.forms.LoginForm;
import com.team01.wholeSaleSystem.dto.forms.SignUpForm;
import com.team01.wholeSaleSystem.dto.forms.UserProfileForm;
import com.team01.wholeSaleSystem.entity.User;

public interface AuthService {

    UserDto authenticateUser(HttpServletRequest request, HttpSession httpSession);
    UserDto authenticateAdmin(HttpServletRequest request, HttpSession httpSession);

    User getUserByRememberMeToken(String rememberMeToken);

    UserDto getUserBySessionToken(String sessionToken);

    UserDto login(LoginForm loginForm);

    User logout(String rememberMeToken, String sessionToken);


    UserDto updateUserProfile(UserProfileForm userProfileForm, MultipartFile file);

    void signUp(SignUpForm signUpForm);

    void saveAssistanceForm(AssistanceForm assistanceForm);

    void changePassword(UserProfileForm userProfileForm);
}
