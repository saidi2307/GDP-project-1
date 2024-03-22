package com.team01.wholeSaleSystem.service;

import org.springframework.web.multipart.MultipartFile;

import com.team01.wholeSaleSystem.dto.auth.UserDto;
import com.team01.wholeSaleSystem.dto.forms.*;
import com.team01.wholeSaleSystem.dto.helper.CartData;
import com.team01.wholeSaleSystem.dto.helper.MonthlySale;
import com.team01.wholeSaleSystem.dto.helper.OrderData;
import com.team01.wholeSaleSystem.entity.*;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    List<Category> getAllCategoriesAdmin();
    List<Category> getAllCategories();

    List<Product> getProductByCategory(Long categoryId);

    List<Product> searchProduct(ProductSearchForm productSearchForm);
    List<Product> searchProductAdmin(ProductSearchForm productSearchForm);

    Cart getCart(Long id);

    CartData getCartData(Long id);

    void addToCart(UserDto userDto, AddToCartForm addToCartForm);

    void deleteCartDetail(Long id);

    void decrementCartDetail(Long id);

    void incrementCartDetail(Long id);

    OrderData createOrder(Long id);

    void confirmOrder(ConfirmOrderForm confirmOrderForm);

    List<Order> getOrders(Long id);

    Category addCategory(AddCategoryForm addCategoryForm, MultipartFile file) throws IOException;
    Product addProduct(AddProductForm addProductForm, MultipartFile file) throws IOException;

    void categoryStatusUpdate(Long categoryId);

    void productStatusUpdate(Long productId);

    List<OrderData> getActiveOrders();

    List<MonthlySale> getMonthlySales();

    void updateOrderStatus(UpdateOrderStatusForm updateOrderStatusForm);
    
    void deleteOrder(UserDto userDto, DeleteOrderForm deleteOrderForm);
}
