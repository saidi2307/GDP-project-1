package com.team01.wholeSaleSystem.repository;

import com.team01.wholeSaleSystem.entity.Order;
import com.team01.wholeSaleSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
    List<Order> findByOrderStatusNot(String orderStatus);

    @Query(value = "SELECT " +
            "    MONTH(order_date) AS month, " +
            "    SUM(price) AS totalAmount, " +
            "    COUNT(*) AS numberOfOrders " +
            "FROM " +
            "    orders " +
            "WHERE " +
            "    YEAR(order_date) = YEAR(CURDATE()) and order_status = 'Placed' " +
            "GROUP BY " +
            "    MONTH(order_date) ", nativeQuery = true)
    List<Object[]> getMonthlySalesData();
}
