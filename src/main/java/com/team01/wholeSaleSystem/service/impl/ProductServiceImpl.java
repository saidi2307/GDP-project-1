package com.team01.wholeSaleSystem.service.impl;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.team01.wholeSaleSystem.dto.auth.UserDto;
import com.team01.wholeSaleSystem.dto.forms.*;
import com.team01.wholeSaleSystem.dto.helper.CartData;
import com.team01.wholeSaleSystem.dto.helper.MonthlySale;
import com.team01.wholeSaleSystem.dto.helper.OrderData;
import com.team01.wholeSaleSystem.entity.*;
import com.team01.wholeSaleSystem.repository.*;
import com.team01.wholeSaleSystem.service.ProductService;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailsRepository cartDetailsRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    @Override
    public List<Category> getAllCategoriesAdmin() {
        return categoryRepository.findAllCategoriesAdmin();
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAllCategories();
    }

    @Override
    @SneakyThrows
    public List<Product> getProductByCategory(Long categoryId) {
        return productRepository.findByCategory(categoryRepository.findById(categoryId).orElseThrow(() -> new Exception("Data not found")));
    }

    @Override
    @SneakyThrows
    public List<Product> searchProduct(ProductSearchForm productSearchForm) {
        if (productSearchForm.getCategoryId() != 0L) {
            if (StringUtils.isEmpty(productSearchForm.getProductName())) {
                return getProductByCategory(productSearchForm.getCategoryId()).stream().filter(Product::getActive).collect(Collectors.toList());
            }
            return productRepository.findByCategoryAndProductName(categoryRepository.findById(productSearchForm.getCategoryId())
                    .orElseThrow(() -> new Exception("Data not found")), productSearchForm.getProductName()).stream().filter(Product::getActive).collect(Collectors.toList());
        }
        if (StringUtils.isEmpty(productSearchForm.getProductName())) {
            return productRepository.findAllProducts().stream().filter(Product::getActive).collect(Collectors.toList());
        }
        return productRepository.findByProductName(productSearchForm.getProductName()).stream().filter(Product::getActive).collect(Collectors.toList());
    }

    @Override
    @SneakyThrows
    public List<Product> searchProductAdmin(ProductSearchForm productSearchForm) {
        if (productSearchForm.getCategoryId() != 0L) {
            if (StringUtils.isEmpty(productSearchForm.getProductName())) {
                return getProductByCategory(productSearchForm.getCategoryId());
            }
            return productRepository.findByCategoryAndProductName(categoryRepository.findById(productSearchForm.getCategoryId())
                    .orElseThrow(() -> new Exception("Data not found")), productSearchForm.getProductName());
        }
        if (StringUtils.isEmpty(productSearchForm.getProductName())) {
            return productRepository.findAllProducts();
        }
        return productRepository.findByProductName(productSearchForm.getProductName());
    }

    @Override
    public Cart getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null)
            cart = cartRepository.save(Cart.builder().userId(userId).build());
        return cart;
    }

    @Override
    public CartData getCartData(Long userId) {
        Cart cart = getCart(userId);
        List<CartDetails> cartDetails = cartDetailsRepository.getCartDetailsByCart(cart);

        float sum = 0;
        for (CartDetails cartDetail : cartDetails) {
            sum += cartDetail.getPrice();
        }

        return CartData.builder()
                .cart(cart)
                .cartDetails(cartDetails)
                .totalAmount(sum)
                .numberOfItems(cartDetails.size())
                .build();
    }

    @Override
    public void addToCart(UserDto userDto, AddToCartForm addToCartForm) {
        Cart cart = getCart(userDto.getId());
        Product product = productRepository.findById(addToCartForm.getProductId()).get();
        cartDetailsRepository.save(CartDetails.builder()
                .product(product)
                .quantity(addToCartForm.getQuantity())
                .price(addToCartForm.getQuantity() * product.getPrice())
                .cart(cart)
                .build());
    }

    @Override
    public void deleteCartDetail(Long id) {
        cartDetailsRepository.deleteById(id);
    }

    @Override
    public void incrementCartDetail(Long id) {

        CartDetails cartDetails = cartDetailsRepository.findById(id).get();
        if (cartDetails.getQuantity() < cartDetails.getProduct().getQuantity()) {
            cartDetails.setQuantity(cartDetails.getQuantity() + 1);
            cartDetails.setPrice(cartDetails.getProduct().getPrice() * cartDetails.getQuantity());
            cartDetailsRepository.save(cartDetails);
        }
    }

    @Override
    public void decrementCartDetail(Long id) {
        CartDetails cartDetails = cartDetailsRepository.findById(id).get();
        if (cartDetails.getQuantity() > 1) {
            cartDetails.setQuantity(cartDetails.getQuantity() - 1);
            cartDetails.setPrice(cartDetails.getProduct().getPrice() * cartDetails.getQuantity());
            cartDetailsRepository.save(cartDetails);
        }
    }

    @Override
    @SneakyThrows
    public OrderData createOrder(Long userId) {
        CartData cartData = getCartData(userId);
        List<OrderDetails> orderDetails = new ArrayList<>();
        Order order = Order.builder()
                .user(userRepository.findById(userId)
                        .orElseThrow(() -> new Exception("Invalid user")))
                .orderDate(new Timestamp(Instant.now().toEpochMilli()))
                .price(cartData.getTotalAmount())
                .orderStatus("Pending")
                .paymentStatus("Pending")
                .build();

        order = orderRepository.save(order);

        for (CartDetails cartDetails : cartData.getCartDetails()) {
            orderDetails.add(OrderDetails.builder()
                    .order(order)
                    .price(cartDetails.getPrice())
                    .quantity(cartDetails.getQuantity())
                    .product(cartDetails.getProduct()).build());
        }
        orderDetailsRepository.saveAll(orderDetails);

        Cart cart = cartData.getCart();
        cart.setPrice(0);
        cartRepository.save(cart);
        cartDetailsRepository.deleteAll(cartData.getCartDetails());

        return OrderData.builder()
                .order(order)
                .orderDetails(orderDetails)
                .totalAmount(order.getPrice())
                .numberOfItems(orderDetails.size())
                .paymentMethod(order.getPaymentMethod())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .orderPlacedTime(order.getOrderDate())
                .build();
    }

    @Override
    @SneakyThrows
    public void confirmOrder(ConfirmOrderForm confirmOrderForm) {

        Order order = orderRepository.findById(confirmOrderForm.getOrderId()).orElseThrow(() -> new Exception("No data found"));
        order.setName(confirmOrderForm.getName());
        order.setAddress(confirmOrderForm.getAddress());
        order.setPaymentMethod(confirmOrderForm.getPaymentMethod().trim());
        if(confirmOrderForm.getPaymentMethod().trim().equalsIgnoreCase("Credit Card")) {
        	order.setCreditCardNumber(confirmOrderForm.getCreditCardNumber());
            order.setExpiryDate(confirmOrderForm.getExpiryDate());
            order.setPaymentStatus("Successful");
            order.setOrderStatus("Placed");
        }

        orderRepository.save(order);

        List<OrderDetails> orderDetails = orderDetailsRepository.findAllByOrder(order);
        orderDetails.forEach(orderDetail -> {
            int quantity = orderDetail.getQuantity();
            Product product = orderDetail.getProduct();
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
        });
    }

    @Override
    @SneakyThrows
    public List<Order> getOrders(Long userId) {
        return orderRepository.findByUser(userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Invalid user")));
    }

    @Override
    @SneakyThrows
    public Category addCategory(AddCategoryForm addCategoryForm, MultipartFile file) throws IOException {
        Category category;
        if (StringUtils.isEmpty(addCategoryForm.getOldCategoryName()))
            category = new Category();
        else category = categoryRepository.findByCategoryName(addCategoryForm.getOldCategoryName()).get();

        category.setCategoryName(addCategoryForm.getName());
        category.setActive(true);

        if (!file.isEmpty())
            category.setImage(file.getBytes());
        else if (StringUtils.isEmpty(addCategoryForm.getOldCategoryName()))
        	throw new Exception("Please select image");

        return categoryRepository.save(category);
    }

    @Override
    @SneakyThrows
    public Product addProduct(AddProductForm addProductForm, MultipartFile file) throws IOException {
        Category category = categoryRepository.findById(addProductForm.getProductCategoryId()).get();
        Product product = null;
        if (StringUtils.isEmpty(addProductForm.getOldProductName()))
            product = new Product();
        else product = productRepository.findFirstByProductName(addProductForm.getOldProductName());

        product.setProductName(addProductForm.getName());
        product.setDescription(addProductForm.getDescription());
        product.setCategory(category);
        product.setPrice(addProductForm.getPrice());
        product.setQuantity(addProductForm.getQuantity());
        product.setActive(true);

        if (!file.isEmpty())
            product.setImage(file.getBytes());
        else if(StringUtils.isEmpty(addProductForm.getOldProductName()))
        	throw new Exception("Please select image");
        
        return productRepository.save(product);
    }

    @Override
    public void categoryStatusUpdate(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).get();
        category.setActive(!category.getActive());
        categoryRepository.save(category);
    }

    @Override
    public void productStatusUpdate(Long productId) {
        Product product = productRepository.findById(productId).get();
        product.setActive(!product.getActive());
        productRepository.save(product);
    }

    @Override
    public List<OrderData> getActiveOrders() {
        List<OrderData> orderDataList = new ArrayList<>();
        List<Order> orders = orderRepository.findByOrderStatusNot("Pending");

        orders.forEach(order -> {
            List<OrderDetails> orderDetails = orderDetailsRepository.findAllByOrder(order);
            orderDataList.add(OrderData.builder()
                    .order(order)
                    .orderDetails(orderDetails)
                    .totalAmount(order.getPrice())
                    .numberOfItems(orderDetails.size())
                    .paymentMethod(order.getPaymentMethod())
                    .orderStatus(order.getOrderStatus())
                    .paymentStatus(order.getPaymentStatus())
                    .orderPlacedTime(order.getOrderDate())
                    .build());
        });
        return orderDataList;
    }

    @Override
    public List<MonthlySale> getMonthlySales() {
        List<Object[]> resultList = orderRepository.getMonthlySalesData();
        List<MonthlySale> monthlySales = new ArrayList<>();

        for (Object[] result : resultList) {
            int month = (int) result[0];
            double totalAmount = Double.parseDouble(result[1].toString());
            Long numberOfOrders = (Long) result[2];
            monthlySales.add(new MonthlySale(month, totalAmount, numberOfOrders));
        }

        return monthlySales;
    }

    @Override
    @SneakyThrows
    public void updateOrderStatus(UpdateOrderStatusForm updateOrderStatusForm) {
        Order o = orderRepository.findById(updateOrderStatusForm.getOrderId())
                .orElseThrow(() -> new Exception("Invalid order id"));
        o.setOrderStatus(updateOrderStatusForm.getOrderStatus());
        orderRepository.save(o);
    }
    
    @Override
    @SneakyThrows
    public void deleteOrder(UserDto userDto, DeleteOrderForm deleteOrderForm){
        Order o = orderRepository.findById(deleteOrderForm.getOrderId())
                .orElseThrow(() -> new Exception("Invalid order id"));
        orderRepository.delete(o);
    }

}
