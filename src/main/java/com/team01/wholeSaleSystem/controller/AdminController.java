package com.team01.wholeSaleSystem.controller;

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
import com.team01.wholeSaleSystem.dto.helper.MonthlySale;
import com.team01.wholeSaleSystem.service.AuthService;
import com.team01.wholeSaleSystem.service.ProductService;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    AuthService authService;
    @Autowired
    ProductService productService;

    @GetMapping("/admin/dashboard")
    public String getDashboard(HttpServletRequest request, HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateAdmin(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";
        if (userDto != null) {
            model.addAttribute("user", userDto);
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("session-token", userDto.getSessionToken());
            httpSession.setAttribute("session-token", userDto.getSessionToken());
            return "Admin-dashboard";
        }
        return "Login";
    }

    @PostMapping("/admin/product-search")
    public String searchProduct(@ModelAttribute(name = "ProductSearchForm") ProductSearchForm productSearchForm,
                                HttpSession httpSession,
                                HttpServletRequest request,
                                HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";
        model.addAttribute("products", productService.searchProductAdmin(productSearchForm));
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("success", "null");
        model.addAttribute("message", "");
        return "Admin-Product";
    }

    @GetMapping("/admin/categories")
    public String getCategoriesPage(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";
        model.addAttribute("categories", productService.getAllCategoriesAdmin());
        model.addAttribute("success", "null");
        model.addAttribute("message", "");

        return "Admin-Category";
    }

    @GetMapping("/admin/product")
    public String getProductPage(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";
        model.addAttribute("products", productService.searchProductAdmin(ProductSearchForm.builder().categoryId(0L).build()));
        model.addAttribute("categories", productService.getAllCategoriesAdmin());
        model.addAttribute("success", "null");
        model.addAttribute("message", "");

        return "Admin-Product";
    }

    @GetMapping("/admin/chat")
    public String getChatPage(HttpServletRequest request, HttpSession httpSession, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("user", userDto);
        return "Admin-chat-room";
    }

    @PostMapping("/admin/add-category")
    public String addCategory(@ModelAttribute(name = "AddCategoryForm") AddCategoryForm addCategoryForm,
                              @RequestParam("file") MultipartFile file,
                              HttpServletRequest request,
                              HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";

        try {
            productService.addCategory(addCategoryForm, file);
            model.addAttribute("categories", productService.getAllCategoriesAdmin());
            model.addAttribute("success", true);
            model.addAttribute("message", "Category added/updated successfully");
            return "Admin-Category";

        } catch (Exception e) {
            model.addAttribute("categories", productService.getAllCategoriesAdmin());
            model.addAttribute("success", false);
            model.addAttribute("message", e.getMessage());
            return "Admin-Category";
        }
    }

    @GetMapping("/admin/category-status-update")
    public String deleteCategory(@RequestParam(name = "categoryId") Long categoryId,
                                 HttpServletRequest request,
                                 HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";

        try {
            productService.categoryStatusUpdate(categoryId);
            model.addAttribute("success", true);
            model.addAttribute("message", "Category status updated successfully");
            model.addAttribute("categories", productService.getAllCategoriesAdmin());
            return "Admin-Category";

        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("categories", productService.getAllCategoriesAdmin());
            return "Admin-Category";
        }
    }

    @PostMapping("/admin/add-product")
    public String addProduct(@ModelAttribute(name = "AddProductForm") AddProductForm addProductForm,
                             @RequestParam("file") MultipartFile file,
                             HttpServletRequest request,
                             HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";

        try {
            productService.addProduct(addProductForm, file);
            model.addAttribute("products", productService.searchProductAdmin(ProductSearchForm.builder().categoryId(0L).build()));
            model.addAttribute("categories", productService.getAllCategoriesAdmin());
            model.addAttribute("success", true);
            model.addAttribute("message", "Product added/updated successfully");
            return "Admin-Product";

        } catch (Exception e) {
            model.addAttribute("products", productService.searchProductAdmin(ProductSearchForm.builder().categoryId(0L).build()));
            model.addAttribute("categories", productService.getAllCategoriesAdmin());
            model.addAttribute("success", false);
            model.addAttribute("message", e.getMessage());
            return "Admin-Product";
        }
    }

    @GetMapping("/admin/product-status-update")
    public String deleteProduct(@RequestParam(name = "productId") Long productId,
                                HttpServletRequest request,
                                HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";

        try {
            productService.productStatusUpdate(productId);
            model.addAttribute("products", productService.searchProductAdmin(ProductSearchForm.builder().categoryId(0L).build()));
            model.addAttribute("categories", productService.getAllCategoriesAdmin());
            model.addAttribute("success", true);
            model.addAttribute("message", "Product status updated successfully");
            return "Admin-Product";

        } catch (Exception e) {
            model.addAttribute("products", productService.searchProductAdmin(ProductSearchForm.builder().categoryId(0L).build()));
            model.addAttribute("categories", productService.getAllCategoriesAdmin());
            model.addAttribute("success", false);
            model.addAttribute("message", e.getMessage());
            return "Admin-Product";
        }
    }

    @GetMapping("/admin/get-active-orders")
    public String getActiveOrders(HttpServletRequest request, HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateAdmin(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";

        model.addAttribute("orders", productService.getActiveOrders());
        model.addAttribute("categories", productService.getAllCategories());
        return "Admin-Active-Orders";
    }

    @GetMapping("/admin/monthly-sales")
    @ResponseBody
    public List<MonthlySale> getMonthlySales(HttpServletRequest request, HttpSession httpSession, HttpServletResponse response, Model model) {
        return productService.getMonthlySales();
    }

    @PostMapping("/admin-update-order-status")
    public String updateOrderStatus(@ModelAttribute(name = "UpdateOrderStatusForm") UpdateOrderStatusForm updateOrderStatusForm,
                                    HttpServletRequest request,
                                    HttpSession httpSession, HttpServletResponse response, Model model) {
        UserDto userDto = authService.authenticateUser(request, httpSession);
        if (userDto != null && userDto.getRole().equalsIgnoreCase("CUSTOMER"))
            return "redirect:/";

        try {
            productService.updateOrderStatus(updateOrderStatusForm);
            return "redirect:/admin/get-active-orders";

        } catch (Exception e) {
            return "redirect:/admin/get-active-orders";
        }
    }


}