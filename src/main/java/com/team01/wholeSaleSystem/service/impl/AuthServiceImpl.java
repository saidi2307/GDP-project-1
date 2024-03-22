package com.team01.wholeSaleSystem.service.impl;

import com.team01.wholeSaleSystem.dto.helper.AssistanceForm;
import com.team01.wholeSaleSystem.entity.Assistance;
import com.team01.wholeSaleSystem.liveChat.user.Status;
import com.team01.wholeSaleSystem.repository.AssistanceRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.team01.wholeSaleSystem.dto.auth.UserDto;
import com.team01.wholeSaleSystem.dto.forms.LoginForm;
import com.team01.wholeSaleSystem.dto.forms.SignUpForm;
import com.team01.wholeSaleSystem.dto.forms.UserProfileForm;
import com.team01.wholeSaleSystem.entity.User;
import com.team01.wholeSaleSystem.repository.UserRepository;
import com.team01.wholeSaleSystem.service.AuthService;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AssistanceRepository assistanceRepository;

    @Override
    public UserDto authenticateUser(HttpServletRequest request, HttpSession httpSession) {

        String sessionToken = httpSession.getAttribute("session-token") != null
                ? httpSession.getAttribute("session-token").toString()
                : null;

        if (sessionToken != null) {
            User user = userRepository.findBySessionToken(sessionToken);
            if (user != null)
                return mapUserDto(user);
            return null;
        }

        Cookie[] cookies = request.getCookies();
        String rememberMeToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember-me-cookie".equals(cookie.getName())) {
                    rememberMeToken = cookie.getValue();
                    break;
                }
            }
        }

        if (rememberMeToken != null) {
            User user = userRepository.findByRememberMeToken(rememberMeToken);
            if (user != null) {
                sessionToken = UUID.randomUUID().toString();
                user.setSessionToken(sessionToken);
                userRepository.save(user);
                return mapUserDto(user);
            }
        }
        return null;
    }

    @Override
    public UserDto authenticateAdmin(HttpServletRequest request, HttpSession httpSession) {

        String sessionToken = httpSession.getAttribute("session-token") != null
                ? httpSession.getAttribute("session-token").toString()
                : null;

        if (sessionToken != null) {
            User user = userRepository.findBySessionToken(sessionToken);
            if (user != null)
                return mapUserDto(user);
            return null;
        }

        Cookie[] cookies = request.getCookies();
        String rememberMeToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember-me-cookie".equals(cookie.getName())) {
                    rememberMeToken = cookie.getValue();
                    break;
                }
            }
        }

        if (rememberMeToken != null) {
            User user = userRepository.findByRememberMeToken(rememberMeToken);
            if (user != null) {
                sessionToken = UUID.randomUUID().toString();
                user.setSessionToken(sessionToken);
                userRepository.save(user);
                return mapUserDto(user);
            }
        }
        return null;
    }

    @SuppressWarnings("static-access")
    @Override
    public User getUserByRememberMeToken(String rememberMeToken) {
        return new User().builder()
                .id(1l)
                .userName("asjid")
                .password("password")
                .rememberMeToken("remembertoken")
                .sessionToken("sessiontoken")
                .phone("03044115263")
                .name("User name")
                .build();
    }

    @Override
    public UserDto getUserBySessionToken(String sessionToken) {
        return null;
    }

    @Override
    @SneakyThrows
    public UserDto login(LoginForm loginForm) {
        User user = userRepository.findByUserName(loginForm.getUsername());
        if (user == null || !user.getPassword().equals(loginForm.getPassword()))
            throw new AccessDeniedException("Invalid Credentials");

        user.setSessionToken(UUID.randomUUID().toString());
        if (loginForm.getRememberMe())
            user.setRememberMeToken(UUID.randomUUID().toString());

        userRepository.save(user);
        return mapUserDto(user);
    }

    @Override
    public User logout(String rememberMeToken, String sessionToken) {
        return null;
    }

    @Override
    @SneakyThrows
    public UserDto updateUserProfile(UserProfileForm userProfileForm, MultipartFile file) {
        User user = userRepository.findById(userProfileForm.getId())
                .orElseThrow(() -> new Exception("Invalid user"));

        if (!StringUtils.isEmpty(userProfileForm.getNewPassword())
                || !StringUtils.isEmpty(userProfileForm.getConfirmPassword())
                || !StringUtils.isEmpty(userProfileForm.getCurrentPassword())) {
            if (!userProfileForm.getCurrentPassword().equals(user.getPassword()))
                throw new Exception("Invalid current password");

            if (!userProfileForm.getConfirmPassword().equals(userProfileForm.getNewPassword()))
                throw new Exception("New password and confirm password must be same");
            user.setPassword(userProfileForm.getNewPassword());
        }


        user.setName(userProfileForm.getName());
        user.setPhone(userProfileForm.getPhoneNumber());

        if (!file.isEmpty())
            user.setImage(file.getBytes());

        userRepository.save(user);

        return mapUserDto(user);
    }

    @Override
    @SneakyThrows
    public void signUp(SignUpForm signUpForm) {
        User user = userRepository.findByUserName(signUpForm.getUserName());
        if (user != null)
            throw new Exception("User already exists");
        if (StringUtils.isEmpty(signUpForm.getPassword())
                || StringUtils.isEmpty(signUpForm.getPassword())
                || !signUpForm.getPassword().equals(signUpForm.getConfirmPassword()))
            throw new Exception("Please provide valid password and confirm password");
        userRepository.save(User.builder()
                .userName(signUpForm.getUserName())
                .password(signUpForm.getPassword())
                .role("CUSTOMER")
                .status(Status.ONLINE)
                .build());
    }

    @Override
    public void saveAssistanceForm(AssistanceForm assistanceForm) {
        assistanceRepository.save(Assistance.builder()
                .email(assistanceForm.getEmail())
                .text(assistanceForm.getText())
                .build());

    }


    private UserDto mapUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .name(user.getName())
                .phoneNumber(user.getPhone())
                .sessionToken(user.getSessionToken())
                .rememberMeToken(user.getRememberMeToken())
                .image(user.getImage())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }

    @Override
    @SneakyThrows
    public void changePassword(UserProfileForm userProfileForm) {
        User user = userRepository.findByUserName(userProfileForm.getUserName());
        if (user == null)
            throw new Exception("User does not exists");
        if (!userProfileForm.getCode().equalsIgnoreCase("1234"))
            throw new Exception("Invalid code");
        if (!userProfileForm.getNewPassword().equals(userProfileForm.getConfirmPassword()))
            throw new Exception("Password and confirm password are not same");

        user.setPassword(userProfileForm.getNewPassword());
        userRepository.save(user);
    }

}
