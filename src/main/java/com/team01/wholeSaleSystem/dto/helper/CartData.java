package com.team01.wholeSaleSystem.dto.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.team01.wholeSaleSystem.entity.Cart;
import com.team01.wholeSaleSystem.entity.CartDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartData {
    private Cart cart;
    private List<CartDetails> cartDetails;
    private float totalAmount;
    private int numberOfItems;
}
