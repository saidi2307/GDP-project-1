package com.team01.wholeSaleSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team01.wholeSaleSystem.entity.Cart;
import com.team01.wholeSaleSystem.entity.CartDetails;

import java.util.List;

@Repository
public interface CartDetailsRepository extends JpaRepository<CartDetails, Long> {

    List<CartDetails> getCartDetailsByCart(Cart cart);
}
