package com.team01.wholeSaleSystem.controller;

import com.team01.wholeSaleSystem.dto.helper.AssistanceForm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.team01.wholeSaleSystem.dto.auth.UserDto;
import com.team01.wholeSaleSystem.dto.forms.*;
import com.team01.wholeSaleSystem.dto.helper.CartData;
import com.team01.wholeSaleSystem.dto.helper.OrderData;
import com.team01.wholeSaleSystem.entity.Order;
import com.team01.wholeSaleSystem.service.AuthService;
import com.team01.wholeSaleSystem.service.ProductService;

import java.util.List;

@Controller
public class AuthController {

    @Autowired
    AuthService authService;
    @Autowired
    ProductService productService;

    @GetMapping("/")
    public String index(HttpServletRequest request, HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        model.addAttribute("orderPlaced", false);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        if (userDto != null) {
            model.addAttribute("user", userDto);
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("session-token", userDto.getSessionToken());
            httpSession.setAttribute("session-token", userDto.getSessionToken());
            return "Dashboard";
        }
        return "Login";
    }

    @GetMapping("/signUp")
    public String signUpPage() {
        return "Signup";
    }

    @PostMapping("/signUp")
    public String signUp(@ModelAttribute(name = "SignUpForm") SignUpForm signUpForm, HttpSession httpSession,
                         HttpServletResponse response, Model model) {
        try {
            authService.signUp(signUpForm);
            return "Login";

        } catch (Exception e) {
            model.addAttribute("error", true);
            model.addAttribute("message", e.getMessage());
            return "Signup";
        }
    }


    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, HttpSession session, Model model) {
        System.out.println(model.getAttribute("session-token") != null ? model.getAttribute("session-token").toString() : null);
        UserDto userDto = authService.authenticateUser(request, session);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        if (userDto != null) {
            return "redirect:/";
        }
        return "Login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(HttpServletRequest request, HttpSession session, Model model) {
        UserDto userDto = authService.authenticateUser(request, session);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        if (userDto != null) {
            return "redirect:/";
        }
        model.addAttribute("success", "");
        model.addAttribute("message", "");
        return "Forgot_password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute(name = "UserProfileForm") UserProfileForm userProfileForm,
                                 HttpServletRequest request,
                                 HttpSession httpSession, HttpServletResponse response, Model model) {
        try {
            authService.changePassword(userProfileForm);
            model.addAttribute("success", true);
            model.addAttribute("message", "User password updated successfully, Please proceed to login");
            return "Forgot_password";

        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("message", e.getMessage());
            return "Forgot_password";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpSession httpSession, HttpServletResponse response) {
        Cookie rememberMeCookie = new Cookie("remember-me-cookie", "");
        rememberMeCookie.setMaxAge(0); // 1 month in seconds
        response.addCookie(rememberMeCookie);
        httpSession.removeAttribute("session-token");
        return "Login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute(name = "LoginForm") LoginForm loginForm, HttpSession httpSession,
                        HttpServletResponse response, Model model) {
        try {
            UserDto userDto = authService.login(loginForm);

            if (loginForm.getRememberMe()) {
                Cookie rememberMeCookie = new Cookie("remember-me-cookie", userDto.getRememberMeToken());
                rememberMeCookie.setMaxAge(2628288); // 1 month in seconds
                response.addCookie(rememberMeCookie);
            } else {
                Cookie rememberMeCookie = new Cookie("remember-me-cookie", "");
                rememberMeCookie.setMaxAge(0); // 1 month in seconds
                response.addCookie(rememberMeCookie);
            }
            httpSession.setAttribute("session-token", userDto.getSessionToken());
            if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
                return "redirect:/admin/dashboard";
            return "redirect:/";

        } catch (Exception e) {
            if (e.getMessage().equals("Invalid Credentials")) {
                model.addAttribute("invalidCredentials", true);
                return "Login";
            }
        }
        return "Login";
    }


    @GetMapping("/products")
    public String product(@RequestParam(name = "categoryId") Long categoryId, HttpServletRequest request, Model model, HttpSession httpSession) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        System.out.println(httpSession.getAttribute("session-token") != null ? httpSession.getAttribute("session-token").toString() : null);
        model.addAttribute("products", productService.getProductByCategory(categoryId));
        model.addAttribute("categories", productService.getAllCategories());
        return "Product";
    }

    @PostMapping("/product-search")
    public String searchProduct(@ModelAttribute(name = "ProductSearchForm") ProductSearchForm productSearchForm,
                                HttpSession httpSession,
                                HttpServletRequest request,
                                HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        model.addAttribute("products", productService.searchProduct(productSearchForm));
        model.addAttribute("categories", productService.getAllCategories());
        return "Product";
    }

    @GetMapping("/user-profile")
    public String userProfile(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        model.addAttribute("user", userDto);
        model.addAttribute("categories", productService.getAllCategories());
        return "User-profile";
    }

    @GetMapping("/cart")
    public String cart(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        model.addAttribute("user", userDto);
        model.addAttribute("categories", productService.getAllCategories());

        CartData cartData = productService.getCartData(userDto.getId());
        model.addAttribute("cartData", cartData);
        return "Cart";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute(name = "UserProfileForm") UserProfileForm userProfileForm,
                                @RequestParam("file") MultipartFile file,
                                HttpServletRequest request,
                                HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";

        try {
            UserDto user = authService.updateUserProfile(userProfileForm, file);
            model.addAttribute("user", user);
            model.addAttribute("success", true);
            model.addAttribute("message", "User profile updated successfully");
            model.addAttribute("categories", productService.getAllCategories());
            return "User-profile";

        } catch (Exception e) {
            model.addAttribute("error", true);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("user", userDto);
            model.addAttribute("categories", productService.getAllCategories());
            return "User-profile";
        }
    }

    @PostMapping("/add-to-cart")
    @ResponseBody
    public Boolean addToCart(HttpServletRequest request, HttpSession httpSession,
                             Model model, @RequestBody AddToCartForm addToCartForm) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return false;
        productService.addToCart(userDto, addToCartForm);
        return true;
    }

    @GetMapping("/chat")
    public String getChatPage(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("user", userDto);
        return "Live-chat";
    }

    @GetMapping("/delete-cart-detail")
    public String deleteCartDetail(@RequestParam Long id) {
        productService.deleteCartDetail(id);
        return "redirect:/cart";
    }

    @GetMapping("/increment-cart-detail")
    public String incrementCartDetail(@RequestParam Long id) {
        productService.incrementCartDetail(id);
        return "redirect:/cart"; // Redirect to another page after deletion
    }

    @GetMapping("/decrement-cart-detail")
    public String decrementCartDetail(@RequestParam Long id) {
        productService.decrementCartDetail(id);
        return "redirect:/cart"; // Redirect to another page after deletion
    }

    @GetMapping("/check-out")
    public String checkOut(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        model.addAttribute("user", userDto);
        model.addAttribute("categories", productService.getAllCategories());
        OrderData orderData = productService.createOrder(userDto.getId());
        model.addAttribute("orderData", orderData);
        return "Check-out";
    }

    @PostMapping("/confirm-order")
    public String confirmOrder(@ModelAttribute(name = "ConfirmOrderForm") ConfirmOrderForm confirmOrderForm,
                               HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        productService.confirmOrder(confirmOrderForm);
        model.addAttribute("orderPlaced", true);
        model.addAttribute("user", userDto);
        model.addAttribute("categories", productService.getAllCategories());
        return "Dashboard";
    }

    @GetMapping("/get-orders")
    public String getOrders(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return "redirect:/admin/dashboard";
        model.addAttribute("user", userDto);
        model.addAttribute("categories", productService.getAllCategories());
        List<Order> orders = productService.getOrders(userDto.getId());
        model.addAttribute("orders", orders);
        return "My-Orders";
    }

    @GetMapping("/assistance")
    public String helpPage(Model model) {
        model.addAttribute("saved", false);
        return "Assistance";
    }

    @PostMapping("/save-assistance-form")
    public String saveHelp(@ModelAttribute(name = "AssistanceForm") AssistanceForm assistanceForm,
                           HttpServletResponse response, Model model) {
        authService.saveAssistanceForm(assistanceForm);
        model.addAttribute("saved", true);
        return "Assistance";

    }
    
    @PostMapping("/delete-order")
    @ResponseBody
    public Boolean deleteOrder(HttpServletRequest request, HttpSession httpSession,
                             Model model, @RequestBody DeleteOrderForm deleteOrderForm) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("ADMIN"))
            return false;
        productService.deleteOrder(userDto, deleteOrderForm);
        return true;
    }


}